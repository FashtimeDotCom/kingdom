/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.credential;

import com.josue.kingdom.application.entity.Application;
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
import com.josue.kingdom.rest.ex.AuthorizationException;
import com.josue.kingdom.rest.ex.InvalidResourceArgException;
import com.josue.kingdom.rest.ex.ResourceAlreadyExistsException;
import com.josue.kingdom.rest.ex.ResourceNotFoundException;
import com.josue.kingdom.rest.ex.RestException;
import com.josue.kingdom.security.KingdomSecurity;
import com.josue.kingdom.security.manager.ManagerToken;
import com.josue.kingdom.util.KingdomUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.enterprise.event.Event;
import javax.xml.bind.DatatypeConverter;
import org.apache.shiro.authz.Permission;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class CredentialControlTest {

    private static final Integer DEFAULT_LIMIT = 100;
    private static final Integer DEFAULT_OFFSET = 0;

    @Mock
    CredentialRepository credentialRepository;

    @Mock
    Event<Object> mockEvent;

    @Mock
    InvitationRepository invRepository;

    @Mock
    DomainRepository domainRespository;

    @Mock
    KingdomUtils utils;

    @Spy
    KingdomSecurity security;

    @InjectMocks
    CredentialControl control = Mockito.spy(new CredentialControl());

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

    @Test
    public void testGetManagers() {
        long totalCount = 50;

        List<Manager> managers = Mockito.mock(List.class);
        when(credentialRepository.getManagers(currentApplication.getUuid(), DEFAULT_LIMIT, DEFAULT_OFFSET)).thenReturn(managers);
        when(credentialRepository.count(Manager.class, currentApplication.getUuid())).thenReturn(totalCount);

        ListResource<Manager> foundManagers = control.getManagers(DEFAULT_LIMIT, DEFAULT_OFFSET);
        assertEquals(totalCount, foundManagers.getTotalCount());
        assertEquals(managers, foundManagers.getItems());

    }

    @Test(expected = RestException.class)
    public void testPasswordResetManagerNotFound() throws RestException {
        String username = "a-username";

        PasswordChangeEvent event = Mockito.spy(new PasswordChangeEvent());
        event.setNewPassword("new-psw-123");
        event.setToken("token-123");

        when(credentialRepository.getManagerByUsername(currentApplication.getUuid(), username)).thenReturn(null);
        control.updateManagerPassword(username, event);
        fail();
    }

    @Test
    public void testCreatePasswordChangeEvent() throws RestException {
        String uuid = "uuid123";
        Manager man = new Manager();
        man.setUuid(uuid);
        man.setEmail("josue@email.com");
        man.setUuid("uuid");
        man.setUsername("username");
        man.setPassword("pass123");
        Manager spyManager = Mockito.spy(man);

        List<PasswordChangeEvent> events = Mockito.spy(new ArrayList<PasswordChangeEvent>());
        PasswordChangeEvent event = Mockito.mock(PasswordChangeEvent.class);
        events.add(event);

        String token = "token-123";

        when(credentialRepository.getManagerByUsername(currentApplication.getUuid(), man.getUsername())).thenReturn(spyManager);
        when(credentialRepository.getPasswordResetEvents(currentApplication.getUuid(), man.getUuid())).thenReturn(events);
        when(utils.generateBase64FromUuid()).thenReturn(token);
        control.createPasswordChangeEvent(man.getUsername());

        PasswordChangeEvent createdEvent = new PasswordChangeEvent(spyManager, token);
        verify(event).setIsValid(false);
        verify(credentialRepository).create(createdEvent);
        verify(mockEvent).fire(createdEvent);

    }

    @Test(expected = ResourceNotFoundException.class)
    public void testUpdateManagerPasswordNotFound() throws RestException {
        String username = "a-username";
        PasswordChangeEvent event = Mockito.spy(new PasswordChangeEvent());
        event.setNewPassword("new-psw-123");
        event.setToken("token-123");

        when(credentialRepository.getPasswordResetEvent(security.getCurrentApplication().getUuid(), event.getToken())).thenReturn(null);
        control.updateManagerPassword(username, event);
        fail();
    }

    @Test(expected = RestException.class)
    public void testUpdateManagerPasswordInvalid() throws RestException {
        String username = "a-username";
        PasswordChangeEvent event = Mockito.spy(new PasswordChangeEvent());
        event.setNewPassword("new-psw-123");
        event.setToken("token-123");
        event.setIsValid(false);

        when(credentialRepository.getPasswordResetEvent(security.getCurrentApplication().getUuid(), event.getToken())).thenReturn(event);
        control.updateManagerPassword(username, event);
        fail();
    }

    @Test(expected = RestException.class)
    public void testUpdateManagerPasswordExpired() throws RestException {
        String username = "a-wrong-username"; //correct username is: current-manager
        PasswordChangeEvent event = Mockito.spy(new PasswordChangeEvent());
        event.setNewPassword("new-psw-123");
        event.setToken("token-123");
        event.setIsValid(false);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -10);

        event.setIsValid(true);
        event.setValidUntil(calendar.getTime());
        Manager manager = new Manager();
        manager.setUsername("a-username");
        event.setTargetManager(manager);

        when(credentialRepository.getPasswordResetEvent(security.getCurrentApplication().getUuid(), event.getToken())).thenReturn(event);
        control.updateManagerPassword(username, event);
        fail();
    }

    @Test(expected = RestException.class)
    public void testUpdateManagerInvalidUsername() throws RestException {
        PasswordChangeEvent event = Mockito.spy(new PasswordChangeEvent());
        event.setNewPassword("new-psw-123");
        event.setToken("token-123");
        event.setIsValid(false);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -10);

        event.setIsValid(true);
        event.setValidUntil(calendar.getTime());

        when(credentialRepository.getPasswordResetEvent(security.getCurrentApplication().getUuid(), event.getToken())).thenReturn(event);
        control.updateManagerPassword(currentManager.getUsername(), event);
        fail();
    }

    @Test
    public void testUpdateManagerPassword() throws RestException {
        String username = "a-username";
        PasswordChangeEvent event = Mockito.spy(new PasswordChangeEvent());
        event.setNewPassword("new-psw-123");
        event.setToken("token-123");
        event.setIsValid(false);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        event.setIsValid(true);
        event.setValidUntil(calendar.getTime());
        event.setTargetManager(currentManager);

        when(credentialRepository.getPasswordResetEvent(security.getCurrentApplication().getUuid(), event.getToken())).thenReturn(event);
        Manager updatedManager = control.updateManagerPassword(username, event);
        assertEquals(event.getNewPassword(), updatedManager.getPassword());
        verify(event, times(2)).setIsValid(false);

    }

    @Test
    public void testCreateManager() throws RestException {
        String token = "token-123";

        Domain domain = new Domain();
        domain.setUuid("domain-uuid");

        DomainPermission permission = new DomainPermission(1);
        permission.setUuid("permission-uuid");

        String targetEmail = "target@email.com";
        Manager manFromInv = new Manager();
        manFromInv.setApplication(currentApplication);
        manFromInv.setEmail(targetEmail);
        manFromInv.setStatus(AccountStatus.PROVISIONING);

        Invitation invitation = new Invitation();
        invitation.setDomain(domain);
        invitation.setPermission(permission);
        invitation.setTargetManager(manFromInv);
        invitation.getTargetManager().setEmail("target@email.com");
        Invitation spyInvitation = Mockito.spy(invitation);

        Manager managerCreate = new Manager();
        managerCreate.setUsername("a-username");
        managerCreate.setEmail("wrong@email.com");
        managerCreate.setFirstName("firstName");
        managerCreate.setLastName("lastName");
        managerCreate.setPassword("new-password");

        Manager spyManager = Mockito.spy(managerCreate);

        when(invRepository.getInvitationByToken(currentApplication.getUuid(), token)).thenReturn(spyInvitation);
        when(credentialRepository.find(Domain.class, currentApplication.getUuid(), spyInvitation.getDomain().getUuid())).thenReturn(domain);
        when(credentialRepository.find(DomainPermission.class, currentApplication.getUuid(), spyInvitation.getPermission().getUuid())).thenReturn(permission);

        Manager createdManager = control.createManager(token, spyManager);
        verify(spyManager, times(1)).removeNonCreatable();
        verify(credentialRepository).update(manFromInv);

        assertEquals(spyInvitation.getTargetManager().getEmail(), createdManager.getEmail());
        assertEquals(AccountStatus.ACTIVE, createdManager.getStatus());

        //Verify if this object is being saved
        ManagerMembership membership = new ManagerMembership();
        membership.setManager(createdManager);
        membership.setDomain(domain);
        membership.setPermission(permission);
        membership.setApplication(security.getCurrentApplication());

        verify(credentialRepository).create(membership);
    }

    @Test
    public void testCreateManagerActivatedAccount() throws RestException {
        String token = "token-123";

        Domain domain = new Domain();
        domain.setUuid("domain-uuid");

        DomainPermission permission = new DomainPermission(1);
        permission.setUuid("permission-uuid");

        String targetEmail = "target@email.com";
        Manager manFromInv = new Manager();
        manFromInv.setApplication(currentApplication);
        manFromInv.setEmail(targetEmail);
        manFromInv.setStatus(AccountStatus.ACTIVE);

        Invitation invitation = new Invitation();
        invitation.setDomain(domain);
        invitation.setPermission(permission);
        invitation.setTargetManager(manFromInv);
        invitation.getTargetManager().setEmail("target@email.com");
        Invitation spyInvitation = Mockito.spy(invitation);

        Manager managerCreate = new Manager();
        managerCreate.setUsername("a-username");
        managerCreate.setEmail("wrong@email.com");
        managerCreate.setFirstName("firstName");
        managerCreate.setLastName("lastName");
        managerCreate.setPassword("new-password");

        Manager spyManager = Mockito.spy(managerCreate);

        when(invRepository.getInvitationByToken(currentApplication.getUuid(), token)).thenReturn(spyInvitation);
        when(credentialRepository.find(Domain.class, currentApplication.getUuid(), spyInvitation.getDomain().getUuid())).thenReturn(domain);
        when(credentialRepository.find(DomainPermission.class, currentApplication.getUuid(), spyInvitation.getPermission().getUuid())).thenReturn(permission);

        Manager createdManager = control.createManager(token, spyManager);

        assertEquals(spyInvitation.getTargetManager().getEmail(), createdManager.getEmail());
        assertEquals(AccountStatus.ACTIVE, createdManager.getStatus());

        verify(credentialRepository, times(0)).update(manFromInv);

        //Verify if this object is being saved
        ManagerMembership membership = new ManagerMembership();
        membership.setManager(createdManager);
        membership.setDomain(domain);
        membership.setPermission(permission);
        membership.setApplication(security.getCurrentApplication());

        verify(credentialRepository).create(membership);
    }

    @Test(expected = RestException.class)
    public void testCreateManagerInactiveAccount() throws RestException {
        String token = "token-123";

        Domain domain = new Domain();
        domain.setUuid("domain-uuid");

        DomainPermission permission = new DomainPermission(1);
        permission.setUuid("permission-uuid");

        String targetEmail = "target@email.com";
        Manager manFromInv = new Manager();
        manFromInv.setApplication(currentApplication);
        manFromInv.setEmail(targetEmail);
        manFromInv.setStatus(AccountStatus.INACTIVE);

        Invitation invitation = new Invitation();
        invitation.setDomain(domain);
        invitation.setTargetManager(manFromInv);
        Invitation spyInvitation = Mockito.spy(invitation);

        Manager managerCreate = new Manager();
        managerCreate.setUsername("a-username");
        managerCreate.setEmail("wrong@email.com");
        managerCreate.setFirstName("firstName");
        managerCreate.setLastName("lastName");
        managerCreate.setPassword("new-password");

        Manager spyManager = Mockito.spy(managerCreate);

        when(invRepository.getInvitationByToken(currentApplication.getUuid(), token)).thenReturn(spyInvitation);

        control.createManager(token, spyManager);
        fail();

    }

    @Test(expected = RestException.class)
    public void testCreateManagerTokenNotFound() throws RestException {
        String token = "token-123";

        when(invRepository.getInvitationByToken(currentApplication.getUuid(), token)).thenReturn(null);
        control.createManager(token, null);
        fail();
    }

    @Test(expected = InvalidResourceArgException.class)
    public void testCreateManagerNoUsername() throws RestException {
        String token = "token";
        Manager manager = Mockito.mock(Manager.class);

        when(invRepository.getInvitationByToken(eq(currentApplication.getUuid()), eq(token))).thenReturn(new Invitation());
        when(manager.getUsername()).thenReturn(null);

        control.createManager(token, manager);
        fail();
    }

    @Test(expected = InvalidResourceArgException.class)
    public void testCreateManagerNoPassword() throws RestException {
        String token = "token";
        Manager manager = Mockito.mock(Manager.class);

        when(invRepository.getInvitationByToken(eq(currentApplication.getUuid()), eq(token))).thenReturn(new Invitation());
        when(manager.getUsername()).thenReturn("any-username");

        control.createManager(token, manager);
        fail();
    }

    @Test(expected = ResourceAlreadyExistsException.class)
    public void testCreateManagerUsernameAlreadyExists() throws RestException {
        String token = "token";
        Manager manager = Mockito.mock(Manager.class);

        when(invRepository.getInvitationByToken(eq(currentApplication.getUuid()), eq(token))).thenReturn(new Invitation());
        when(manager.getUsername()).thenReturn("a-username");
        when(manager.getPassword()).thenReturn("a-password");
        when(credentialRepository.getManagerByUsername(currentApplication.getUuid(), manager.getUsername())).thenReturn(manager);
        control.createManager(token, manager);
        fail();
    }

    @Test(expected = InvalidResourceArgException.class)
    public void testCreateManagerCompletedStatus() throws RestException {
        String token = "token-123";

        Invitation invitation = new Invitation();
        invitation.setStatus(InvitationStatus.COMPLETED);

        Invitation spyInvitation = Mockito.spy(invitation);

        when(invRepository.getInvitationByToken(eq(currentApplication.getUuid()), eq(token))).thenReturn(spyInvitation);
        control.createManager(token, null);
        fail();
    }

    @Test(expected = InvalidResourceArgException.class)
    public void testCreateCredentialExpiredStatus() throws RestException {
        String token = "token-123";

        Invitation invitation = new Invitation();
        invitation.setStatus(InvitationStatus.EXPIRED);

        Invitation spyInvitation = Mockito.spy(invitation);

        when(invRepository.getInvitationByToken(eq(currentApplication.getUuid()), eq(token))).thenReturn(spyInvitation);
        control.createManager(token, null);
        fail();
    }

    @Test(expected = InvalidResourceArgException.class)
    public void testCreateCredentialFailedStatus() throws RestException {
        String token = "token-123";

        Invitation invitation = new Invitation();
        invitation.setStatus(InvitationStatus.FAILED);

        Invitation spyInvitation = Mockito.spy(invitation);

        when(invRepository.getInvitationByToken(eq(currentApplication.getUuid()), eq(token))).thenReturn(spyInvitation);
        control.createManager(token, null);
        fail();
    }

    @Test
    public void testGetAPICredentials() throws RestException {
        ManagerMembership membership = new ManagerMembership();
        membership.setDomain(new Domain());

        APICredential apiCredMock = spy(new APICredential("0123456789"));
        apiCredMock.setMembership(membership);
        List<APICredential> realList = Arrays.asList(apiCredMock);

        String domainUuid = "uuid-123";

        when(credentialRepository.getAPICredentials(currentApplication.getUuid(), domainUuid, DEFAULT_LIMIT, DEFAULT_OFFSET)).thenReturn(realList);

        ListResource<APICredential> apiCredentials = control.getAPICredentials(domainUuid, DEFAULT_LIMIT, DEFAULT_OFFSET);
        assertEquals(realList.size(), apiCredentials.getItems().size());
        for (APICredential apicred : apiCredentials.getItems()) {
            assertNotNull(apicred);
            assertEquals(apiCredMock, apicred);

            verify(apicred).setApiKey(any(String.class));
        }
    }

    @Test
    public void testGetAPICredentialsByDomainAndManager() throws RestException {
        String domainUuid = "domain-uuid";
        String apiKey = "api-key-uuid-123";

        APICredential apiCredential = Mockito.mock(APICredential.class, Mockito.RETURNS_DEEP_STUBS);
        List<APICredential> realList = Arrays.asList(apiCredential);

        when(credentialRepository.getAPICredentials(currentApplication.getUuid(), domainUuid, security.getCurrentManager().getUuid(), DEFAULT_LIMIT, DEFAULT_OFFSET)).thenReturn(realList);
        when(apiCredential.getApiKey()).thenReturn(UUID.randomUUID().toString());

        ListResource<APICredential> apiCredentials = control.getAPICredentialsByDomainAndManager(domainUuid, DEFAULT_LIMIT, DEFAULT_OFFSET);
        assertEquals(realList.size(), apiCredentials.getItems().size());

        for (APICredential cred : apiCredentials.getItems()) {
            assertTrue(!cred.getApiKey().equals(apiKey));
        }
    }

    @Test
    public void testGetAPICredential() {
        String apiKeyUuid = "apikey-uuid-123";
        APICredential apiCredential = Mockito.mock(APICredential.class, Mockito.RETURNS_DEEP_STUBS);

        when(credentialRepository.getAPICredential(currentApplication.getUuid(), apiKeyUuid)).thenReturn(apiCredential);
        when(apiCredential.getApiKey()).thenReturn(UUID.randomUUID().toString());

        APICredential foundAPICredential = control.getAPICredential(apiKeyUuid);
        assertNotNull(foundAPICredential);
        assertEquals(apiCredential, foundAPICredential);

        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(apiCredential).setApiKey(argument.capture());
    }

    @Test
    public void testLoginRecovery() throws RestException {
        String email = "test@email.com";
        Manager foundManager = Mockito.mock(Manager.class);

        when(credentialRepository.getManagerByEmail(currentApplication.getUuid(), email)).thenReturn(foundManager);

        control.loginRecovery(email);

        LoginRecoveryEvent event = new LoginRecoveryEvent(foundManager);
        verify(credentialRepository).create(event);
        verify(mockEvent).fire(event);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testLoginRecoveryNotFound() throws RestException {
        String email = "test@email.com";

        when(credentialRepository.getManagerByEmail(currentApplication.getUuid(), email)).thenReturn(null);
        control.loginRecovery(email);
        fail();

    }

    @Test(expected = ResourceNotFoundException.class)
    public void testUpdateAPICredentialNotFound() throws RestException {
        String domainUuid = "domain-uuid";
        String credentialUuid = "cred-uuid";

        when(credentialRepository.find(APICredential.class, currentApplication.getUuid(), credentialUuid)).thenReturn(null);
        control.updateAPICredential(domainUuid, credentialUuid, null);
        fail();
    }

    @Test(expected = InvalidResourceArgException.class)
    public void testUpdateAPICredentialInvalidResource() throws RestException {
        String domainUuid = "domain-uuid";
        String credentialUuid = "cred-uuid";
        APICredential apiCredential = Mockito.spy(new APICredential());
        apiCredential.setMembership(new ManagerMembership());
        apiCredential.getMembership().setPermission(new DomainPermission());

        DomainPermission domainPerm = Mockito.mock(DomainPermission.class);

        when(credentialRepository.find(APICredential.class, currentApplication.getUuid(), credentialUuid)).thenReturn(apiCredential);
        when(domainRespository.getDomainPermission(currentApplication.getUuid(), domainUuid, domainPerm.getName())).thenReturn(null);
        control.updateAPICredential(domainUuid, credentialUuid, apiCredential);
        fail();
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateAPICredentialNotAuthorized() throws RestException {
        String domainUuid = "domain-uuid";
        String credentialUuid = "cred-uuid";
        APICredential apiCredential = Mockito.spy(new APICredential());
        apiCredential.setMembership(new ManagerMembership());
        apiCredential.getMembership().setPermission(new DomainPermission());

        DomainPermission domainPerm = Mockito.mock(DomainPermission.class);

        when(credentialRepository.find(APICredential.class, currentApplication.getUuid(), credentialUuid)).thenReturn(apiCredential);
        when(domainRespository.getDomainPermission(currentApplication.getUuid(), domainUuid, domainPerm.getName())).thenReturn(domainPerm);
        doReturn(false).when(security).isPermitted(any(Permission.class));
        control.updateAPICredential(domainUuid, credentialUuid, apiCredential);
        fail();
    }

    @Test
    public void testUpdateAPICredential() throws RestException {
        String domainUuid = "domain-uuid";
        String credentialUuid = "cred-uuid";
        APICredential apiCredential = Mockito.spy(new APICredential());
        apiCredential.setMembership(new ManagerMembership());
        apiCredential.getMembership().setPermission(new DomainPermission());

        APICredential foundAPICredential = Mockito.mock(APICredential.class);

        DomainPermission domainPerm = Mockito.mock(DomainPermission.class);

        when(credentialRepository.find(APICredential.class, currentApplication.getUuid(), credentialUuid)).thenReturn(foundAPICredential);
        when(domainRespository.getDomainPermission(currentApplication.getUuid(), domainUuid, domainPerm.getName())).thenReturn(domainPerm);
        doReturn(true).when(security).isPermitted(any(Permission.class));
        when(credentialRepository.update(foundAPICredential)).thenReturn(foundAPICredential);

        APICredential updatedDomCred = control.updateAPICredential(domainUuid, credentialUuid, apiCredential);

        verify(foundAPICredential).copyUpdatable(apiCredential);
        verify(credentialRepository).update(foundAPICredential);
        assertEquals(foundAPICredential, updatedDomCred);

    }

    @Test(expected = InvalidResourceArgException.class)
    public void testCreateAPICredentialPermissionNotFound() throws RestException {
        String domainUuid = "domain-123";
        APICredential apiCreedntial = Mockito.mock(APICredential.class);
        DomainPermission mockedDomPerm = Mockito.mock(DomainPermission.class);

        when(domainRespository.getDomainPermission(currentApplication.getUuid(), domainUuid, mockedDomPerm.getName())).thenReturn(null);

        control.createAPICredential(domainUuid, apiCreedntial);
        fail();
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateAPICredentialNotAuthorized() throws RestException {
        String domainUuid = "domain-123";
        APICredential apiCredential = Mockito.spy(new APICredential());
        apiCredential.setMembership(new ManagerMembership());
        apiCredential.getMembership().setPermission(new DomainPermission());
        apiCredential.getMembership().setDomain(new Domain());

//        AccessLevelPermission mockPerm = Mockito.spy(new AccessLevelPermission(apiCredential.getMembership().getDomain().getUuid(), apiCredential.getMembership().getPermission()));
        when(credentialRepository.getManagerMembership(currentApplication.getUuid(), domainUuid, security.getCurrentManager().getUuid())).thenReturn(new ManagerMembership());
        doReturn(false).when(security).isPermitted(any(Permission.class));

        control.createAPICredential(domainUuid, apiCredential);
        fail();
    }

    @Test(expected = InvalidResourceArgException.class)
    public void testCreateAPICredentialDomainNotFound() throws RestException {
        String domainUuid = "domain-123";
        APICredential apiCredential = Mockito.spy(new APICredential());

        doReturn(true).when(security).isPermitted(any(Permission.class));
        when(credentialRepository.getManagerMembership(currentApplication.getUuid(), domainUuid, security.getCurrentManager().getUuid())).thenReturn(null);

        control.createAPICredential(domainUuid, apiCredential);
        fail();
    }

    @Test
    public void testCreateAPICredential() throws RestException {
        String domainUuid = "domain-123";
        APICredential apiCredential = Mockito.spy(new APICredential());

        ManagerMembership foundMembership = Mockito.mock(ManagerMembership.class);

        when(apiCredential.getApiKey()).thenReturn("1234567890");
        when(credentialRepository.getManagerMembership(currentApplication.getUuid(), domainUuid, security.getCurrentManager().getUuid())).thenReturn(foundMembership);
        doReturn(true).when(security).isPermitted(any(Permission.class));

        APICredential createdAPiDomCred = control.createAPICredential(domainUuid, apiCredential);

        verify(apiCredential).removeNonCreatable();
        verify(apiCredential).setMembership(foundMembership);
        verify(apiCredential).setApplication(security.getCurrentApplication());
        verify(credentialRepository).create(apiCredential);
        assertNotNull(createdAPiDomCred.getApiKey());
        assertEquals(AccountStatus.ACTIVE, createdAPiDomCred.getStatus());

    }

    @Test(expected = ResourceNotFoundException.class)
    public void testDeleteAPICredentialNotFound() throws RestException {
        String domainUuid = "domain-123";
        String domainCredentialUuid = "dom-cred-123";

        when(credentialRepository.find(APICredential.class, currentApplication.getUuid(), domainCredentialUuid)).thenReturn(null);

        control.deleteAPICredential(domainUuid, domainCredentialUuid);
        fail();
    }

    @Test
    public void testDeleteAPICredential() throws RestException {
        String domainUuid = "domain-123";
        String domainCredentialUuid = "dom-cred-123";
        APICredential apiCredential = Mockito.mock(APICredential.class);

        when(credentialRepository.find(APICredential.class, currentApplication.getUuid(), domainCredentialUuid)).thenReturn(apiCredential);

        control.deleteAPICredential(domainUuid, domainCredentialUuid);
        verify(credentialRepository).delete(apiCredential);

    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetManagerByloginNotFound() throws RestException {
        String login = "login-123";
        when(credentialRepository.getManagerByUsername(currentApplication.getUuid(), login)).thenReturn(null);
        control.getManagerBylogin(login);
        fail();
    }

    @Test
    public void testGetManagerBylogin() throws RestException {
        String login = "login-123";
        Manager mockManager = Mockito.mock(Manager.class);

        when(credentialRepository.getManagerByUsername(currentApplication.getUuid(), login)).thenReturn(mockManager);
        Manager foundManager = control.getManagerBylogin(login);
        assertEquals(mockManager, foundManager);
    }

    @Test
    public void testLogin() throws Exception {
        String email = "test@email.com";
        String psw = "pass123";
        String base64Formatted = DatatypeConverter.printBase64Binary((email + ":" + psw).getBytes());

        SimpleLogin simpleLogin = new SimpleLogin();
        simpleLogin.setType(SimpleLogin.LoginType.BASIC);
        simpleLogin.setValue(base64Formatted);

        //For this test, KingdomSecurity should me mocked and no spied
        KingdomSecurity mock = Mockito.mock(KingdomSecurity.class);
        control.security = mock;

        when(mock.getCurrentApplication()).thenReturn(currentApplication);
        ManagerToken token = new ManagerToken(email, psw.toCharArray(), mock.getCurrentApplication().getUuid());
        when(mock.login(token)).thenReturn(new Manager());
        Manager foundManager = control.login(simpleLogin);
        assertNotNull(foundManager);

    }

    @Test
    public void testGetCurrentManager() throws Exception {
        when(security.getCurrentManager()).thenReturn(new Manager());
        control.getCurrentManager();
        verify(security).getCurrentManager();

    }
}
