/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.util.cdi;

import com.josue.kingdom.credential.CredentialRepository;
import com.josue.kingdom.credential.entity.Manager;
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
    public Manager current() {
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            Manager manager = (Manager) subject;
            return manager;

        }
        throw new RuntimeException("Could not load credential, check for credential type");
    }

}
