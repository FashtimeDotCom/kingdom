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
import com.josue.kingdom.rest.ex.RestException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
@Path("account")
@ApplicationScoped
public class AccountRest {

    @Context
    UriInfo info;

    @Inject
    AccountControl control;

    /*
     returns the current account details
     */
    @GET
    @Produces(value = CONTENT_TYPE)
    public Response current() {
        Subject subject = SecurityUtils.getSubject();
        //TODO remove password - login / apikey
        Credential credential = (Credential) subject.getPrincipal();
        return ResponseUtils.buildSimpleResponse(credential, Response.Status.OK, info);
    }

    //TODO verb ??? really ?.... move or update this
    //This methos is not authenticated by Shiro
    @GET
    @Path("recover")
    public Response passwordRecovery(@QueryParam("email") String email) throws RestException {

        control.passwordRecovery(email);
        return ResponseUtils.buildSimpleResponse(null, Response.Status.OK, info);
    }

    //TODO chould move to another module package ? rest refactor is needed
    @POST
    @Produces(value = CONTENT_TYPE)
    @Consumes(value = CONTENT_TYPE)
    //TODO change reponse STATUS to CREATED from POST resources
    public Response create(@QueryParam("token") String token, ManagerCredential managerCredential) {
        ManagerCredential createdCredential = control.create(token, managerCredential);
        return ResponseUtils.buildSimpleResponse(createdCredential, Response.Status.CREATED, info);
    }

}
