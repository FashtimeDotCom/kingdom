/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.invitation.entity;

import com.josue.kingdom.account.entity.Manager;
import com.josue.kingdom.domain.entity.Domain;
import com.josue.kingdom.domain.entity.Role;
import com.josue.kingdom.rest.Resource;
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
import javax.validation.constraints.NotNull;

/**
 *
 * @author Josue
 */
@Entity
@Table(name = "invitation")
//TODO change to Invitation
public class Invitation extends Resource {

    @NotNull
    @Column(name = "target_email")
    //TODO create validator
    private String targetEmail;

    @NotNull
    @OneToOne(targetEntity = Manager.class)
    @JoinColumn(name = "author_manager_uuid")
    private Resource authorManager;

    @NotNull
    @OneToOne(targetEntity = Domain.class)
    @JoinColumn(name = "domain_uuid")
    private Resource domain;

    @OneToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @NotNull
    private String token;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "valid_until", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date validUntil;

    @NotNull
    @Enumerated(EnumType.STRING)
    private InvitationStatus status;

    @Override
    public void removeNonCreatableFields() {
        super.removeNonCreatableFields();
        this.token = null;
        this.validUntil = null;
        this.status = null;
        this.authorManager = null;
        if (authorManager != null) {
            authorManager.removeNonCreatableFields();
        }
        if (domain != null) {
            domain.removeNonCreatableFields();
        }
    }

    @Override
    public void copyUpdatebleFields(Resource newData) {
        if (newData instanceof Invitation) {
            Invitation invitation = (Invitation) newData;
            status = status != null ? invitation.status : status;
        }
    }

    public String getTargetEmail() {
        return targetEmail;
    }

    public void setTargetEmail(String targetEmail) {
        this.targetEmail = targetEmail;
    }

    public Resource getAuthorManager() {
        return authorManager;
    }

    public void setAuthorManager(Resource authorManager) {
        this.authorManager = authorManager;
    }

    public Resource getDomain() {
        return domain;
    }

    public void setDomain(Resource domain) {
        this.domain = domain;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.targetEmail);
        hash = 37 * hash + Objects.hashCode(this.authorManager);
        hash = 37 * hash + Objects.hashCode(this.domain);
        hash = 37 * hash + Objects.hashCode(this.role);
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
        if (!Objects.equals(this.targetEmail, other.targetEmail)) {
            return false;
        }
        if (!Objects.equals(this.authorManager, other.authorManager)) {
            return false;
        }
        if (!Objects.equals(this.domain, other.domain)) {
            return false;
        }
        if (!Objects.equals(this.role, other.role)) {
            return false;
        }
        if (!Objects.equals(this.token, other.token)) {
            return false;
        }
        if (!Objects.equals(this.validUntil, other.validUntil)) {
            return false;
        }
        if (this.status != other.status) {
            return false;
        }
        return true;
    }

}
