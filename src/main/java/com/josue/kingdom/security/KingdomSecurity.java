/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.security;

import com.josue.kingdom.application.entity.Application;
import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.rest.ex.HeaderRequiredException;
import com.josue.kingdom.security.application.ApplicationFilter;

/**
 *
 * @author Josue
 */
//TODO check if its needed add aditional values to this class
public class KingdomSecurity {

    private final Application currentApplication;
    private final Manager currentManager;

    public KingdomSecurity(Application currentApplication, Manager currentManager) {
        this.currentApplication = currentApplication;
        this.currentManager = currentManager;
    }

    public Application getCurrentApplication() {
        return currentApplication;
    }

    //As manager should not be validate on shiro auth, when getting the current manager, it shouldnt be null, to avoid NPE
    public Manager getCurrentManager() throws HeaderRequiredException {
        if (currentManager == null) {
            throw new HeaderRequiredException(ApplicationFilter.KINGDOM_HEADER);
        }
        return currentManager;
    }

}
