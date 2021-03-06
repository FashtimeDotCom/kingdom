/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.invitation;

import com.josue.kingdom.application.entity.Application;
import com.josue.kingdom.credential.CredentialRepository;
import com.josue.kingdom.credential.entity.AccountStatus;
import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.domain.DomainRepository;
import com.josue.kingdom.domain.entity.Domain;
import com.josue.kingdom.domain.entity.DomainPermission;
import com.josue.kingdom.invitation.entity.Invitation;
import com.josue.kingdom.rest.ListResource;
import com.josue.kingdom.rest.ex.InvalidResourceArgException;
import com.josue.kingdom.rest.ex.ResourceAlreadyExistsException;
import com.josue.kingdom.rest.ex.ResourceNotFoundException;
import com.josue.kingdom.rest.ex.RestException;
import com.josue.kingdom.security.KingdomSecurity;
import com.josue.kingdom.util.KingdomUtils;
import java.util.List;
import java.util.UUID;
import javax.enterprise.event.Event;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
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

    @Mock
    KingdomUtils utils;

    @Spy
    KingdomSecurity security;

    @Mock
    InvitationRepository invitationRepository;

    @Mock
    CredentialRepository credentialRepository;

    @Mock
    InvitationService service;

    @Mock
    DomainRepository domainRepository;

    @Mock
    Event<Invitation> invitatioEvent;

    @InjectMocks
    InvitationControl control = new InvitationControl();

    private Manager currentManager;
    private Application currentApplication;

    @Before
    public void init() {
        Application currentApp = new Application();
        currentApp.setUuid("application-uuid");

        Manager currentMan = new Manager();
        currentMan.setUuid(UUID.randomUUID().toString());
        currentMan.setEmail("current-manager@email.com");
        currentMan.setFirstName("Current");
        currentMan.setLastName("Manager");
        currentMan.setPassword("current-manager-psw");
        currentMan.setStatus(AccountStatus.ACTIVE);
        currentMan.setUsername("current-manager");
        currentMan.setApplication(currentApp);

        security = new KingdomSecurity(currentApp, currentMan, KingdomSecurity.ManagerStatus.AUTHENTICATED);
        currentManager = currentMan;
        currentApplication = currentApp;

        MockitoAnnotations.initMocks(this);

    }

    @Test(expected = InvalidResourceArgException.class)
    public void testCreateInvitationNullDomain() throws RestException {
        Invitation invitation = Mockito.spy(new Invitation());
        control.createInvitation(invitation);
        fail();
    }

    @Test(expected = InvalidResourceArgException.class)
    public void testCreateInvitationNullTargetManager() throws RestException {
        Invitation invitation = Mockito.spy(new Invitation());
        invitation.setDomain(new Domain());
        control.createInvitation(invitation);
        fail();
    }

    @Test(expected = InvalidResourceArgException.class)
    public void testCreateInvitationEmptyEmail() throws RestException {
        Invitation invitation = Mockito.spy(new Invitation());
        invitation.setDomain(new Domain());
        invitation.setTargetManager(new Manager());
        control.createInvitation(invitation);
        fail();
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
        Invitation invitation = Mockito.spy(new Invitation());
        invitation.setDomain(new Domain());
        invitation.setTargetManager(new Manager());
        invitation.getTargetManager().setEmail("test@email.com");
        invitation.setPermission(new DomainPermission());
        invitation.getPermission().setUuid("any-uuid");

        when(invitationRepository.find(Domain.class, currentApplication.getUuid(), invitation.getDomain().getUuid())).thenReturn(null);
        control.createInvitation(invitation);
        fail();
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testUpdateInvitationNotFound() throws RestException {

        String invitationUuid = "uuid-123";
        Invitation invitation = Mockito.mock(Invitation.class);

        when(invitationRepository.find(Invitation.class, currentApplication.getUuid(), invitationUuid)).thenReturn(null);
        control.updateInvitation(invitationUuid, invitation);
        fail();
    }

    @Test(expected = ResourceAlreadyExistsException.class)
    public void testCreateInvitationAlreadyExists() throws RestException {
        Domain domain = Mockito.spy(new Domain());
        domain.setOwner(currentManager);
        domain.setApplication(security.getCurrentApplication());

        Manager targetManager = Mockito.spy(new Manager());

        Invitation invitation = Mockito.spy(new Invitation());
        invitation.setDomain(domain);
        invitation.setTargetManager(targetManager);
        invitation.getTargetManager().setEmail("test@email.com");
        invitation.setApplication(security.getCurrentApplication());
        invitation.setPermission(new DomainPermission());
        invitation.getPermission().setUuid("any-uuid");

        when(invitationRepository.find(Domain.class, currentApplication.getUuid(), invitation.getDomain().getUuid())).thenReturn(domain);
        when(credentialRepository.getManagerByEmail(currentApplication.getUuid(), targetManager.getEmail())).thenReturn(targetManager);
        when(invitationRepository.getInvitation(currentApplication.getUuid(), domain.getUuid(), targetManager.getEmail())).thenReturn(new Invitation());
        control.createInvitation(invitation);
        fail();
    }

    @Test(expected = RestException.class)
    public void testCreateInvitationAreadyJoined() throws RestException {
        Domain domain = Mockito.spy(new Domain());
        domain.setOwner(currentManager);
        domain.setApplication(security.getCurrentApplication());

        Manager targetManager = Mockito.spy(new Manager());

        Invitation invitation = Mockito.spy(new Invitation());
        invitation.setDomain(domain);
        invitation.setTargetManager(targetManager);
        invitation.getTargetManager().setEmail("test@email.com");
        invitation.setApplication(security.getCurrentApplication());
        invitation.setPermission(new DomainPermission());
        invitation.getPermission().setUuid("any-uuid");

        when(invitationRepository.find(Domain.class, currentApplication.getUuid(), invitation.getDomain().getUuid())).thenReturn(domain);
        when(credentialRepository.getManagerByEmail(currentApplication.getUuid(), targetManager.getEmail())).thenReturn(targetManager);
        when(domainRepository.getJoinedDomain(currentApplication.getUuid(), targetManager.getUuid(), domain.getUuid())).thenReturn(new Domain());//already joined
        control.createInvitation(invitation);
        fail();
    }

    @Test
    public void testCreateInvitationExistentManager() throws RestException {
        Domain domain = Mockito.spy(new Domain());
        domain.setOwner(currentManager);
        domain.setApplication(security.getCurrentApplication());

        Manager targetManager = Mockito.spy(new Manager());

        Invitation invitation = Mockito.spy(new Invitation());
        invitation.setDomain(domain);
        invitation.setTargetManager(targetManager);
        invitation.getTargetManager().setEmail("test@email.com");
        invitation.setApplication(security.getCurrentApplication());
        invitation.setPermission(new DomainPermission());
        invitation.getPermission().setUuid("any-uuid");

        when(invitationRepository.find(Domain.class, currentApplication.getUuid(), invitation.getDomain().getUuid())).thenReturn(domain);
        when(credentialRepository.getManagerByEmail(currentApplication.getUuid(), targetManager.getEmail())).thenReturn(targetManager);
        when(domainRepository.getJoinedDomain(currentApplication.getUuid(), targetManager.getUuid(), domain.getUuid())).thenReturn(null);

        control.createInvitation(invitation);

        verify(invitation).removeNonCreatable();
        verify(invitation, times(2)).setTargetManager(targetManager);//test(1) + control(1)
        verify(invitationRepository, times(0)).create(invitation.getTargetManager());

        verify(invitationRepository).create(invitation);
        verify(invitatioEvent).fire(any(Invitation.class));
    }

    @Test
    public void testCreateInvitationNewManager() throws RestException {
        Domain domain = Mockito.spy(new Domain());
        domain.setOwner(currentManager);
        domain.setApplication(security.getCurrentApplication());

        Manager targetManager = Mockito.spy(new Manager());

        Invitation invitation = Mockito.spy(new Invitation());
        invitation.setDomain(domain);
        invitation.setTargetManager(targetManager);
        invitation.getTargetManager().setEmail("test@email.com");
        invitation.setApplication(security.getCurrentApplication());
        invitation.setPermission(new DomainPermission());
        invitation.getPermission().setUuid("any-uuid");

        when(invitationRepository.find(Domain.class, currentApplication.getUuid(), invitation.getDomain().getUuid())).thenReturn(domain);
        when(credentialRepository.getManagerByEmail(currentApplication.getUuid(), targetManager.getEmail())).thenReturn(null);

        control.createInvitation(invitation);

        verify(invitation).removeNonCreatable();
        verify(invitation).setTargetManager(targetManager);//test(1) + control(1)

        verify(targetManager).setPassword(any(String.class));
        verify(targetManager).setStatus(AccountStatus.PROVISIONING);
        verify(invitationRepository).create(invitation.getTargetManager());

        verify(invitationRepository).create(invitation);
        verify(invitatioEvent).fire(any(Invitation.class));
    }

    @Test
    public void testUpdateInvitation() throws RestException {

        String invitationUuid = "uuid-123";
        Invitation invitation = Mockito.mock(Invitation.class);
        Invitation foundInvitation = Mockito.mock(Invitation.class);
        Invitation updatedInvitation = Mockito.mock(Invitation.class);

        when(invitationRepository.find(Invitation.class, currentApplication.getUuid(), invitationUuid)).thenReturn(foundInvitation);
        when(invitationRepository.update(foundInvitation)).thenReturn(updatedInvitation);
        Invitation invResponse = control.updateInvitation(invitationUuid, invitation);
        verify(foundInvitation).copyUpdatable(invitation);
        verify(invitationRepository).update(foundInvitation);
        assertNotNull(invResponse);
        assertEquals(updatedInvitation, invResponse);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetInvitationNotFound() throws RestException {
        String invitationUuid = "inv-123";
        when(invitationRepository.find(Invitation.class, currentApplication.getUuid(), invitationUuid)).thenReturn(null);
        control.getInvitation(invitationUuid);
        fail();
    }

    @Test
    public void testGetInvitation() throws RestException {
        Invitation invitation = Mockito.mock(Invitation.class);
        String invitationUuid = "inv-123";

        when(invitationRepository.find(Invitation.class, currentApplication.getUuid(), invitationUuid)).thenReturn(invitation);
        Invitation foundInvitation = control.getInvitation(invitationUuid);
        verify(invitationRepository).find(Invitation.class, currentApplication.getUuid(), invitationUuid);
        assertEquals(invitation, foundInvitation);
    }

    @Test
    public void testGetInvitationByToken() throws RestException {
        Invitation invitation = Mockito.mock(Invitation.class);
        String token = "inv-123";

        when(invitationRepository.getInvitationByToken(currentApplication.getUuid(), token)).thenReturn(invitation);
        Invitation foundInvitation = control.getInvitationByToken(token);
        verify(invitationRepository).getInvitationByToken(currentApplication.getUuid(), token);
        assertEquals(invitation, foundInvitation);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetInvitationByTokenNotFound() throws RestException {
        String token = "inv-123";
        when(invitationRepository.getInvitationByToken(currentApplication.getUuid(), token)).thenReturn(null);
        control.getInvitationByToken(token);
        fail();
    }

    @Test
    public void testIsSignup() throws RestException {
        String token = "inv-123";
        Invitation invitation = Mockito.spy(new Invitation());

        Manager foundManager = Mockito.mock(Manager.class);
        invitation.setTargetManager(foundManager);

        Application application = Mockito.mock(Application.class);
        invitation.setApplication(application);

        when(invitationRepository.getInvitationByToken(currentApplication.getUuid(), token)).thenReturn(invitation);
        when(credentialRepository.getManagerByEmail(invitation.getApplication().getUuid(), invitation.getTargetManager().getEmail())).thenReturn(foundManager);
        when(foundManager.getStatus()).thenReturn(AccountStatus.ACTIVE);

        boolean isSignup = control.isSignup(token);
        assertFalse(isSignup);

        when(foundManager.getStatus()).thenReturn(AccountStatus.PROVISIONING);

        boolean isnotSignup = control.isSignup(token);
        assertTrue(isnotSignup);
    }

    @Test
    public void testGetInvitations() throws RestException {

        int limit = 15;
        int offset = 0;

        long count = 10;
        List<Invitation> invitations = Mockito.mock(List.class);
        when(invitationRepository.getInvitations(currentApplication.getUuid(), currentManager.getUuid(), limit, offset)).thenReturn(invitations);
        when(invitationRepository.getInvitationsCount(currentApplication.getUuid(), currentManager.getUuid())).thenReturn(count);

        ListResource<Invitation> foundInvitations = control.getInvitations(limit, offset);
        assertEquals(count, foundInvitations.getTotalCount());
        assertEquals(invitations, foundInvitations.getItems());
    }

}
