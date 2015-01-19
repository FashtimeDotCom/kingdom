/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.invitation;

import com.josue.kingdom.credential.CredentialRepository;
import com.josue.kingdom.credential.entity.Credential;
import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.credential.entity.ManagerCredential;
import com.josue.kingdom.domain.DomainRepository;
import com.josue.kingdom.domain.entity.Domain;
import com.josue.kingdom.domain.entity.DomainPermission;
import com.josue.kingdom.invitation.entity.Invitation;
import com.josue.kingdom.invitation.entity.InvitationStatus;
import com.josue.kingdom.rest.ListResource;
import com.josue.kingdom.rest.ex.AuthorizationException;
import com.josue.kingdom.rest.ex.InvalidResourceArgException;
import com.josue.kingdom.rest.ex.ResourceNotFoundException;
import com.josue.kingdom.rest.ex.RestException;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

/**
 *
 * @author Josue
 */
public class InvitationControlTest {

    @Spy
    Credential credential = new ManagerCredential();

    @Mock
    Manager manager;

    @Mock
    InvitationRepository invitationRepository;

    @Mock
    CredentialRepository credentialRepository;

    @Mock
    InvitationService service;

    @Mock
    DomainRepository domainRepository;

    @InjectMocks
    InvitationControl control = new InvitationControl();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        credential.setManager(manager);
        control.credential = credential;
    }

    @Test
    public void testCreateInvitation() throws RestException {

        Domain domain = Mockito.mock(Domain.class);
        DomainPermission permission = Mockito.mock(DomainPermission.class);

        Invitation invitation = Mockito.spy(new Invitation());
        invitation.setDomain(domain);
        invitation.setPermission(permission);
        String targetEmail = "test@email.com";
        invitation.setTargetEmail(targetEmail);

        when(domain.getOwner()).thenReturn(manager);
        when(invitationRepository.find(Domain.class, domain.getUuid())).thenReturn(domain);
        when(invitationRepository.find(DomainPermission.class, permission.getUuid())).thenReturn(permission);
        //Manager doesnt exist yet...
        when(credentialRepository.getManagerByEmail(targetEmail)).thenReturn(null);

        Invitation createdInvitation = control.createInvitation(invitation);
        assertEquals(InvitationStatus.CREATED, createdInvitation.getStatus());
        assertNotNull(invitation.getToken());
        assertNotNull(invitation.getValidUntil());
        assertEquals(targetEmail, createdInvitation.getTargetEmail());

        verify(invitation, times(1)).removeNonCreatable();
        verify(service, times(1)).sendInvitation(createdInvitation);

    }

    @Test(expected = InvalidResourceArgException.class)
    public void testCreateInvitationInvalidDomain() throws RestException {
        Invitation invitation = Mockito.mock(Invitation.class);
        when(invitation.getDomain()).thenReturn(null);
        control.createInvitation(invitation);
        fail();
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testCreateInvitationDomainNotFound() throws RestException {
        Invitation invitation = Mockito.mock(Invitation.class);
        Domain domain = Mockito.mock(Domain.class);

        when(invitation.getDomain()).thenReturn(domain);
        when(invitationRepository.find(Domain.class, domain.getUuid())).thenReturn(null);
        control.createInvitation(invitation);
        fail();
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateInvitationNotAuthorized() throws RestException {
        Invitation invitation = Mockito.mock(Invitation.class);
        Domain domain = Mockito.mock(Domain.class);

        Manager anotherManager = Mockito.mock(Manager.class);

        when(invitation.getDomain()).thenReturn(domain);
        when(invitationRepository.find(Domain.class, domain.getUuid())).thenReturn(domain);
        when(domain.getOwner()).thenReturn(anotherManager);

        control.createInvitation(invitation);
        fail();
    }

    @Test(expected = RestException.class)
    public void testCreateInvitationAlreadyJoined() throws RestException {

        Domain domain = Mockito.mock(Domain.class);
        DomainPermission permission = Mockito.mock(DomainPermission.class);

        Invitation invitation = Mockito.spy(new Invitation());
        invitation.setDomain(domain);
        invitation.setPermission(permission);
        String targetEmail = "test@email.com";
        invitation.setTargetEmail(targetEmail);

        Manager existingManager = Mockito.mock(Manager.class);

        when(domain.getOwner()).thenReturn(manager);
        when(invitationRepository.find(Domain.class, domain.getUuid())).thenReturn(domain);
        when(invitationRepository.find(DomainPermission.class, permission.getUuid())).thenReturn(permission);
        //Manager already exist
        when(credentialRepository.getManagerByEmail(targetEmail)).thenReturn(existingManager);
        //User already joined to domain
        when(domainRepository.getJoinedDomain(existingManager.getUuid(), domain.getUuid())).thenReturn(domain);
        control.createInvitation(invitation);
        fail();
    }

    @Test
    public void testCreateInvitationExistingManager() throws RestException {

        Domain domain = Mockito.mock(Domain.class);
        DomainPermission permission = Mockito.mock(DomainPermission.class);

        Invitation invitation = Mockito.spy(new Invitation());
        invitation.setDomain(domain);
        invitation.setPermission(permission);
        String targetEmail = "test@email.com";
        invitation.setTargetEmail(targetEmail);

        Manager existingManager = Mockito.mock(Manager.class);

        when(domain.getOwner()).thenReturn(manager);
        when(invitationRepository.find(Domain.class, domain.getUuid())).thenReturn(domain);
        when(invitationRepository.find(DomainPermission.class, permission.getUuid())).thenReturn(permission);
        //Manager already exist
        when(credentialRepository.getManagerByEmail(targetEmail)).thenReturn(existingManager);
        //User not joined to domain yet
        when(domainRepository.getJoinedDomain(existingManager.getUuid(), domain.getUuid())).thenReturn(null);

        Invitation createdInvitation = control.createInvitation(invitation);
        assertEquals(InvitationStatus.CREATED, createdInvitation.getStatus());
        assertNotNull(invitation.getToken());
        assertNotNull(invitation.getValidUntil());
        assertEquals(targetEmail, createdInvitation.getTargetEmail());

        verify(invitation, times(1)).removeNonCreatable();
        verify(service, times(1)).sendInvitation(createdInvitation);

    }

    @Test(expected = ResourceNotFoundException.class)
    public void testUpdateInvitationNotFound() throws RestException {

        String invitationUuid = "uuid-123";
        Invitation invitation = Mockito.mock(Invitation.class);

        when(invitationRepository.find(Invitation.class, invitationUuid)).thenReturn(null);
        control.updateInvitation(invitationUuid, invitation);
        fail();
    }

    public void testUpdateInvitation() throws RestException {

        String invitationUuid = "uuid-123";
        Invitation invitation = Mockito.mock(Invitation.class);
        Invitation foundInvitation = Mockito.mock(Invitation.class);

        when(invitationRepository.find(Invitation.class, invitationUuid)).thenReturn(foundInvitation);
        Invitation updatedInvitation = control.updateInvitation(invitationUuid, invitation);
        verify(foundInvitation).copyUpdatable(invitation);
        verify(invitationRepository).update(foundInvitation);
        assertEquals(foundInvitation, updatedInvitation);
    }

    @Test
    public void testGetInvitation() throws RestException {
        Invitation invitation = Mockito.mock(Invitation.class);
        String invitationUuid = "inv-123";

        when(invitationRepository.find(Invitation.class, invitationUuid)).thenReturn(invitation);
        Invitation foundInvitation = control.getInvitation(invitationUuid);
        verify(invitationRepository).find(Invitation.class, invitationUuid);
        assertEquals(invitation, foundInvitation);
    }

    @Test
    public void testGetInvitationByToken() throws RestException {
        Invitation invitation = Mockito.mock(Invitation.class);
        String token = "inv-123";

        when(invitationRepository.getInvitationByToken(token)).thenReturn(invitation);
        Invitation foundInvitation = control.getInvitationByToken(token);
        verify(invitationRepository).getInvitationByToken(token);
        assertEquals(invitation, foundInvitation);
    }

    @Test
    public void testIsSignup() throws RestException {
        String token = "inv-123";
        Invitation invitation = Mockito.mock(Invitation.class);
        Manager foundManager = Mockito.mock(Manager.class);

        when(invitationRepository.getInvitationByToken(token)).thenReturn(invitation);
        when(credentialRepository.getManagerByEmail(invitation.getTargetEmail())).thenReturn(foundManager);

        boolean isSignup = control.isSignup(token);
        assertFalse(isSignup);

        when(credentialRepository.getManagerByEmail(invitation.getTargetEmail())).thenReturn(null);

        boolean isnotSignup = control.isSignup(token);
        assertTrue(isnotSignup);
    }

    @Test
    public void testGetInvitations() {

        int limit = 15;
        int offset = 0;

        long count = 10;
        List<Invitation> invitations = Mockito.mock(List.class);
        when(invitationRepository.getInvitations(manager.getUuid(), limit, offset)).thenReturn(invitations);
        when(invitationRepository.getInvitationsCount(manager.getUuid())).thenReturn(count);

        ListResource<Invitation> foundInvitations = control.getInvitations(limit, offset);
        assertEquals(count, foundInvitations.getTotalCount());
        assertEquals(invitations, foundInvitations.getItems());
    }

}
