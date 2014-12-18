/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.account;

import com.josue.credential.manager.JpaRepository;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 *
 * @author iFood
 */
@ApplicationScoped
public class AccountControl {

    @Inject
    JpaRepository repository;

    public ManagerInvitation invite(ManagerInvitation managerInvitation) {
        return null;
    }

    public ManagerInvitation confirm(ManagerInvitation managerInvitation) {
        return null;
    }
}
