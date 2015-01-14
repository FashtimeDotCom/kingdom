/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.account;

import com.josue.credential.manager.auth.credential.CredentialStatus;
import com.josue.credential.manager.auth.credential.ManagerCredential;
import com.josue.credential.manager.auth.domain.Domain;
import com.josue.credential.manager.auth.domain.ManagerDomainCredential;
import com.josue.credential.manager.auth.manager.Manager;
import com.josue.credential.manager.auth.role.Role;
import com.josue.credential.manager.business.credential.CredentialRepository;
import com.josue.credential.manager.business.invitation.InvitationRepository;
import com.josue.credential.manager.business.invitation.ManagerInvitation;
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
    InvitationRepository invRepository;

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

    //TODO validate all cases (username already exists.. etc)
    public ManagerCredential create(String token, ManagerCredential managerCredential) {
        ManagerInvitation invitationByToken = invRepository.getInvitationByToken(token);
        if (invitationByToken == null) {
            //throw new Exception ???? AccountExceptin... business Exception ?? create better EX !
            return null;
        }
        //Check
        String foundLogin = credentialRepository.getManagerCredentialByLogin(managerCredential.getLogin());
        if (foundLogin != null) {
            //TODO throw another exception, check for exception for package modules
        }

        Domain foundDomain = accountRepository.find(Domain.class, invitationByToken.getDomain().getUuid());
        Role foundRole = accountRepository.find(Role.class, invitationByToken.getRole().getId());
        //check if domain is null... etc

        //TODO All this block should run inside the same TX
        managerCredential.removeNonCreatableFields();
        //Email should be the same from invitation
        managerCredential.getManager().setEmail(invitationByToken.getTargetEmail());
        managerCredential.setStatus(CredentialStatus.ACTIVE);
        //TODO cascade ????
        accountRepository.create(managerCredential.getManager());
        accountRepository.create(managerCredential);
        //Assign user to domain

        ManagerDomainCredential manDomCred = new ManagerDomainCredential();
        manDomCred.setCredential(managerCredential);
        manDomCred.setDomain(foundDomain);
        manDomCred.setRole(foundRole);

        accountRepository.create(manDomCred);

        return managerCredential;
    }
}
