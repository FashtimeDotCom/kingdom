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
    @JoinColumn(name = "domain_permission_uuid")
    private DomainPermission permission;

    @Override
    protected void copyUpdatable(Resource newData) {
        if (newData instanceof DomainCredential) {
            DomainCredential domainCredential = (DomainCredential) newData;
            domain.copyUpdatable(domainCredential.domain);

            permission = domainCredential.permission == null ? permission : domainCredential.permission;
        }
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public DomainPermission getRole() {
        return permission;
    }

    public void setRole(DomainPermission permission) {
        this.permission = permission;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.domain);
        hash = 41 * hash + Objects.hashCode(this.permission);
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
        if (!Objects.equals(this.permission, other.permission)) {
            return false;
        }
        return true;
    }

}
