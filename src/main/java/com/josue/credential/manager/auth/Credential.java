/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.auth;

import com.josue.credential.manager.Resource;
import com.josue.credential.manager.account.Manager;
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
    @OneToOne
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

}
