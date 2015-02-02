/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.credential.entity;

import com.josue.kingdom.rest.TenantResource;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Josue
 */
@Entity
@Table(name = "password_reset_event")
public class PasswordChangeEvent extends TenantResource {

    @OneToOne
    private Manager targetManager;

    @NotNull
    private String token;

    @NotNull
    @Future
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "valid_until", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date validUntil;

    private boolean isValid;

    public PasswordChangeEvent() {

    }

    public PasswordChangeEvent(Manager targetManager, String token) {
        this.targetManager = targetManager;
        this.token = token;
    }

    public Manager getTargetManager() {
        return targetManager;
    }

    public void setTargetManager(Manager targetManager) {
        this.targetManager = targetManager;
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

    public boolean isIsValid() {
        return isValid;
    }

    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(this.targetManager);
        hash = 71 * hash + Objects.hashCode(this.token);
        hash = 71 * hash + Objects.hashCode(this.validUntil);
        hash = 71 * hash + (this.isValid ? 1 : 0);
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
        final PasswordChangeEvent other = (PasswordChangeEvent) obj;
        if (!Objects.equals(this.targetManager, other.targetManager)) {
            return false;
        }
        if (!Objects.equals(this.token, other.token)) {
            return false;
        }
        if (!Objects.equals(this.validUntil, other.validUntil)) {
            return false;
        }
        return this.isValid == other.isValid;
    }
}
