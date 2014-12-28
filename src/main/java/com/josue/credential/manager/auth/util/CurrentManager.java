/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.auth.util;

import com.josue.credential.manager.business.account.AccountRepository;
import com.josue.credential.manager.auth.manager.Manager;
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
    public Manager getCurrentManager() {
        Subject subject = SecurityUtils.getSubject();
        Manager foundManager = repository.getManagerByCredential(subject.getPrincipal().toString());
        return foundManager;
    }

}
