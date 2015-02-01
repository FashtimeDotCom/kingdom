/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.invitation;

import com.josue.kingdom.credential.CredentialRepository;
import com.josue.kingdom.credential.entity.AccountStatus;
import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.domain.DomainRepository;
import com.josue.kingdom.domain.entity.Domain;
import com.josue.kingdom.domain.entity.DomainPermission;
import com.josue.kingdom.invitation.entity.Invitation;
import com.josue.kingdom.invitation.entity.InvitationStatus;
import com.josue.kingdom.rest.ListResource;
import com.josue.kingdom.rest.ListResourceUtils;
import com.josue.kingdom.rest.ex.AuthorizationException;
import com.josue.kingdom.rest.ex.InvalidResourceArgException;
import com.josue.kingdom.rest.ex.ResourceAlreadyExistsException;
import com.josue.kingdom.rest.ex.ResourceNotFoundException;
import com.josue.kingdom.rest.ex.RestException;
import com.josue.kingdom.security.Current;
import com.josue.kingdom.security.KingdomSecurity;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

/**
 *
 * @author Josue
 */
@ApplicationScoped
public class InvitationControl {

    @Inject
    @Current
    KingdomSecurity security;

    @Inject
    InvitationRepository invitationRepository;

    @Inject
    CredentialRepository credentialRepository;

    @Inject
    DomainRepository domainRepository;

    @Inject
    Event<Invitation> invitatioEvent;

    //TODO add use cases:
    //a manager can invite a given email for a given domain once, he can resend the the invitation a number of times (e.g. 3)
    //a manager can invite the same email for different domains, each invite will be a separated process
    //TODO update test cases
    //TODO on expires... delete created manager
    @Transactional(Transactional.TxType.REQUIRED)
    public Invitation createInvitation(Invitation invitation) throws RestException {
        //TODO add bean validation here ?
        if (invitation.getDomain() == null) {
            throw new InvalidResourceArgException(Invitation.class, "domain", null);
        }
        if (invitation.getTargetManager() == null) {
            throw new InvalidResourceArgException(Manager.class, "targetManager", null);
        }
        if (invitation.getTargetManager().getEmail() == null) {// TODO validate email
            throw new InvalidResourceArgException(Manager.class, "targetManager.email", null);
        }//TODO validate another if null ??? bean validation ?
        if (invitation.getPermission() == null) {
            throw new InvalidResourceArgException(Manager.class, "permission", null);
        }
        if (invitation.getPermission().getUuid() == null) {
            throw new InvalidResourceArgException(Manager.class, "permission.uuid", null);
        }

        Domain foundDomain = invitationRepository.find(Domain.class, security.getCurrentApplication().getUuid(), invitation.getDomain().getUuid());
        if (foundDomain == null) {
            throw new ResourceNotFoundException(Domain.class, invitation.getDomain().getUuid());
        } else if (!foundDomain.getOwner().equals(security.getCurrentManager())) {
            throw new AuthorizationException("You must be the Domain owner to invite someone");
        }
        Invitation existingInvitation = invitationRepository.getInvitation(security.getCurrentApplication().getUuid(), invitation.getDomain().getUuid(), invitation.getTargetManager().getUuid());
        if (existingInvitation != null) {//already exist an invitation for this domain of the same targetemail... resend ?
            throw new ResourceAlreadyExistsException(Invitation.class, "targetEmail", invitation.getTargetManager().getEmail());
        }

        DomainPermission permission = invitationRepository.find(DomainPermission.class, security.getCurrentApplication().getUuid(), invitation.getPermission().getUuid());

        invitation.removeNonCreatable();
        invitation.setStatus(InvitationStatus.CREATED);
        invitation.setValidUntil(getInvitationExprirationDate());
        invitation.setDomain(foundDomain);
        invitation.setAuthorManager(security.getCurrentManager());
        invitation.setPermission(permission);
        invitation.setToken(UUID.randomUUID().toString());
        invitation.setApplication(security.getCurrentApplication());

        Manager manager = credentialRepository.getManagerByEmail(foundDomain.getApplication().getUuid(), invitation.getTargetManager().getEmail());
        if (manager != null) {// manager or 'empty invitation manager' already exist
            if (manager.equals(security.getCurrentManager())) {//self invitation... this is not allowed
                throw new InvalidResourceArgException(Invitation.class, "You cannot invit yourself");
            }
            invitation.setTargetManager(manager);
            Domain joinedDomain = domainRepository.getJoinedDomain(security.getCurrentApplication().getUuid(), manager.getUuid(), foundDomain.getUuid());
            if (joinedDomain != null) { //User already joined to Domain
                throw new RestException(Manager.class, manager.getUuid(), "Already joined to domain", Response.Status.BAD_REQUEST);
            }
        } else { //create a new 'empty' manager
            invitation.getTargetManager().removeNonCreatable();
            invitation.getTargetManager().setPassword(new BigInteger(130, new SecureRandom()).toString(16));
            invitation.getTargetManager().setStatus(AccountStatus.PROVISIONING);
            invitation.getTargetManager().setApplication(security.getCurrentApplication());
            invitationRepository.create(invitation.getTargetManager());
        }

        //TODO this should run within the same TX... check CDI observer for event on commit success !!!
        invitationRepository.create(invitation);
        invitatioEvent.fire(invitation);

        return invitation;
    }

    //Internal only, theres no reason for user update an invitation
    public Invitation updateInvitation(String uuid, Invitation inv) throws RestException {
        Invitation invitation = invitationRepository.find(Invitation.class, security.getCurrentApplication().getUuid(), uuid);
        if (invitation == null) {
            throw new ResourceNotFoundException(Invitation.class, uuid);
        }
        invitation.copyUpdatable(inv);
        Invitation updatedInvitation = invitationRepository.update(invitation);
        return updatedInvitation;

    }

    public Invitation getInvitation(String uuid) throws RestException {
        Invitation foundInvitation = invitationRepository.find(Invitation.class, security.getCurrentApplication().getUuid(), uuid);
        if (foundInvitation == null) {
            throw new ResourceNotFoundException(Invitation.class, uuid);
        }
        return foundInvitation;
    }

    //TODO update how to get an appUuid, specially for resources that dont require an authenticated user
    public Invitation getInvitationByToken(String token) throws RestException {
        Invitation foundInvitation = invitationRepository.getInvitationByToken(security.getCurrentApplication().getUuid(), token);
        if (foundInvitation == null) {
            throw new ResourceNotFoundException(Invitation.class, "token", token);
        }
        return foundInvitation;
    }

    //TODO should do all signup logic, also chanage the name and the response, for better method definitions
    public boolean isSignup(String token) throws RestException {
        //Here invitation can return null for non existing tokens
        Invitation invitation = getInvitationByToken(token);
        Manager foundManager = credentialRepository.getManagerByEmail(invitation.getApplication().getUuid(), invitation.getTargetManager().getEmail());
        return foundManager.getStatus().equals(AccountStatus.PROVISIONING);
    }

    public ListResource<Invitation> getInvitations(Integer limit, Integer offset) throws RestException {
        List<Invitation> invitations = invitationRepository.getInvitations(security.getCurrentApplication().getUuid(), security.getCurrentManager().getUuid(), limit, offset);
        long invitationsCount = invitationRepository.getInvitationsCount(security.getCurrentApplication().getUuid(), security.getCurrentManager().getUuid());
        return ListResourceUtils.buildListResource(invitations, invitationsCount, limit, offset);
    }

    private Date getInvitationExprirationDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        return calendar.getTime();
    }

}
