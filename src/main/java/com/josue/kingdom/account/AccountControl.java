/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.account;

import com.josue.kingdom.account.entity.Manager;
import com.josue.kingdom.credential.CredentialRepository;
import com.josue.kingdom.credential.entity.CredentialStatus;
import com.josue.kingdom.credential.entity.ManagerCredential;
import com.josue.kingdom.domain.entity.Domain;
import com.josue.kingdom.domain.entity.ManagerDomainCredential;
import com.josue.kingdom.domain.entity.DomainRole;
import com.josue.kingdom.invitation.InvitationRepository;
import com.josue.kingdom.invitation.entity.Invitation;
import com.josue.kingdom.rest.ListResource;
import com.josue.kingdom.rest.ex.RestException;
import com.josue.kingdom.util.ListResourceUtil;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
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

    public Manager getManagerByCredential(String credentialUuid) {
        return accountRepository.getManagerByCredential(credentialUuid);
    }

    public ListResource<Manager> getManagers(Integer limit, Integer offset) {
        List<Manager> managers = accountRepository.getManagers(limit, offset);
        Long totalCount = accountRepository.count(Manager.class);
        return ListResourceUtil.buildListResource(managers, totalCount, limit, offset);
    }

    public void passwordRecovery(String login) throws RestException {
        Manager foundManager = accountRepository.getManagerByLogin(login);
        if (foundManager == null) {
            //TODO wicch exception should be thrown ?
            throw new RestException(Manager.class, "", String.format("Account not found for login {0}", login), Response.Status.OK);
        }

        //This block should run within the same TX block
        ManagerCredential foundCredential = credentialRepository.getManagerCredentialByManager(foundManager.getUuid());
        String newPassword = new BigInteger(130, new SecureRandom()).toString(32);
        foundCredential.setPassword(newPassword);
        credentialRepository.update(foundCredential);

        service.sendPasswordRecovery(foundManager.getEmail(), newPassword);
    }

    //TODO validate all cases (username already exists.. etc)
    public ManagerCredential createCredential(String token, ManagerCredential managerCredential) {
        Invitation invitationByToken = invRepository.getInvitationByToken(token);
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
        DomainRole foundRole = accountRepository.find(DomainRole.class, invitationByToken.getRole().getUuid());
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
