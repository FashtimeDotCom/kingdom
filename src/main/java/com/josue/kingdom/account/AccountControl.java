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
import com.josue.kingdom.domain.entity.DomainPermission;
import com.josue.kingdom.domain.entity.ManagerDomainCredential;
import com.josue.kingdom.invitation.InvitationRepository;
import com.josue.kingdom.invitation.entity.Invitation;
import com.josue.kingdom.invitation.entity.InvitationStatus;
import com.josue.kingdom.rest.ListResource;
import com.josue.kingdom.rest.ex.InvalidResourceArgException;
import com.josue.kingdom.rest.ex.ResourceAlreadyExistsException;
import com.josue.kingdom.rest.ex.ResourceNotFoundException;
import com.josue.kingdom.rest.ex.RestException;
import com.josue.kingdom.util.ListResourceUtil;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
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

    @Transactional(Transactional.TxType.REQUIRED)
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

    @Transactional(Transactional.TxType.REQUIRED)
    public ManagerCredential createCredential(String token, ManagerCredential managerCredential) throws RestException {
        Invitation invitationByToken = invRepository.getInvitationByToken(token);
        if (invitationByToken == null) {
            throw new ResourceNotFoundException(Invitation.class, "token", token, "Invalid invitation token, the domain still exists ? check with the Domain manager");
        }
        checkInvitationStatus(invitationByToken.getStatus());

        String foundLogin = credentialRepository.getManagerCredentialByLogin(managerCredential.getLogin());
        if (foundLogin != null) {
            throw new ResourceAlreadyExistsException(ManagerCredential.class, "login", managerCredential.getLogin());
        }

        //TODO All this block should run inside the same TX
        //Domain and DomainPermission should not be null on this stage
        Domain foundDomain = accountRepository.find(Domain.class, invitationByToken.getDomain().getUuid());
        DomainPermission foundPermission = accountRepository.find(DomainPermission.class, invitationByToken.getPermission().getUuid());

        managerCredential.removeNonCreatable();
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
        manDomCred.setPermission(foundPermission);

        accountRepository.create(manDomCred);

        return managerCredential;
    }

    private void checkInvitationStatus(InvitationStatus status) throws RestException {
        if (status == InvitationStatus.COMPLETED) {
            throw new InvalidResourceArgException(Invitation.class, "This token was already used, try recovery your password");
        } else if (status == InvitationStatus.FAILED) {
            throw new InvalidResourceArgException(Invitation.class, "This invitation failed, please request a new invitation");
        } else if (status == InvitationStatus.EXPIRED) {
            throw new InvalidResourceArgException(Invitation.class, "Token expired, please request a new invitation");
        }
    }
}
