/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.account;

import com.josue.credential.manager.auth.role.Role;
import com.josue.credential.manager.auth.shiro.AccessLevelPermission;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 * REST Web Service
 *
 * @author Josue
 */
@Path("account")
@RequestScoped
public class AccountRest {

    private static final Logger LOG = Logger.getLogger(AccountRest.class.getName());

    @Context
    private UriInfo context;

    @Inject
    AccountService control;

    @GET
    @Produces("text/plain")
    public String getAccount() {
        Subject subject = SecurityUtils.getSubject();
        LOG.log(Level.INFO, subject.getPrincipal().toString());
        //domain: 70f4b4b0-18d2-4707-824b-b30af193d99b

        if (subject.isPermitted(new AccessLevelPermission("70f4b4b0-18d2-4707-824b-b30af193d99b", new Role(1)))) {
            LOG.log(Level.INFO, "*** HAS ACCESS Role 1***");
        }
        if (subject.isPermitted(new AccessLevelPermission("AAAAAA", new Role(1)))) {
            LOG.log(Level.INFO, "*** HAS ACCESS ***");
        }
        if (subject.isPermitted(new AccessLevelPermission("70f4b4b0-18d2-4707-824b-b30af193d99b", new Role(2)))) {
            LOG.log(Level.INFO, "*** HAS ACCESS Role2 ***");
        }
        return "OK";
    }

}
