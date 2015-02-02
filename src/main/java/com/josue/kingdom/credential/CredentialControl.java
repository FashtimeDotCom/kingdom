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
import com.josue.kingdom.credential.entity.PasswordChangeEvent;
import com.josue.kingdom.credential.entity.SimpleLogin;
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
import com.josue.kingdom.security.AccessLevelPermission;
import com.josue.kingdom.security.KingdomSecurity;
import com.josue.kingdom.security.manager.ManagerToken;
import com.josue.kingdom.util.KingdomUtils;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;

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
    DomainRepository domainRepository;

    @Inject
    Event<PasswordChangeEvent> passwordResetEvent;

    @Inject
    Event<LoginRecoveryEvent> loginRecoveryEvent;

    @Inject
    KingdomSecurity security;

    @Inject
    KingdomUtils utils;

    public ListResource<APICredential> getAPICredentials(String domainUuid, Integer limit, Integer offset) throws RestException {
        List<APICredential> apiCredentials = credentialRepository.getAPICredentials(security.getCurrentApplication().getUuid(), domainUuid, limit, offset);
        for (APICredential apiCredential : apiCredentials) {
            String obfuscatedToken = utils.obfuscateToken(apiCredential.getApiKey(), apiCredential.getApiKey().length() - 3);
            apiCredential.setApiKey(obfuscatedToken);
            //Optional.. removing non usable fields
            apiCredential.getMembership().getDomain().setOwner(null);
            apiCredential.getMembership().setManager(null);
        }

        long totalCount = credentialRepository.countAPICredential(security.getCurrentManager().getUuid(), security.getCurrentManager().getUuid());
        return ListResourceUtils.buildListResource(apiCredentials, totalCount, limit, offset);
    }

    public ListResource<APICredential> getAPICredentialsByDomainAndManager(String domainUuid, Integer limit, Integer offset) throws RestException {
        List<APICredential> apiCredentials = credentialRepository.getAPICredentials(security.getCurrentApplication().getUuid(), domainUuid, security.getCurrentManager().getUuid(), limit, offset);
        for (APICredential apiCredential : apiCredentials) {
            String obfuscatedToken = utils.obfuscateToken(apiCredential.getApiKey(), apiCredential.getApiKey().length() - 3);
            apiCredential.setApiKey(obfuscatedToken);
            //Optional.. removing non usable fields
            apiCredential.getMembership().getDomain().setOwner(null);
            apiCredential.getMembership().setManager(null);
        }

        long totalCount = credentialRepository.countAPICredential(security.getCurrentManager().getUuid(), security.getCurrentManager().getUuid());
        return ListResourceUtils.buildListResource(apiCredentials, totalCount, limit, offset);
    }

    public APICredential getAPICredential(String apiCredentialUuid) {
        APICredential apiCredential = credentialRepository.getAPICredential(security.getCurrentApplication().getUuid(), apiCredentialUuid);
        String obfuscatedToken = utils.obfuscateToken(apiCredential.getApiKey(), apiCredential.getApiKey().length() - 3);
        apiCredential.setApiKey(obfuscatedToken);
        return apiCredential;
    }

    public APICredential updateAPICredential(String domainUuid, String credentialUuid, APICredential apiCredential) throws RestException {
        APICredential foundCredential = credentialRepository.find(APICredential.class, security.getCurrentApplication().getUuid(), credentialUuid);
        if (foundCredential == null) {
            throw new ResourceNotFoundException(APICredential.class, credentialUuid);
        }

        DomainPermission foundPermission = domainRepository.getDomainPermission(security.getCurrentApplication().getUuid(), domainUuid, apiCredential.getMembership().getPermission().getName());
        if (foundPermission == null) {
            throw new InvalidResourceArgException(APICredential.class, "Permission name", apiCredential.getMembership().getPermission().getName());
        }

        if (!security.isPermitted(new AccessLevelPermission(domainUuid, foundPermission))) {
            throw new AuthorizationException(apiCredential.getMembership().getPermission());
        }

        foundCredential.copyUpdatable(apiCredential);
        APICredential updated = credentialRepository.update(foundCredential);
        return updated;

    }

    public APICredential createAPICredential(String domainUuid, APICredential apiCredential) throws RestException {

        apiCredential.removeNonCreatable();
        ManagerMembership membership = credentialRepository.getManagerMembership(security.getCurrentApplication().getUuid(), domainUuid, security.getCurrentManager().getUuid());

        if (membership == null) {
            throw new InvalidResourceArgException(ManagerMembership.class,
                    String.format("Membership not found for for current Manager and Domain %s", domainUuid));
        }

        //Check permission for create API Permission level
        if (!security.isPermitted(new AccessLevelPermission(domainUuid, membership.getPermission()))) {
            throw new AuthorizationException(apiCredential.getMembership().getPermission());
        }

        //API credential is bound to an manager-domain membership, and inherits its Permission
        apiCredential.setMembership(membership);
        apiCredential.setApplication(security.getCurrentApplication());
        utils.obfuscateToken(apiCredential.getApiKey(), apiCredential.getApiKey().length() - 3);
        apiCredential.setStatus(AccountStatus.ACTIVE);

        credentialRepository.create(apiCredential);
        return apiCredential;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void deleteAPICredential(String domainUuid, String apiCredentialUuid) throws ResourceNotFoundException {
        APICredential apiCredential = credentialRepository.find(APICredential.class, security.getCurrentApplication().getUuid(), apiCredentialUuid);
        if (apiCredential == null) {
            throw new ResourceNotFoundException(APICredential.class, apiCredentialUuid);
        }
        //This block should run within the same TX
        credentialRepository.delete(apiCredential);
    }

    public ListResource<Manager> getManagers(Integer limit, Integer offset) {
        List<Manager> managers = credentialRepository.getManagers(security.getCurrentApplication().getUuid(), limit, offset);
        Long totalCount = credentialRepository.count(Manager.class, security.getCurrentApplication().getUuid());
        return ListResourceUtils.buildListResource(managers, totalCount, limit, offset);
    }

    public Manager getManagerBylogin(String login) throws ResourceNotFoundException {
        Manager managerByLogin = credentialRepository.getManagerByUsername(security.getCurrentApplication().getUuid(), login);
        if (managerByLogin == null) {
            throw new ResourceNotFoundException(Manager.class, "login", login);
        }
        return managerByLogin;
    }

    public Manager login(SimpleLogin simpleLogin) throws RestException {

        String value = simpleLogin.getValue();
        byte[] parseBase64Binary = DatatypeConverter.parseBase64Binary(value);
        String[] loginPass = new String(parseBase64Binary).split(":");
        if (loginPass.length != 2) {//Invalid ':' character
            throw new InvalidResourceArgException(SimpleLogin.class, "value", value);
        }

        Manager foundManager = security.login(new ManagerToken(loginPass[0], loginPass[1].toCharArray(), security.getCurrentApplication().getUuid()));
        return foundManager;

    }

    public Manager getCurrentManager() throws RestException {
        return security.getCurrentManager();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Manager updateManagerPassword(String passwordChangeToken, String newPassword) throws RestException {
        PasswordChangeEvent eventBean = credentialRepository.getPasswordResetEvent(security.getCurrentApplication().getUuid(), passwordChangeToken);
        if (eventBean == null) {
            throw new ResourceNotFoundException(PasswordChangeEvent.class, "token", passwordChangeToken);
        } else if (!eventBean.isIsValid() || eventBean.getValidUntil().before(new Date())) {
            throw new RestException(PasswordChangeEvent.class, passwordChangeToken, "Invalid token", Response.Status.BAD_REQUEST);
        }
        //TODO and if password is the actual ?
        //running inside TX, we dont need to update

        eventBean.setIsValid(false);
        eventBean.getTargetManager().setPassword(newPassword);
        return eventBean.getTargetManager();

    }

    //Creates a new Event, if any unused token already exists, invalidate it and create a new one
    @Transactional(Transactional.TxType.REQUIRED)
    public void createPasswordChangeEvent(String username) throws RestException {
        Manager foundManager = credentialRepository.getManagerByUsername(security.getCurrentApplication().getUuid(), username);
        if (foundManager == null) {
            //TODO wich exception should be thrown ?
            throw new ResourceNotFoundException(Manager.class, "username", username);
        }

        //This block should run within the same TX
        List<PasswordChangeEvent> events = credentialRepository.getPasswordResetEvents(security.getCurrentApplication().getUuid(), foundManager.getUuid());
        for (PasswordChangeEvent event : events) {
            event.setIsValid(false);//running inside TX, dont need to explict update it
        }

        String token = utils.generateBase64FromUuid();
        PasswordChangeEvent eventBean = new PasswordChangeEvent(foundManager, token);

        credentialRepository.create(eventBean);
        passwordResetEvent.fire(eventBean);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void loginRecovery(String email) throws RestException {
        Manager foundManager = credentialRepository.getManagerByEmail(security.getCurrentApplication().getUuid(), email);
        if (foundManager == null) {
            //TODO wich exception should be thrown ?
            throw new ResourceNotFoundException(Manager.class, "email", email);
        }

        //This block should run within the same TX
        //TODO check email / login access policies
        loginRecoveryEvent.fire(new LoginRecoveryEvent(foundManager.getEmail(), foundManager.getUsername()));
    }

    @Transactional(Transactional.TxType.REQUIRED)//TODO update all logic... manager is now created on invitation submit
    public Manager createManager(String token, Manager manager) throws RestException {
        Invitation foundInvitation = invRepository.getInvitationByToken(security.getCurrentApplication().getUuid(), token);
        if (foundInvitation == null) {
            throw new ResourceNotFoundException(Invitation.class, "token", token, "Invalid invitation token, the domain still exists ? check with the Domain manager");
        }
        checkInvitationStatus(foundInvitation.getStatus());

        //Validate non nullable fields... Bean validation ?
        //TODO apply rules for username and password
        if (manager.getUsername() == null || manager.getUsername().length() == 0) {
            throw new InvalidResourceArgException(Manager.class, "Username should not be null or empty");
        }
        if (manager.getPassword() == null || manager.getPassword().length() == 0) {
            throw new InvalidResourceArgException(Manager.class, "Password should not be null or empty");
        }

        Manager existingByUsername = credentialRepository.getManagerByUsername(security.getCurrentApplication().getUuid(), manager.getUsername());
        if (existingByUsername != null) {
            throw new ResourceAlreadyExistsException(Manager.class, "username", manager.getUsername());
        }

        Manager targetManager = foundInvitation.getTargetManager();
        if (targetManager.getStatus().equals(AccountStatus.INACTIVE)) {//Account already blocked, the invitation process should fail
            throw new RestException(Manager.class, targetManager.getUuid(), "Manager account is blocked", Response.Status.BAD_REQUEST);
        } else if (targetManager.getStatus().equals(AccountStatus.ACTIVE)) {
            //Account already activated by another invitation process, just add it to current invitation domain
            //Do nothing with manager. it already exist
        } else if (targetManager.getStatus().equals(AccountStatus.PROVISIONING)) {
            manager.removeNonCreatable();
            targetManager.setStatus(AccountStatus.ACTIVE);
            targetManager.setFirstName(manager.getFirstName());
            targetManager.setLastName(manager.getLastName());
            targetManager.setPassword(manager.getPassword());
            targetManager.setUsername(manager.getUsername());
            credentialRepository.update(targetManager);
        }

        //TODO validate if the domain and the permission still exists
        Domain foundDomain = credentialRepository.find(Domain.class, security.getCurrentApplication().getUuid(), foundInvitation.getDomain().getUuid());
        DomainPermission foundPermission = credentialRepository.find(DomainPermission.class, security.getCurrentApplication().getUuid(), foundInvitation.getPermission().getUuid());

        //Assign user to domain
        ManagerMembership managerMembership = new ManagerMembership();
        managerMembership.setApplication(security.getCurrentApplication());
        managerMembership.setDomain(foundDomain);
        managerMembership.setPermission(foundPermission);
        managerMembership.setManager(targetManager);

        credentialRepository.create(managerMembership);
        //TODO complete the invitation status and 'invalidate' the token
        //assign a 'confirmation' token to the manager
        return targetManager;
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
