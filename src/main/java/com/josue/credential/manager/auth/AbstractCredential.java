/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.auth;

import com.josue.credential.manager.account.Manager;
import com.josue.credential.manager.account.Resource;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

/**
 *
 * @author Josue
 */
@MappedSuperclass
public class AbstractCredential extends Resource {

    @OneToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToOne(targetEntity = Manager.class)
    @JoinColumn(name = "manager_uuid")
    private Resource manager;

    private boolean active;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Resource getManager() {
        return manager;
    }

    public void setManager(Resource manager) {
        this.manager = manager;
    }

}
