/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.invitation;

import com.josue.credential.manager.auth.credential.Credential;
import com.josue.credential.manager.auth.domain.Domain;
import com.josue.credential.manager.auth.manager.Manager;
import com.josue.credential.manager.auth.role.Role;
import com.josue.credential.manager.auth.util.Current;
import com.josue.credential.manager.business.ListResourceUtil;
import com.josue.credential.manager.business.account.AccountRepository;
import com.josue.credential.manager.rest.ListResource;
import com.josue.credential.manager.rest.ex.ResourceNotFoundException;
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
    AccountRepository accountRepository;

    @Inject
    InvitationService service;

    //TODO make this application robust using validator, ere we have several possibles NPE, fix this ASAP
    public ManagerInvitation create(ManagerInvitation invitation) {
        Domain foundDomain = repository.find(Domain.class, invitation.getDomain().getUuid());
        Role role = repository.find(Role.class, invitation.getRole().getId());

        invitation.removeNonCreatableFields();
        invitation.setStatus(ManagerInvitationStatus.CREATED);
        invitation.setValidUntil(getInvitationExprirationDate());
        invitation.setDomain(foundDomain);
        invitation.setAuthorManager(credential.getManager());
        invitation.setRole(role);
        invitation.setToken(UUID.randomUUID().toString());

        Manager manager = accountRepository.findManagerByEmail(invitation.getTargetEmail());
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

    public ManagerInvitation update(String uuid, ManagerInvitation inv) throws ResourceNotFoundException {
        ManagerInvitation invitation = repository.find(ManagerInvitation.class, uuid);
        if (invitation == null) {
            throw new ResourceNotFoundException(ManagerInvitation.class, uuid);
        }
        invitation.copyUpdatebleFields(inv);
        ManagerInvitation updatedInvitation = repository.edit(invitation);
        return updatedInvitation;

    }

    public ManagerInvitation getInvitation(String uuid) {
        return repository.find(ManagerInvitation.class, uuid);
    }

    public ManagerInvitation getInvitationByToken(String token) {
        return repository.getInvitationByToken(token);
    }

    //TODO should do all signup logic, also chanage the name and the response, for better method definitions
    public boolean isSignup(String token) {
        //Here invitation can return null for non existing tokens
        ManagerInvitation invitation = getInvitationByToken(token);
        Manager foundManager = accountRepository.findManagerByEmail(invitation.getTargetEmail());
        return foundManager == null;
    }

    public ListResource<ManagerInvitation> getInvitations(Integer limit, Integer offset) {
        List<ManagerInvitation> invitations = repository.getInvitations(credential.getManager().getUuid(), limit, offset);
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
