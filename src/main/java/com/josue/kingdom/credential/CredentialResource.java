/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.credential;

import com.josue.kingdom.credential.entity.Credential;
import com.josue.kingdom.rest.ResponseUtils;
import static com.josue.kingdom.rest.ResponseUtils.CONTENT_TYPE;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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

    /*
     returns the ManagerCredential fot the login
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
}
