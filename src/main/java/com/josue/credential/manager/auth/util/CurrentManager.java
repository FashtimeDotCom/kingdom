/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.auth.util;

import com.josue.credential.manager.auth.credential.Credential;
import com.josue.credential.manager.business.account.AccountRepository;
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
    AccountRepository repository;

    @Produces
    @Current
    @SessionScoped
    //TODO WELD-000052: Cannot return null from a non-dependent producer method: Producer for Producer Method
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
