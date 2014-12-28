/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.domain;

import com.josue.credential.manager.auth.domain.Domain;
import com.josue.credential.manager.auth.domain.DomainCredential;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Josue
 */
@Named
@RequestScoped
public class DomainBean implements Serializable {

    @Inject
    DomainControl control;

    private static final Logger LOG = Logger.getLogger(DomainBean.class.getName());

    private String selectedDomain;
    private List<String> domains;

    private List<Domain> ownedDomains;
    private List<DomainCredential> joinedDomains;

    @PostConstruct
    public void init() {
        this.ownedDomains = control.getOwnedDomains();
        this.joinedDomains = control.getJoinedDomains();

        fetchDomains();
    }

    private void fetchDomains() {
        joinedDomains = control.getJoinedDomains();

        domains = new ArrayList<>();
        for (DomainCredential d : joinedDomains) {
            domains.add(d.getDomain().getName());
            selectedDomain = d.getDomain().getName();
        }
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

    public List<Domain> getOwnedDomains() {
        return ownedDomains;
    }

    public void setOwnedDomains(List<Domain> ownedDomains) {
        this.ownedDomains = ownedDomains;
    }

    public List<DomainCredential> getJoinedDomains() {
        return joinedDomains;
    }

    public void setJoinedDomains(List<DomainCredential> joinedDomains) {
        this.joinedDomains = joinedDomains;
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

}
