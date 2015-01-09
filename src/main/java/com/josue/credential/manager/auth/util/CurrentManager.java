/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.auth.util;

import com.josue.credential.manager.auth.credential.Credential;
import com.josue.credential.manager.business.credential.CredentialRepository;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 *
 * @author Josue
 */
@ApplicationScoped
public class CurrentManager {

    @Inject
    CredentialRepository repository;

    @Produces
    @Current
    @SessionScoped
    public Credential currentCredential() {
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            if (subject.getPrincipal() instanceof Credential) {
                return (Credential) subject.getPrincipal();
            }
        }
        throw new RuntimeException("Could not load credential, check for credential type");
    }

}
