/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.auth;

import com.josue.credential.manager.Resource;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Josue
 */
@Entity
@Table(name = "manager_domain_credential")
//http://stackoverflow.com/questions/5127129/mapping-many-to-many-association-table-with-extra-columns
public class ManagerDomainCredential extends Resource {

    @ManyToOne
    @JoinColumn(name = "domain_uuid")
    private Domain domain;

    @ManyToOne
    @JoinColumn(name = "credential_uuid")
    private ManagerCredential credential;

    //Role for this domain
    @OneToOne
    @JoinColumn(name = "domain_role_uuid")
    private Role role;

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public ManagerCredential getCredential() {
        return credential;
    }

    public void setCredential(ManagerCredential credential) {
        this.credential = credential;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

}
