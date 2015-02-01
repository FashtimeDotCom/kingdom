/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.invitation.entity;

import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.domain.entity.Domain;
import com.josue.kingdom.domain.entity.DomainPermission;
import com.josue.kingdom.rest.Resource;
import com.josue.kingdom.rest.TenantResource;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Josue
 */
@Entity
@Table(name = "invitation", uniqueConstraints
        = @UniqueConstraint(columnNames = {"target_manager_uuid", "domain_uuid", "application_uuid"}))
public class Invitation extends TenantResource {

    //TODO change to an pre created manager
    @OneToOne(optional = false)
    @JoinColumn(name = "target_manager_uuid")
    private Manager targetManager;

    @OneToOne(optional = false)
    @JoinColumn(name = "author_manager_uuid")
    private Manager authorManager;

    @OneToOne(optional = false)
    @JoinColumn(name = "domain_uuid")
    private Domain domain;

    @OneToOne(optional = false)
    @JoinColumn(name = "domain_permission_uuid")
    private DomainPermission permission;

    @NotNull
    private String token;

    @NotNull
    @Future
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "valid_until", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date validUntil;

    @NotNull
    @Enumerated(EnumType.STRING)
    private InvitationStatus status;

    @Column(name = "confirmation_token")
    private String confirmationToken;

    @Override
    public void removeNonCreatable() {
        super.removeNonCreatable();
        this.token = null;
        this.validUntil = null;
        this.status = null;
        this.authorManager = null;
        if (domain != null) {
            domain.removeNonCreatable();
        }
    }

    @Override
    public void copyUpdatable(Resource newData) {
        super.copyUpdatable(newData);
        if (newData instanceof Invitation) {
            Invitation invitation = (Invitation) newData;
            status = status != null ? invitation.status : status;
        }
    }

    public Manager getTargetManager() {
        return targetManager;
    }

    public void setTargetManager(Manager targetManager) {
        this.targetManager = targetManager;
    }

    public Manager getAuthorManager() {
        return authorManager;
    }

    public void setAuthorManager(Manager authorManager) {
        this.authorManager = authorManager;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public DomainPermission getPermission() {
        return permission;
    }

    public void setPermission(DomainPermission permission) {
        this.permission = permission;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Date validUntil) {
        this.validUntil = validUntil;
    }

    public InvitationStatus getStatus() {
        return status;
    }

    public void setStatus(InvitationStatus status) {
        this.status = status;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.targetManager);
        hash = 37 * hash + Objects.hashCode(this.authorManager);
        hash = 37 * hash + Objects.hashCode(this.domain);
        hash = 37 * hash + Objects.hashCode(this.permission);
        hash = 37 * hash + Objects.hashCode(this.token);
        hash = 37 * hash + Objects.hashCode(this.validUntil);
        hash = 37 * hash + Objects.hashCode(this.status);
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
        final Invitation other = (Invitation) obj;
        if (!Objects.equals(this.targetManager, other.targetManager)) {
            return false;
        }
        if (!Objects.equals(this.authorManager, other.authorManager)) {
            return false;
        }
        if (!Objects.equals(this.domain, other.domain)) {
            return false;
        }
        if (!Objects.equals(this.permission, other.permission)) {
            return false;
        }
        if (!Objects.equals(this.token, other.token)) {
            return false;
        }
        if (!Objects.equals(this.validUntil, other.validUntil)) {
            return false;
        }
        return this.status == other.status;
    }

}
