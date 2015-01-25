/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.security;

import com.josue.kingdom.application.entity.Application;
import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.rest.ex.HeaderRequiredException;
import com.josue.kingdom.rest.ex.RestException;
import com.josue.kingdom.security.application.ApplicationFilter;
import javax.ws.rs.core.Response;

/**
 *
 * @author Josue
 */
//TODO check if its needed add aditional values to this class
public class KingdomSecurity {

    public static enum ManagerStatus {

        AUTHENTICATED, EMPTY, UNAUTHENTICATED
    }

    private final Application currentApplication;
    private final Manager currentManager;
    private final ManagerStatus managerStatus;

    public KingdomSecurity() {
        currentApplication = null;
        currentManager = null;
        managerStatus = ManagerStatus.EMPTY;
    }

    public KingdomSecurity(Application currentApplication, Manager currentManager, ManagerStatus managerStatus) {
        this.currentApplication = currentApplication;
        this.currentManager = currentManager;
        this.managerStatus = managerStatus;
    }

    public Application getCurrentApplication() {
        return currentApplication;
    }

    //As manager should not be validate on shiro auth, when getting the current manager, it shouldnt be null, to avoid NPE
    public Manager getCurrentManager() throws RestException {
        if (managerStatus.equals(ManagerStatus.EMPTY)) {
            throw new HeaderRequiredException(ApplicationFilter.KINGDOM_HEADER);
        } else if (managerStatus.equals(ManagerStatus.UNAUTHENTICATED)) {
            throw new RestException(null, null, "Invalid Manager credentials, check '" + ApplicationFilter.KINGDOM_HEADER + "' header", Response.Status.BAD_REQUEST);
        }
        return currentManager;
    }

}
