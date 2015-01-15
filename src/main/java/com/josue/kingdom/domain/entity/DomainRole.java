/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.domain.entity;

import com.josue.kingdom.rest.Resource;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author Josue
 */
@Entity
@Table(name = "domain_role", uniqueConstraints = @UniqueConstraint(columnNames = {"level", "domain_uuid"}))
//TODO extend from Resource
public class DomainRole extends Resource {

    public DomainRole() {
    }

    public DomainRole(int level) {
        this.level = level;
    }

    private int level;
    private String name;
    private String description;

    @OneToOne
    @JoinColumn(name = "domain_uuid")
    private Domain domain;

    @Override
    protected void copyUpdatebleFields(Resource newData) {
        if (newData instanceof DomainRole) {
            DomainRole role = (DomainRole) newData;

            name = role.name == null ? name : role.name;
            description = role.description == null ? description : role.description;

            domain.copyUpdatebleFields(role.domain);
        }
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

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

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + this.level;
        hash = 47 * hash + Objects.hashCode(this.name);
        hash = 47 * hash + Objects.hashCode(this.description);
        hash = 47 * hash + Objects.hashCode(this.domain);
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
        final DomainRole other = (DomainRole) obj;
        if (this.level != other.level) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.domain, other.domain)) {
            return false;
        }
        return true;
    }

}
