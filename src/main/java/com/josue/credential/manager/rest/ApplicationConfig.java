/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.rest;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author Josue
 */
@javax.ws.rs.ApplicationPath("api")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
//        resources.add(com.josue.credential.manager.business.role.RoleRest.class);
//        resources.add(com.josue.credential.manager.business.credential.CredentialRest.class);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method. It is automatically
     * populated with all resources defined in the project. If required, comment
     * out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(com.josue.credential.manager.business.account.AccountRest.class);
        resources.add(com.josue.credential.manager.business.credential.CredentialRest.class);
        resources.add(com.josue.credential.manager.business.domain.DomainRest.class);
        resources.add(com.josue.credential.manager.business.role.RoleRest.class);
        resources.add(com.josue.credential.manager.rest.CustomJacksonProvider.class);
        resources.add(com.josue.credential.manager.rest.VersionRest.class);
        resources.add(com.josue.credential.manager.rest.ex.RestExceptionMapper.class);
    }

}
