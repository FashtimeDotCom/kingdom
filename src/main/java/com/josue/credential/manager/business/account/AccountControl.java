/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.account;

import com.josue.credential.manager.auth.credential.ManagerCredential;
import com.josue.credential.manager.auth.manager.Manager;
import com.josue.credential.manager.business.credential.CredentialRepository;
import com.josue.credential.manager.rest.ex.RestException;
import java.math.BigInteger;
import java.security.SecureRandom;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

/**
 *
 * @author Josue
 */
@ApplicationScoped
public class AccountControl {

    @Inject
    CredentialRepository credentialRepository;

    @Inject
    AccountRepository accountRepository;

    @Inject
    AccountService service;

    public void passwordRecovery(String email) throws RestException {
        Manager foundManager = accountRepository.findManagerByEmail(email);
        if (foundManager == null) {
            throw new RestException(Manager.class, "", String.format("Account not found for email {0}", email), Response.Status.OK);
        }

        //This block should run within the same TX block
        ManagerCredential foundCredential = credentialRepository.getManagerCredentialByManager(foundManager.getUuid());
        String newPassword = new BigInteger(130, new SecureRandom()).toString(32);
        foundCredential.setPassword(newPassword);
        credentialRepository.edit(foundCredential);

        service.sendPasswordRecovery(foundManager.getEmail(), newPassword);
    }
}
