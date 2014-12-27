/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.account;

import com.josue.credential.manager.auth.Domain;
import com.josue.credential.manager.auth.DomainCredential;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
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
    AccountControl control;

    private List<Domain> ownedDomains;
    private List<DomainCredential> joinedDomains;

    @PostConstruct
    public void init() {
        this.ownedDomains = control.getOwnedDomains();
        this.joinedDomains = control.getJoinedDomains();
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

}
