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
import com.josue.kingdom.security.Current;
import com.josue.kingdom.security.KingdomSecurity;
import com.josue.kingdom.security.manager.ManagerToken;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
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
    KingdomSecurity security;

    public ListResource<APICredential> getAPICredentials(String domainUuid, Integer limit, Integer offset) throws RestException {
        List<APICredential> apiCredentials = accountRepository.getAPICredentials(security.getCurrentApplication().getUuid(), domainUuid, limit, offset);
        for (APICredential apiCredential : apiCredentials) {
            obfuscateKeys(apiCredential);
            //Optional.. removing non usable fields
            apiCredential.getMembership().getDomain().setOwner(null);
            apiCredential.getMembership().setManager(null);
        }

        long totalCount = accountRepository.countAPICredential(security.getCurrentManager().getUuid(), security.getCurrentManager().getUuid());
        return ListResourceUtils.buildListResource(apiCredentials, totalCount, limit, offset);
    }

    public ListResource<APICredential> getAPICredentialsByDomainAndManager(String domainUuid, Integer limit, Integer offset) throws RestException {
        List<APICredential> apiCredentials = accountRepository.getAPICredentials(security.getCurrentApplication().getUuid(), domainUuid, security.getCurrentManager().getUuid(), limit, offset);
        for (APICredential apiCredential : apiCredentials) {
            obfuscateKeys(apiCredential);
            //Optional.. removing non usable fields
            apiCredential.getMembership().getDomain().setOwner(null);
            apiCredential.getMembership().setManager(null);
        }

        long totalCount = accountRepository.countAPICredential(security.getCurrentManager().getUuid(), security.getCurrentManager().getUuid());
        return ListResourceUtils.buildListResource(apiCredentials, totalCount, limit, offset);
    }

    public APICredential getAPICredential(String apiCredentialUuid) {
        APICredential apiCredential = accountRepository.getAPICredential(security.getCurrentApplication().getUuid(), apiCredentialUuid);
        obfuscateKeys(apiCredential);
        return apiCredential;
    }

    //TODO check how the permission will work for each of the Domain custom credentials
    public APICredential updateAPICredential(String domainUuid, String credentialUuid, APICredential apiCredential) throws RestException {
        APICredential foundCredential = accountRepository.find(APICredential.class, security.getCurrentApplication().getUuid(), credentialUuid);
        if (foundCredential == null) {
            throw new ResourceNotFoundException(APICredential.class, credentialUuid);
        }

        DomainPermission foundPermission = domainRepository.getDomainPermission(security.getCurrentApplication().getUuid(), domainUuid, apiCredential.getMembership().getPermission().getName());
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
        ManagerMembership membership = accountRepository.getManagerMembership(security.getCurrentApplication().getUuid(), domainUuid, security.getCurrentManager().getUuid());

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
        apiCredential.setApplication(security.getCurrentApplication());
        apiCredential.setApiKey(generateAPIKey());
        apiCredential.setStatus(AccountStatus.ACTIVE);

        accountRepository.create(apiCredential);
        return apiCredential;

    }

    public void deleteAPICredential(String domainUuid, String apiCredentialUuid) throws ResourceNotFoundException {
        APICredential apiCredential = accountRepository.find(APICredential.class, security.getCurrentApplication().getUuid(), apiCredentialUuid);
        if (apiCredential == null) {
            throw new ResourceNotFoundException(APICredential.class, apiCredentialUuid);
        }
        //TODO This block should run within the same TX
        accountRepository.delete(apiCredential);
    }

    public ListResource<Manager> getManagers(Integer limit, Integer offset) {
        List<Manager> managers = accountRepository.getManagers(security.getCurrentApplication().getUuid(), limit, offset);
        Long totalCount = accountRepository.count(Manager.class, security.getCurrentApplication().getUuid());
        return ListResourceUtils.buildListResource(managers, totalCount, limit, offset);
    }

    public Manager getManagerBylogin(String login) throws ResourceNotFoundException {
        Manager managerByLogin = accountRepository.getManagerByUsername(security.getCurrentApplication().getUuid(), login);
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

        Manager foundManager;
        try {
            SecurityUtils.getSubject().login(new ManagerToken(loginPass[0], loginPass[1].toCharArray(), security.getCurrentApplication().getUuid()));
            foundManager = (Manager) SecurityUtils.getSubject().getPrincipal();
        } catch (AuthenticationException e) {
            throw new com.josue.kingdom.rest.ex.AuthenticationException("json response here");
        }
        return foundManager;

    }

    public Manager getCurrentManager() throws RestException {
        return security.getCurrentManager();
    }

    //TODO create a resource class to register credentials changes
    @Transactional(Transactional.TxType.REQUIRED)
    public void passwordReset(String username) throws RestException {
        Manager foundManager = accountRepository.getManagerByUsername(security.getCurrentApplication().getUuid(), username);
        if (foundManager == null) {
            //TODO wicch exception should be thrown ?
            throw new ResourceNotFoundException(Manager.class, "username", username);
        }

        //This block should run within the same TX block
        String newPassword = new BigInteger(130, new SecureRandom()).toString(32);
        foundManager.setPassword(newPassword);
        accountRepository.update(foundManager);

        passwordResetEvent.fire(new PasswordResetEvent(foundManager.getEmail(), newPassword));
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void loginRecovery(String email) throws RestException {
        Manager foundManager = accountRepository.getManagerByEmail(security.getCurrentApplication().getUuid(), email);
        if (foundManager == null) {
            //TODO wicch exception should be thrown ?
            throw new ResourceNotFoundException(Manager.class, "email", email);
        }

        //This block should run within the same TX block
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

        Manager existingByUsername = accountRepository.getManagerByUsername(security.getCurrentApplication().getUuid(), manager.getUsername());
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
            accountRepository.update(targetManager);
        }

        //TODO validate if the domain and the permission still exists
        Domain foundDomain = accountRepository.find(Domain.class, security.getCurrentApplication().getUuid(), foundInvitation.getDomain().getUuid());
        DomainPermission foundPermission = accountRepository.find(DomainPermission.class, security.getCurrentApplication().getUuid(), foundInvitation.getPermission().getUuid());

        //Assign user to domain
        ManagerMembership managerMembership = new ManagerMembership();
        managerMembership.setApplication(security.getCurrentApplication());
        managerMembership.setDomain(foundDomain);
        managerMembership.setPermission(foundPermission);
        managerMembership.setManager(targetManager);

        accountRepository.create(managerMembership);
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
