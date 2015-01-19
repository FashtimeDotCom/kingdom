/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.credential;

import com.josue.kingdom.credential.entity.APICredential;
import com.josue.kingdom.credential.entity.Credential;
import com.josue.kingdom.credential.entity.CredentialStatus;
import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.credential.entity.ManagerCredential;
import com.josue.kingdom.domain.DomainRepository;
import com.josue.kingdom.domain.entity.APIDomainCredential;
import com.josue.kingdom.domain.entity.Domain;
import com.josue.kingdom.domain.entity.DomainPermission;
import com.josue.kingdom.domain.entity.ManagerDomainCredential;
import com.josue.kingdom.invitation.InvitationRepository;
import com.josue.kingdom.invitation.entity.Invitation;
import com.josue.kingdom.invitation.entity.InvitationStatus;
import com.josue.kingdom.rest.ListResource;
import com.josue.kingdom.rest.ex.AuthorizationException;
import com.josue.kingdom.rest.ex.InvalidResourceArgException;
import com.josue.kingdom.rest.ex.ResourceAlreadyExistsException;
import com.josue.kingdom.rest.ex.ResourceNotFoundException;
import com.josue.kingdom.rest.ex.RestException;
import com.josue.kingdom.shiro.AccessLevelPermission;
import com.josue.kingdom.util.ListResourceUtil;
import com.josue.kingdom.util.cdi.Current;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.apache.shiro.SecurityUtils;

/**
 *
 * @author Josue
 */
@ApplicationScoped
public class CredentialControl {

    @Inject
    InvitationRepository invRepository;

    @Inject
    CredentialRepository credentialRepository;

    @Inject
    DomainRepository permissionRepository;

    @Inject
    CredentialService service;

    @Inject
    @Current
    Credential currentCredential;

    public ListResource<APIDomainCredential> getAPICredentials(String domainUuid, Integer limit, Integer offset) {
        List<APIDomainCredential> apiDomainCredentials = credentialRepository.getAPICredentials(currentCredential.getManager().getUuid(), domainUuid, limit, offset);
        for (APIDomainCredential apiDomCredential : apiDomainCredentials) {
            obfuscateKeys(apiDomCredential.getCredential());
            //Optional.. removing non usable fields
            apiDomCredential.getDomain().setOwner(null);
            apiDomCredential.getCredential().setManager(null);
        }

        long totalCount = credentialRepository.countAPICredential(currentCredential.getManager().getUuid(), currentCredential.getManager().getUuid());
        return ListResourceUtil.buildListResource(apiDomainCredentials, totalCount, limit, offset);
    }

    //Not used yet, return all credential for all Domains
    public ListResource<APIDomainCredential> getAPICredentials(Integer limit, Integer offset) {
        List<APIDomainCredential> apiDomainCredentials = credentialRepository.getAPICredentials(currentCredential.getManager().getUuid(), limit, offset);
        for (APIDomainCredential apiDomCredential : apiDomainCredentials) {
            obfuscateKeys(apiDomCredential.getCredential());
        }
        long totalCount = credentialRepository.countAPICredential(currentCredential.getManager().getUuid(), currentCredential.getManager().getUuid());
        return ListResourceUtil.buildListResource(apiDomainCredentials, totalCount, limit, offset);
    }

    public APIDomainCredential getAPICredential(String domainUuid, String apiKeyUuid) {
        APIDomainCredential apiDomainCredentials = credentialRepository.getAPICredential(currentCredential.getManager().getUuid(), domainUuid, apiKeyUuid);
        obfuscateKeys(apiDomainCredentials.getCredential());
        return apiDomainCredentials;
    }

    public APIDomainCredential updateAPICredential(String domainUuid, String credentialUuid, APIDomainCredential domainCredential) throws RestException {
        APIDomainCredential foundCredential = credentialRepository.find(APIDomainCredential.class, credentialUuid);
        if (foundCredential == null) {
            throw new ResourceNotFoundException(APICredential.class, credentialUuid);
        }

        DomainPermission foundPermission = permissionRepository.getDomainPermission(domainUuid, domainCredential.getPermission().getName());
        if (foundPermission == null) {
            throw new InvalidResourceArgException(APICredential.class, "Permission name", domainCredential.getPermission().getName());
        }

        //Check permission for create API Permission level
        if (!SecurityUtils.getSubject().isPermitted(new AccessLevelPermission(domainUuid, foundPermission))) {
            throw new AuthorizationException(domainCredential.getPermission());
        }

        foundCredential.copyUpdatable(domainCredential);
        APIDomainCredential updated = credentialRepository.update(foundCredential);
        credentialRepository.update(foundCredential.getCredential());
        return updated;

    }

    public APIDomainCredential createAPICredential(String domainUuid, APIDomainCredential domainCredential) throws RestException {

        domainCredential.removeNonCreatable();
        DomainPermission foundPermission = permissionRepository.getDomainPermission(domainUuid, domainCredential.getPermission().getName());
        if (foundPermission == null) {
            throw new InvalidResourceArgException(APICredential.class, "Permission name", domainCredential.getPermission().getName());
        }

        //Check permission for create API Permission level
        if (!SecurityUtils.getSubject().isPermitted(new AccessLevelPermission(domainUuid, foundPermission))) {
            throw new AuthorizationException(domainCredential.getPermission());
        }

        Domain currentDomain = credentialRepository.find(Domain.class, domainUuid);
        if (currentDomain == null) {
            throw new InvalidResourceArgException(Domain.class, "Domain", domainUuid);
        }

        domainCredential.setPermission(foundPermission);
        domainCredential.setDomain(currentDomain);
        domainCredential.getCredential().setApiKey(generateAPIKey());
        domainCredential.getCredential().setStatus(CredentialStatus.ACTIVE);
        domainCredential.getCredential().setManager(currentCredential.getManager());

        //TODO This block should be executed within sae transaction
        credentialRepository.create(domainCredential.getCredential());
        credentialRepository.create(domainCredential);

        return domainCredential;

    }

    public void deleteAPICredential(String domainUuid, String domainCredentialUuid) throws ResourceNotFoundException {
        APIDomainCredential apiDomCred = credentialRepository.find(APIDomainCredential.class, domainCredentialUuid);
        if (apiDomCred == null) {
            throw new ResourceNotFoundException(APIDomainCredential.class, domainCredentialUuid);
        }
        //TODO This block should run within the same TX
        credentialRepository.delete(apiDomCred);
        credentialRepository.delete(apiDomCred.getCredential());
    }

    public Manager getManagerByCredential(String credentialUuid) {
        return credentialRepository.getManagerByCredential(credentialUuid);
    }

    public ListResource<Manager> getManagers(Integer limit, Integer offset) {
        List<Manager> managers = credentialRepository.getManagers(limit, offset);
        Long totalCount = credentialRepository.count(Manager.class);
        return ListResourceUtil.buildListResource(managers, totalCount, limit, offset);
    }

    public Manager getManagerBylogin(String login) throws ResourceNotFoundException {
        Manager managerByLogin = credentialRepository.getManagerByLogin(login);
        if (managerByLogin == null) {
            throw new ResourceNotFoundException(Manager.class, "login", login);
        }
        return managerByLogin;
    }

    //TODO create a resource class to register credentials changes
    @Transactional(Transactional.TxType.REQUIRED)
    public void passwordReset(String login) throws RestException {
        Manager foundManager = credentialRepository.getManagerByLogin(login);
        if (foundManager == null) {
            //TODO wicch exception should be thrown ?
            throw new ResourceNotFoundException(Manager.class, "login", login);
        }

        //This block should run within the same TX block
        ManagerCredential foundCredential = credentialRepository.getManagerCredentialByManager(foundManager.getUuid());
        String newPassword = new BigInteger(130, new SecureRandom()).toString(32);
        foundCredential.setPassword(newPassword);
        credentialRepository.update(foundCredential);

        service.sendPasswordReset(foundManager.getEmail(), newPassword);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void loginRecovery(String email) throws RestException {
        Manager foundManager = credentialRepository.getManagerByEmail(email);
        if (foundManager == null) {
            //TODO wicch exception should be thrown ?
            throw new ResourceNotFoundException(Manager.class, "email", email);
        }

        //This block should run within the same TX block
        ManagerCredential foundCredential = credentialRepository.getManagerCredentialByManager(foundManager.getUuid());
        service.sendLoginRecovery(foundManager.getEmail(), foundCredential.getLogin());
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
        Domain foundDomain = credentialRepository.find(Domain.class, invitationByToken.getDomain().getUuid());
        DomainPermission foundPermission = credentialRepository.find(DomainPermission.class, invitationByToken.getPermission().getUuid());

        managerCredential.removeNonCreatable();
        //Email should be the same from invitation
        managerCredential.getManager().setEmail(invitationByToken.getTargetEmail());
        managerCredential.setStatus(CredentialStatus.ACTIVE);
        //TODO cascade ????
        credentialRepository.create(managerCredential.getManager());
        credentialRepository.create(managerCredential);
        //Assign user to domain

        ManagerDomainCredential manDomCred = new ManagerDomainCredential();
        manDomCred.setCredential(managerCredential);
        manDomCred.setDomain(foundDomain);
        manDomCred.setPermission(foundPermission);

        credentialRepository.create(manDomCred);

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

    //This method should not executed inside the same transaction of ANY repository
    //TODO improve
    private void obfuscateKeys(APICredential apiCredential) {
        String apiKey = apiCredential.getApiKey();
        String obfuscatedApiKey = "************" + apiKey.substring(apiKey.length() - 5);
        apiCredential.setApiKey(obfuscatedApiKey);
    }

    private String generateAPIKey() {
        return new BigInteger(130, new SecureRandom()).toString(32);
    }

}
