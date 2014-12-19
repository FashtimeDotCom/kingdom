/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.account;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 * REST Web Service
 *
 * @author Josue
 */
@Path("account")
@RequestScoped
public class AccountRest {

    @Context
    private UriInfo context;

    @Inject
    AccountService control;

    @GET
    @Produces("text/plain")
    public String getAccount() {
        return "OK";
    }
}
