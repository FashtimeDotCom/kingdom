/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.domain.entity;

import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.rest.Resource;
import com.josue.kingdom.rest.TenantResource;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Josue
 */
@Entity
@Table(name = "domain", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"application_uuid", "uuid", "owner_uuid"})})
public class Domain extends TenantResource {

    private String name;
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DomainStatus status = DomainStatus.ACTIVE;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_uuid")
    private Manager owner;

    @Override
    public void copyUpdatable(Resource newData) {
        if (newData instanceof Domain) {
            Domain domain = (Domain) newData;
            description = domain.description == null ? description : domain.description;
            status = domain.status == null ? status : domain.status;
        }
    }

    @Override
    public void removeNonCreatable() {
        super.removeNonCreatable();
        this.owner = null;
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

    public DomainStatus getStatus() {
        return status;
    }

    public void setStatus(DomainStatus status) {
        this.status = status;
    }

    public Manager getOwner() {
        return owner;
    }

    public void setOwner(Manager owner) {
        this.owner = owner;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.name);
        hash = 89 * hash + Objects.hashCode(this.description);
        hash = 89 * hash + Objects.hashCode(this.status);
        hash = 89 * hash + Objects.hashCode(this.owner);
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
        final Domain other = (Domain) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (this.status != other.status) {
            return false;
        }
        if (!Objects.equals(this.owner, other.owner)) {
            return false;
        }
        return true;
    }

}
