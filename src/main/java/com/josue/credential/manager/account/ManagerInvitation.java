/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.account;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
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

    @OneToOne(targetEntity = Manager.class)
    @JoinColumn(name = "target_manager")
    private Resource targetManager;

    @OneToOne(targetEntity = Manager.class)
    @JoinColumn(name = "author_manager")
    private Resource authorManager;

    private String token;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "valid_until", columnDefinition = "TIMESTAMP")
    private Date validUntil;

    @NotNull
    private boolean active;

    public Resource getTargetManager() {
        return targetManager;
    }

    public void setTargetManager(Resource targetManager) {
        this.targetManager = targetManager;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + (this.targetManager != null ? this.targetManager.hashCode() : 0);
        hash = 71 * hash + (this.authorManager != null ? this.authorManager.hashCode() : 0);
        hash = 71 * hash + (this.token != null ? this.token.hashCode() : 0);
        hash = 71 * hash + (this.validUntil != null ? this.validUntil.hashCode() : 0);
        hash = 71 * hash + (this.active ? 1 : 0);
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
        if (this.targetManager != other.targetManager && (this.targetManager == null || !this.targetManager.equals(other.targetManager))) {
            return false;
        }
        if (this.authorManager != other.authorManager && (this.authorManager == null || !this.authorManager.equals(other.authorManager))) {
            return false;
        }
        if ((this.token == null) ? (other.token != null) : !this.token.equals(other.token)) {
            return false;
        }
        if (this.validUntil != other.validUntil && (this.validUntil == null || !this.validUntil.equals(other.validUntil))) {
            return false;
        }
        if (this.active != other.active) {
            return false;
        }
        return true;
    }

}
