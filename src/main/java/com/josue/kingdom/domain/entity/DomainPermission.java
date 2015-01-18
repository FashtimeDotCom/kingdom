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
@Table(name = "domain_permission", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"level", "domain_uuid"}),
    @UniqueConstraint(columnNames = {"name", "domain_uuid"})})
//TODO extend from Resource
public class DomainPermission extends Resource {

    public DomainPermission() {
    }

    public DomainPermission(int level) {
        this.level = level;
    }

    private Integer level;
    private String name;
    private String description;

    @OneToOne
    @JoinColumn(name = "domain_uuid")
    private Domain domain;

    @Override
    public void copyUpdatable(Resource newData) {
        if (newData instanceof DomainPermission) {
            DomainPermission permission = (DomainPermission) newData;

            name = permission.name == null ? name : permission.name;
            description = permission.description == null ? description : permission.description;
            level = permission.level == null ? level : permission.level;

            domain.copyUpdatable(permission.domain);
        }
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
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
        final DomainPermission other = (DomainPermission) obj;
        if (!Objects.equals(this.level, other.level)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        return Objects.equals(this.domain, other.domain);
    }

}
