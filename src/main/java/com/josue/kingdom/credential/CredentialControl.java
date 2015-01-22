/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.credential;

import com.josue.kingdom.credential.entity.APICredential;
import com.josue.kingdom.credential.entity.AccountStatus;
import com.josue.kingdom.credential.entity.LoginRecoveryEvent;
import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.credential.entity.PasswordResetEvent;
import com.josue.kingdom.domain.DomainRepository;
import com.josue.kingdom.domain.entity.Domain;
import com.josue.kingdom.domain.entity.DomainPermission;
import com.josue.kingdom.domain.entity.ManagerMembership;
import com.josue.kingdom.invitation.InvitationRepository;
import com.josue.kingdom.invitation.entity.Invitation;
import com.josue.kingdom.invitation.entity.InvitationStatus;
import com.josue.kingdom.rest.ListResource;
import com.josue.kingdom.rest.ListResourceUtils;
import com.josue.kingdom.rest.ex.AuthorizationException;
import com.josue.kingdom.rest.ex.InvalidResourceArgException;
import com.josue.kingdom.rest.ex.ResourceAlreadyExistsException;
import com.josue.kingdom.rest.ex.ResourceNotFoundException;
import com.josue.kingdom.rest.ex.RestException;
import com.josue.kingdom.shiro.AccessLevelPermission;
import com.josue.kingdom.util.cdi.Current;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.Permission;

/**
 *
 * @author Josue
 */
@ApplicationScoped
public class CredentialControl {

    @Inject
    InvitationRepository invRepository;

    @Inject
    CredentialRepository accountRepository;

    @Inject
    DomainRepository domainRepository;

    @Inject
    Event<PasswordResetEvent> passwordResetEvent;

    @Inject
    Event<LoginRecoveryEvent> loginRecoveryEvent;

    @Inject
    @Current
    Manager currentManager;

    public ListResource<APICredential> getAPICredentials(String domainUuid, Integer limit, Integer offset) {
        List<APICredential> apiCredentials = accountRepository.getAPICredentials(currentManager.getApplication().getUuid(), domainUuid, limit, offset);
        for (APICredential apiCredential : apiCredentials) {
            obfuscateKeys(apiCredential);
            //Optional.. removing non usable fields
            apiCredential.getMembership().getDomain().setOwner(null);
            apiCredential.getMembership().setManager(null);
        }

        long totalCount = accountRepository.countAPICredential(currentManager.getUuid(), currentManager.getUuid());
        return ListResourceUtils.buildListResource(apiCredentials, totalCount, limit, offset);
    }

    public ListResource<APICredential> getAPICredentialsByDomainAndManager(String domainUuid, Integer limit, Integer offset) {
        List<APICredential> apiCredentials = accountRepository.getAPICredentials(currentManager.getApplication().getUuid(), domainUuid, currentManager.getUuid(), limit, offset);
        for (APICredential apiCredential : apiCredentials) {
            obfuscateKeys(apiCredential);
            //Optional.. removing non usable fields
            apiCredential.getMembership().getDomain().setOwner(null);
            apiCredential.getMembership().setManager(null);
        }

        long totalCount = accountRepository.countAPICredential(currentManager.getUuid(), currentManager.getUuid());
        return ListResourceUtils.buildListResource(apiCredentials, totalCount, limit, offset);
    }

    public APICredential getAPICredential(String apiCredentialUuid) {
        APICredential apiCredential = accountRepository.getAPICredential(currentManager.getApplication().getUuid(), apiCredentialUuid);
        obfuscateKeys(apiCredential);
        return apiCredential;
    }

    public APICredential updateAPICredential(String domainUuid, String credentialUuid, APICredential apiCredential) throws RestException {
        APICredential foundCredential = accountRepository.find(APICredential.class, currentManager.getApplication().getUuid(), credentialUuid);
        if (foundCredential == null) {
            throw new ResourceNotFoundException(APICredential.class, credentialUuid);
        }

        DomainPermission foundPermission = domainRepository.getDomainPermission(domainUuid, apiCredential.getMembership().getPermission().getName());
        if (foundPermission == null) {
            throw new InvalidResourceArgException(APICredential.class, "Permission name", apiCredential.getMembership().getPermission().getName());
        }

        if (!isPermitted(new AccessLevelPermission(domainUuid, foundPermission))) {
            throw new AuthorizationException(apiCredential.getMembership().getPermission());
        }

        foundCredential.copyUpdatable(apiCredential);
        APICredential updated = accountRepository.update(foundCredential);
        return updated;

    }

    //TODO should change to a specific class ???
    //Encapsules thrity party (Shiro)... for testing purposes
    protected boolean isPermitted(Permission permission) {
        return SecurityUtils.getSubject().isPermitted(permission);

    }

    public APICredential createAPICredential(String domainUuid, APICredential apiCredential) throws RestException {

        apiCredential.removeNonCreatable();
        ManagerMembership membership = accountRepository.getManagerMembership(currentManager.getApplication().getUuid(), domainUuid, currentManager.getUuid());

        if (membership == null) {
            throw new InvalidResourceArgException(ManagerMembership.class,
                    String.format("Membership not found for for current Manager and Domain %s", domainUuid));
        }

        //Check permission for create API Permission level
        if (!isPermitted(new AccessLevelPermission(domainUuid, membership.getPermission()))) {
            throw new AuthorizationException(apiCredential.getMembership().getPermission());
        }

        //API credential is bound to an manager-domain membership, and inherits its Permission
        apiCredential.setMembership(membership);
        apiCredential.setApplication(currentManager.getApplication());
        apiCredential.setApiKey(generateAPIKey());
        apiCredential.setStatus(AccountStatus.ACTIVE);

        accountRepository.create(apiCredential);
        return apiCredential;

    }

    public void deleteAPICredential(String domainUuid, String apiCredentialUuid) throws ResourceNotFoundException {
        APICredential apiCredential = accountRepository.find(APICredential.class, currentManager.getApplication().getUuid(), apiCredentialUuid);
        if (apiCredential == null) {
            throw new ResourceNotFoundException(APICredential.class, apiCredentialUuid);
        }
        //TODO This block should run within the same TX
        accountRepository.delete(apiCredential);
    }

    public ListResource<Manager> getManagers(Integer limit, Integer offset) {
        List<Manager> managers = accountRepository.getManagers(currentManager.getApplication().getUuid(), limit, offset);
        Long totalCount = accountRepository.count(Manager.class, currentManager.getApplication().getUuid());
        return ListResourceUtils.buildListResource(managers, totalCount, limit, offset);
    }

    public Manager getManagerBylogin(String login) throws ResourceNotFoundException {
        Manager managerByLogin = accountRepository.getManagerByUsername(currentManager.getApplication().getUuid(), login);
        if (managerByLogin == null) {
            throw new ResourceNotFoundException(Manager.class, "login", login);
        }
        return managerByLogin;
    }

    //TODO create a resource class to register credentials changes
    @Transactional(Transactional.TxType.REQUIRED)
    public void passwordReset(String login) throws RestException {
        Manager foundManager = accountRepository.getManagerByUsername(currentManager.getApplication().getUuid(), login);
        if (foundManager == null) {
            //TODO wicch exception should be thrown ?
            throw new ResourceNotFoundException(Manager.class, "login", login);
        }

        //This block should run within the same TX block
        String newPassword = new BigInteger(130, new SecureRandom()).toString(32);
        foundManager.setPassword(newPassword);
        accountRepository.update(foundManager);

        passwordResetEvent.fire(new PasswordResetEvent(foundManager.getEmail(), newPassword));
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void loginRecovery(String email) throws RestException {
        Manager foundManager = accountRepository.getManagerByEmail(currentManager.getApplication().getUuid(), email);
        if (foundManager == null) {
            //TODO wicch exception should be thrown ?
            throw new ResourceNotFoundException(Manager.class, "email", email);
        }

        //This block should run within the same TX block
        //TODO check email / login access policies
        loginRecoveryEvent.fire(new LoginRecoveryEvent(foundManager.getEmail(), foundManager.getUsername()));
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Manager createManager(String token, Manager manager) throws RestException {
        Invitation invitationByToken = invRepository.getInvitationByToken(token);
        if (invitationByToken == null) {
            throw new ResourceNotFoundException(Invitation.class, "token", token, "Invalid invitation token, the domain still exists ? check with the Domain manager");
        }
        checkInvitationStatus(invitationByToken.getStatus());

        //TODO working only with email
        Manager foundManager = accountRepository.getManagerByEmail(currentManager.getApplication().getUuid(), manager.getEmail());
        if (foundManager != null) {
            throw new ResourceAlreadyExistsException(Manager.class, "email", manager.getEmail());
        }

        //TODO All this block should run inside the same TX
        //Domain and DomainPermission should not be null on this stage
        Domain foundDomain = accountRepository.find(Domain.class, currentManager.getApplication().getUuid(), invitationByToken.getDomain().getUuid());
        DomainPermission foundPermission = accountRepository.find(DomainPermission.class, currentManager.getApplication().getUuid(), invitationByToken.getPermission().getUuid());

        manager.removeNonCreatable();
        //Email should be the same from invitation
        manager.setEmail(invitationByToken.getTargetEmail());
        manager.setStatus(AccountStatus.ACTIVE);
        accountRepository.create(manager);

        //Assign user to domain
        ManagerMembership managerMembership = new ManagerMembership();
        managerMembership.setApplication(currentManager.getApplication());
        managerMembership.setDomain(foundDomain);
        managerMembership.setPermission(foundPermission);
        managerMembership.setManager(manager);

        accountRepository.create(managerMembership);

        return manager;
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
