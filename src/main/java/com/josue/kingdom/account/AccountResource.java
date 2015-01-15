/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.account;

import com.josue.kingdom.credential.entity.Credential;
import com.josue.kingdom.credential.entity.ManagerCredential;
import com.josue.kingdom.rest.ResponseUtils;
import static com.josue.kingdom.rest.ResponseUtils.CONTENT_TYPE;
import static com.josue.kingdom.rest.ResponseUtils.DEFAULT_LIMIT;
import static com.josue.kingdom.rest.ResponseUtils.DEFAULT_OFFSET;
import com.josue.kingdom.rest.ex.RestException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
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
@Path("accounts")
@ApplicationScoped
public class AccountResource {
    //TODO change reponse STATUS to CREATED from all POST resources

    @Context
    UriInfo info;

    @Inject
    AccountControl control;

    @GET
    @Produces(value = CONTENT_TYPE)
    //Return all system managers
    //TODO check access, is it open ?
    public Response getAccounts(@QueryParam("limit") @DefaultValue(DEFAULT_LIMIT) Integer limit,
            @QueryParam("offset") @DefaultValue(DEFAULT_OFFSET) Integer offset) {
        Subject subject = SecurityUtils.getSubject();
        Credential credential = (Credential) subject.getPrincipal();
        return ResponseUtils.buildSimpleResponse(credential, Response.Status.OK, info);
    }

    /*
     returns the Manager fot the login
     */
    @GET
    @Path("{login}")
    @Produces(value = CONTENT_TYPE)
    public Response getAccount(@PathParam("login") String login) {
        Subject subject = SecurityUtils.getSubject();
        Credential credential = (Credential) subject.getPrincipal();
        return ResponseUtils.buildSimpleResponse(credential.getManager(), Response.Status.OK, info);
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
    public Response createAccount(@QueryParam("token") String token, ManagerCredential managerCredential) {
        ManagerCredential createdCredential = control.createCredential(token, managerCredential);
        return ResponseUtils.buildSimpleResponse(createdCredential, Response.Status.CREATED, info);
    }

}
