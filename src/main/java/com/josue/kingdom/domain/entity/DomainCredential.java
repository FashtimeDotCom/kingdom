/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.domain.entity;

import com.josue.kingdom.rest.Resource;
import java.util.Objects;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

/**
 *
 * @author Josue
 */
@MappedSuperclass
public abstract class DomainCredential extends Resource {

    @ManyToOne
    @JoinColumn(name = "domain_uuid")
    private Domain domain;

    //Role for this domain
    @OneToOne
    @JoinColumn(name = "domain_role_uuid")
    private DomainRole role;

    @Override
    protected void copyUpdatebleFields(Resource newData) {
        if (newData instanceof DomainCredential) {
            DomainCredential domainCredential = (DomainCredential) newData;
            domain.copyUpdatebleFields(domainCredential.domain);

            role = domainCredential.role == null ? role : domainCredential.role;
        }
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public DomainRole getRole() {
        return role;
    }

    public void setRole(DomainRole role) {
        this.role = role;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.domain);
        hash = 41 * hash + Objects.hashCode(this.role);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DomainCredential other = (DomainCredential) obj;
        if (!Objects.equals(this.domain, other.domain)) {
            return false;
        }
        if (!Objects.equals(this.role, other.role)) {
            return false;
        }
        return true;
    }

}
