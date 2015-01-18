/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.credential;

import com.josue.kingdom.credential.entity.Credential;
import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.credential.entity.ManagerCredential;
import com.josue.kingdom.rest.ResponseUtils;
import static com.josue.kingdom.rest.ResponseUtils.CONTENT_TYPE;
import com.josue.kingdom.rest.ex.RestException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 *
 * @author Josue
 */
@ApplicationScoped
@Path("credentials")
public class CredentialResource {

    @Context
    UriInfo info;

    @Inject
    CredentialControl control;

    /*
     returns the ManagerCredential for the login
     */
    @GET
    @Path("current")
    @Produces(value = CONTENT_TYPE)
    public Response getCurrentCredential() {
        Subject subject = SecurityUtils.getSubject();
        //TODO remove password - login / apikey
        Credential credential = (Credential) subject.getPrincipal();
        return ResponseUtils.buildSimpleResponse(credential, Response.Status.OK, info);
    }

    @GET
    @Path("{login}")
    @Produces(value = CONTENT_TYPE)
    public Response getAccount(@PathParam("login") String login) throws RestException {
        Manager managerBylogin = control.getManagerBylogin(login);
        return ResponseUtils.buildSimpleResponse(managerBylogin, Response.Status.OK, info);
    }

    @GET
    @Path("{login}/password-reset")
    public Response passwordReset(@QueryParam("login") String login) throws RestException {
        control.passwordRecovery(login);
        return ResponseUtils.buildSimpleResponse(null, Response.Status.OK, info);
    }

    @GET
    @Path("{email}/login-recover")
    //TODO implement
    public Response loginRecover(@QueryParam("email") String email) throws RestException {

        throw new RuntimeException("*** NOT IMPLEMENTED YET ***");
//        control.passwordRecovery(login);
//        return ResponseUtils.buildSimpleResponse(null, Response.Status.OK, info);
    }

    //TODO move to CredentialResource or keep it here ?
    @POST
    @Produces(value = CONTENT_TYPE)
    @Consumes(value = CONTENT_TYPE)
    public Response createAccount(@QueryParam("token") String token, ManagerCredential managerCredential) throws RestException {
        ManagerCredential createdCredential = control.createCredential(token, managerCredential);
        return ResponseUtils.buildSimpleResponse(createdCredential, Response.Status.CREATED, info);
    }
}
