/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.account;

import com.josue.credential.manager.auth.manager.Manager;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 *
 * @author Josue
 */
@Named
@SessionScoped
public class AccountBean implements Serializable {

    private static final Logger LOG = Logger.getLogger(AccountBean.class.getName());

    private Manager manager;

    @Inject
    AccountControl control;

    @PostConstruct
    public void init() {
        Subject currentUser = SecurityUtils.getSubject();
        LOG.log(Level.INFO, "LOGGED AS {0} ON SESSIONID {1}", new Object[]{currentUser.getPrincipal(), currentUser.getSession().getId()});

        fetchLoggedManager();

    }

    private void fetchLoggedManager() {
        Subject currentUser = SecurityUtils.getSubject();
        manager = control.getManagerByCredential(currentUser.getPrincipal().toString());
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }
}
