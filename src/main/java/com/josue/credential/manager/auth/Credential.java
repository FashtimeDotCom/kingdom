/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.auth;

import com.josue.credential.manager.Resource;
import com.josue.credential.manager.account.Manager;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import org.apache.shiro.authc.AuthenticationToken;

/**
 *
 * @author Josue
 */
@MappedSuperclass
public abstract class Credential extends Resource implements AuthenticationToken {

    //enforce relationship
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "manager_uuid")
    private Manager manager;

    //Global status
    @Enumerated(EnumType.STRING)
    private CredentialStatus status;

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public CredentialStatus getStatus() {
        return status;
    }

    public void setStatus(CredentialStatus status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.manager);
        hash = 29 * hash + Objects.hashCode(this.status);
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
        final Credential other = (Credential) obj;
        if (!Objects.equals(this.manager, other.manager)) {
            return false;
        }
        if (this.status != other.status) {
            return false;
        }
        return true;
    }

}
