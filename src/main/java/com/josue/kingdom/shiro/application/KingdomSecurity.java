/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.shiro.application;

import com.josue.kingdom.application.entity.Application;
import com.josue.kingdom.credential.entity.Manager;

/**
 *
 * @author Josue
 */
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

    public Manager getCurrentManager() {
        return currentManager;
    }

}
