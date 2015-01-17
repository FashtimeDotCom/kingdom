/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.invitation;

import com.josue.kingdom.credential.CredentialRepository;
import com.josue.kingdom.credential.entity.Credential;
import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.domain.entity.Domain;
import com.josue.kingdom.domain.entity.DomainPermission;
import com.josue.kingdom.invitation.entity.Invitation;
import com.josue.kingdom.invitation.entity.InvitationStatus;
import com.josue.kingdom.rest.ListResource;
import com.josue.kingdom.rest.ex.ResourceNotFoundException;
import com.josue.kingdom.util.ListResourceUtil;
import com.josue.kingdom.util.cdi.Current;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 *
 * @author Josue
 */
@ApplicationScoped
public class InvitationControl {

    @Inject
    @Current
    Credential credential;

    @Inject
    InvitationRepository repository;

    @Inject
    CredentialRepository credentialRepository;

    @Inject
    InvitationService service;

    //TODO make this application robust using validator, ere we have several possibles NPE, fix this ASAP
    public Invitation createInvitation(Invitation invitation) {
        Domain foundDomain = repository.find(Domain.class, invitation.getDomain().getUuid());
        DomainPermission permission = repository.find(DomainPermission.class, invitation.getPermission().getUuid());

        invitation.removeNonCreatable();
        invitation.setStatus(InvitationStatus.CREATED);
        invitation.setValidUntil(getInvitationExprirationDate());
        invitation.setDomain(foundDomain);
        invitation.setAuthorManager(credential.getManager());
        invitation.setPermission(permission);
        invitation.setToken(UUID.randomUUID().toString());

        Manager manager = credentialRepository.getManagerByEmail(invitation.getTargetEmail());
        if (manager == null) {
            //Manager should fill form before acion completes
        } else {
            //Manager already exists, just add the Domain
        }

        //TODO this should run within the same TX... check CDI observer for event on commit success !!!
        repository.create(invitation);
        service.sendInvitation(invitation);
        return invitation;
    }

    public Invitation updateInvitation(String uuid, Invitation inv) throws ResourceNotFoundException {
        Invitation invitation = repository.find(Invitation.class, uuid);
        if (invitation == null) {
            throw new ResourceNotFoundException(Invitation.class, uuid);
        }
        invitation.copyUpdatable(inv);
        Invitation updatedInvitation = repository.update(invitation);
        return updatedInvitation;

    }

    public Invitation getInvitation(String uuid) {
        return repository.find(Invitation.class, uuid);
    }

    public Invitation getInvitationByToken(String token) {
        return repository.getInvitationByToken(token);
    }

    //TODO should do all signup logic, also chanage the name and the response, for better method definitions
    public boolean isSignup(String token) {
        //Here invitation can return null for non existing tokens
        Invitation invitation = getInvitationByToken(token);
        Manager foundManager = credentialRepository.getManagerByEmail(invitation.getTargetEmail());
        return foundManager == null;
    }

    public ListResource<Invitation> getInvitations(Integer limit, Integer offset) {
        List<Invitation> invitations = repository.getInvitations(credential.getManager().getUuid(), limit, offset);
        long invitationsCount = repository.getInvitationsCount(credential.getManager().getUuid());
        return ListResourceUtil.buildListResource(invitations, invitationsCount, limit, offset);
    }

    private Date getInvitationExprirationDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        return calendar.getTime();
    }

}
