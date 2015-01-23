/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.domain.entity;

import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.rest.TenantResource;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author Josue
 */
@Entity
@Table(name = "manager_membership", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"domain_uuid", "manager_uuid", "application_uuid"})})
//http://stackoverflow.com/questions/5127129/mapping-many-to-many-association-table-with-extra-columns
public class ManagerMembership extends TenantResource {

    @OneToOne(optional = false)
    private Domain domain;

    @OneToOne(optional = false)
    private Manager manager;

    @OneToOne(optional = false)
    private DomainPermission permission;

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public DomainPermission getPermission() {
        return permission;
    }

    public void setPermission(DomainPermission permission) {
        this.permission = permission;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.domain);
        hash = 11 * hash + Objects.hashCode(this.manager);
        hash = 11 * hash + Objects.hashCode(this.permission);
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
        final ManagerMembership other = (ManagerMembership) obj;
        if (!Objects.equals(this.domain, other.domain)) {
            return false;
        }
        if (!Objects.equals(this.manager, other.manager)) {
            return false;
        }
        return Objects.equals(this.permission, other.permission);
    }

}
