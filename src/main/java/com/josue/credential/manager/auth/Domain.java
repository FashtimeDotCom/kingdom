/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.auth;

import com.josue.credential.manager.Resource;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author Josue
 */
@Entity
public class Domain extends Resource {

    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private DomainStatus status;

//    //TODO other properties
    @ManyToOne
    @JoinColumn(name = "owner_credential_uuid")
    private ManagerCredential owner;

    @OneToMany(mappedBy = "domain", cascade = CascadeType.ALL)//TODO check cascade
    private Set<ManagerDomainCredential> domainManagers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DomainStatus getStatus() {
        return status;
    }

    public void setStatus(DomainStatus status) {
        this.status = status;
    }

    public ManagerCredential getOwner() {
        return owner;
    }

    public void setOwner(ManagerCredential owner) {
        this.owner = owner;
    }

    public Set<ManagerDomainCredential> getDomainManagers() {
        return domainManagers;
    }

    public void setDomainManagers(Set<ManagerDomainCredential> domainManagers) {
        this.domainManagers = domainManagers;
    }

}
