/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.account;

import com.josue.credential.manager.auth.credential.Credential;
import com.josue.credential.manager.rest.ResponseUtils;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
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

    /*
     returns the current account details
     */
    @GET
    public Response current() {
        Subject subject = SecurityUtils.getSubject();
        //TODO remove password - login / apikey
        Credential credential = (Credential) subject.getPrincipal();
        return ResponseUtils.buildSimpleResponse(credential, Response.Status.OK, info);
    }

}
