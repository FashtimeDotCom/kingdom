/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.credential;

import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.credential.entity.PasswordChangeEvent;
import com.josue.kingdom.credential.entity.SimpleLogin;
import com.josue.kingdom.rest.ResponseUtils;
import static com.josue.kingdom.rest.ResponseUtils.CONTENT_TYPE;
import com.josue.kingdom.rest.ex.RestException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author Josue
 */
@ApplicationScoped
@Path("managers")
public class ManagerResource {

    @Context
    UriInfo info;

    @Inject
    CredentialControl control;

    @POST
    @Path("login/attempts")
    @Produces(value = CONTENT_TYPE)
    public Response login(SimpleLogin simpleLogin) throws RestException {
        return ResponseUtils.buildSimpleResponse(control.login(simpleLogin), Response.Status.OK, info);
    }

    /*
     returns the ManagerCredential for the login
     */
    @GET
    @Path("current")
    @Produces(value = CONTENT_TYPE)
    public Response getCurrentManager() throws RestException {
        return ResponseUtils.buildSimpleResponse(control.getCurrentManager(), Response.Status.OK, info);
    }

    @GET
    @Path("{username}")//TODO username and email ?
    @Produces(value = CONTENT_TYPE)
    public Response getAccount(@PathParam("username") String username) throws RestException {
        Manager managerBylogin = control.getManagerBylogin(username);
        return ResponseUtils.buildSimpleResponse(managerBylogin, Response.Status.OK, info);
    }

    @POST
    @Path("{username}/password-request")
    public Response passwordReset(@PathParam("username") String username) throws RestException {
        control.createPasswordChangeEvent(username);
        return ResponseUtils.buildSimpleResponse(null, Response.Status.OK, info);
    }

    @PUT
    @Path("{username}/password")
    public Response updateManagerPassword(@PathParam("username") String username, PasswordChangeEvent event) throws RestException {
        control.updateManagerPassword(username, event);
        return ResponseUtils.buildSimpleResponse(null, Response.Status.OK, info);
    }

    @POST
    @Path("{email}/login-recover")
    //TODO Improve the body ? LoginRecoveryEvent just have a manager
    public Response loginRecover(@PathParam("email") String email) throws RestException {
        control.loginRecovery(email);
        return ResponseUtils.buildSimpleResponse(null, Response.Status.OK, info);
    }

    //TODO move to CredentialResource or keep it here ?
    @POST
    @Path("{token}")
    @Produces(value = CONTENT_TYPE)
    @Consumes(value = CONTENT_TYPE)
    public Response createManager(@PathParam("token") String token, Manager manager) throws RestException {
        Manager createdManager = control.createManager(token, manager);
        return ResponseUtils.buildSimpleResponse(createdManager, Response.Status.CREATED, info);
    }
}
