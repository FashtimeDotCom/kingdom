/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.account;

import com.josue.credential.manager.auth.manager.Manager;
import com.josue.credential.manager.rest.Resource;
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
@Table(name = "manager_invitation")
public class ManagerInvitation extends Resource {

    @NotNull
    @Column(name = "target_email")
    String targetEmail;

    @OneToOne(targetEntity = Manager.class)
    @JoinColumn(name = "author_manager_uuid")
    private Resource authorManager;

    @NotNull
    private String token;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "valid_until", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date validUntil;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ManagerInvitationStatus status;

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

    public ManagerInvitationStatus getStatus() {
        return status;
    }

    public void setStatus(ManagerInvitationStatus status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.targetEmail);
        hash = 97 * hash + Objects.hashCode(this.authorManager);
        hash = 97 * hash + Objects.hashCode(this.token);
        hash = 97 * hash + Objects.hashCode(this.validUntil);
        hash = 97 * hash + Objects.hashCode(this.status);
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
        final ManagerInvitation other = (ManagerInvitation) obj;
        if (!Objects.equals(this.targetEmail, other.targetEmail)) {
            return false;
        }
        if (!Objects.equals(this.authorManager, other.authorManager)) {
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
