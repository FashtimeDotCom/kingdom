/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.account;

import com.josue.credential.manager.auth.Domain;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.ValueChangeEvent;
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
    private String selectedDomain;
    private List<String> domains;

    @Inject
    AccountControl control;

    @PostConstruct
    public void init() {
        Subject currentUser = SecurityUtils.getSubject();
        LOG.log(Level.INFO, "LOGGED AS {0} ON SESSIONID {1}", new Object[]{currentUser.getPrincipal(), currentUser.getSession().getId()});

        fetchLoggedManager();
        fetchDomains();

    }

    private void fetchDomains() {
        Subject currentUser = SecurityUtils.getSubject();
        List<Domain> foundDomains = control.getManagerDomains(currentUser.getPrincipal().toString());

        domains = new ArrayList<>();
        for (Domain d : foundDomains) {
            domains.add(d.getName());
            selectedDomain = d.getName();
        }

    }

    private void fetchLoggedManager() {
        Subject currentUser = SecurityUtils.getSubject();
        manager = control.getManagerByCredential(currentUser.getPrincipal().toString());
    }

    public String logout() {

        Subject currentUser = SecurityUtils.getSubject();
        try {
            currentUser.logout();
        } catch (Exception e) {
            LOG.warning(e.toString());
        }
        return "/login.xhtml?faces-redirect=true";
    }

    public void updateDomain(ValueChangeEvent event) {
        LOG.log(Level.INFO, "TRYING TO CHANGE TO DOMAIN {0}", event.getNewValue());
        for (String domain : domains) {
            if (domain.equals(event.getNewValue())) {
                selectedDomain = domain;
                LOG.log(Level.INFO, "CHANGING TO DOMAIN {0}", event.getNewValue());
            }
        }
    }

    public String getSelectedDomain() {
        return selectedDomain;
    }

    public void setSelectedDomain(String selectedDomain) {
        this.selectedDomain = selectedDomain;
    }

    public List<String> getDomains() {
        return domains;
    }

    public void setDomains(List<String> domains) {
        this.domains = domains;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }
}
