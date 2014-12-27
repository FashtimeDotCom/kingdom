/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.account;

import com.josue.credential.manager.auth.Domain;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 *
 * @author iFood
 */
@ApplicationScoped
public class AccountControl {

    @Inject
    AccountRepository repository;

    public Manager getManagerByCredential(String credentialUuid) {
        return repository.getManagerByCredential(credentialUuid);
    }

    public List<Domain> getManagerDomains(String credentialUuid) {
        return repository.getManagerDomainByCredential(credentialUuid);
    }

    public ManagerInvitation invite(ManagerInvitation managerInvitation) {
        return null;
    }

    public ManagerInvitation confirm(ManagerInvitation managerInvitation) {
        return null;
    }
}
