/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.rest;

import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 *
 * @author Josue
 */
@ApplicationPath("api/v1")
public class JaxrsApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method. It is automatically
     * populated with all resources defined in the project. If required, comment
     * out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(com.josue.kingdom.credential.APICredentialResource.class);
        resources.add(com.josue.kingdom.credential.APICredentialSubResource.class);
        resources.add(com.josue.kingdom.credential.LoginAttemptResource.class);
        resources.add(com.josue.kingdom.credential.ManagerResource.class);
        resources.add(com.josue.kingdom.domain.DomainPermissionSubResource.class);
        resources.add(com.josue.kingdom.domain.DomainResource.class);
        resources.add(com.josue.kingdom.invitation.InvitationResource.class);
        resources.add(com.josue.kingdom.rest.CustomJacksonProvider.class);
        resources.add(com.josue.kingdom.rest.VersionRest.class);
        resources.add(com.josue.kingdom.rest.ex.RestExceptionMapper.class);
    }

}
