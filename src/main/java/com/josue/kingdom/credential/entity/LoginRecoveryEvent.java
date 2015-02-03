/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.credential.entity;

import com.josue.kingdom.rest.TenantResource;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Josue
 */
//TODO improve ?
@Entity
@Table(name = "login_recovery_event")
public class LoginRecoveryEvent extends TenantResource {

    @OneToOne
    @JoinColumn(name = "target_manager_uuid")
    private Manager targetManager;

    public LoginRecoveryEvent() {
    }

    public LoginRecoveryEvent(Manager targetManager) {
        this.targetManager = targetManager;
    }

    public Manager getTargetManager() {
        return targetManager;
    }

    public void setTargetManager(Manager targetManager) {
        this.targetManager = targetManager;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.targetManager);
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
        final LoginRecoveryEvent other = (LoginRecoveryEvent) obj;
        if (!Objects.equals(this.targetManager, other.targetManager)) {
            return false;
        }
        return true;
    }

}
