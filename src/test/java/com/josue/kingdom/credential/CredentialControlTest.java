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
import com.josue.kingdom.credential.entity.PasswordResetEvent;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.enterprise.event.Event;
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
import static org.mockito.Matchers.anyString;
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

    @Test
    public void testPasswordReset() throws RestException {
        String uuid = "uuid123";
        Manager man = new Manager();
        man.setUuid(uuid);
        man.setEmail("josue@email.com");
        man.setUuid("uuid");
        man.setUsername("username");
        man.setPassword("pass123");
        Manager spyManager = Mockito.spy(man);

        when(credentialRepository.getManagerByUsername(currentApplication.getUuid(), man.getUsername())).thenReturn(spyManager);
        control.passwordReset(man.getUsername());

        verify(spyManager).setPassword(anyString());
        verify(mockEvent).fire(new PasswordResetEvent(spyManager.getEmail(), spyManager.getPassword()));

    }

    //TODO create specific ExceptionClass
    @Test(expected = RestException.class)
    public void testPasswordResetManagerNotFound() throws RestException {
        String login = "login";
        when(credentialRepository.getManagerByUsername(currentApplication.getUuid(), login)).thenReturn(null);
        control.passwordReset(login);
        fail();
    }

    @Test
    public void testCreateManager() throws RestException {
        String token = "token-123";

        Domain domain = new Domain();
        domain.setUuid("domain-uuid");

        DomainPermission permission = new DomainPermission(1);
        permission.setUuid("permission-uuid");

        Invitation invitation = new Invitation();
        invitation.setDomain(domain);
        invitation.setPermission(permission);
        invitation.setTargetManager(new Manager());
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
        when(credentialRepository.getManagerByEmail(currentApplication.getUuid(), managerCreate.getEmail())).thenReturn(null);

        Manager createdManager = control.createManager(token, spyManager);
        verify(spyManager, times(1)).removeNonCreatable();
        verify(credentialRepository, times(1)).create(spyManager);

        assertEquals(spyInvitation.getTargetManager().getEmail(), createdManager.getEmail());
        assertEquals(AccountStatus.ACTIVE, createdManager.getStatus());

        //Verify if this object is being saved
        ManagerMembership membership = new ManagerMembership();
        membership.setManager(createdManager);
        membership.setDomain(domain);
        membership.setPermission(permission);
        membership.setApplication(security.getCurrentApplication());

        verify(credentialRepository, times(1)).create(membership);
    }

    @Test(expected = RestException.class)
    public void testCreateManagerTokenNotFound() throws RestException {
        String token = "token-123";

        when(invRepository.getInvitationByToken(currentApplication.getUuid(), token)).thenReturn(null);
        control.createManager(token, null);
        fail();
    }

    @Test(expected = ResourceAlreadyExistsException.class)
    public void testCreateManagerLoginAlreadyExists() throws RestException {
        String token = "token";
        Manager manager = Mockito.mock(Manager.class);

        when(invRepository.getInvitationByToken(eq(currentApplication.getUuid()), eq(token))).thenReturn(new Invitation());
        when(credentialRepository.getManagerByEmail(currentApplication.getUuid(), manager.getEmail())).thenReturn(manager);
        control.createManager(token, manager);
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
    public void testGetAPICredentialsByDomain() throws RestException {
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

            ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
            verify(apiCredMock, times(realList.size())).setApiKey(argument.capture());
            assertTrue(argument.getValue().contains("*"));
        }
    }

    @Test
    public void testGetAPICredentialsByDomainAndManager() throws RestException {
        String domainUuid = "domain-uuid";
        APICredential apiCredential = Mockito.mock(APICredential.class, Mockito.RETURNS_DEEP_STUBS);
        List<APICredential> realList = Arrays.asList(apiCredential);

        when(credentialRepository.getAPICredentials(currentApplication.getUuid(), domainUuid, security.getCurrentManager().getUuid(), DEFAULT_LIMIT, DEFAULT_OFFSET)).thenReturn(realList);
        when(apiCredential.getApiKey()).thenReturn(UUID.randomUUID().toString());

        ListResource<APICredential> apiCredentials = control.getAPICredentialsByDomainAndManager(domainUuid, DEFAULT_LIMIT, DEFAULT_OFFSET);
        assertEquals(realList.size(), apiCredentials.getItems().size());
        for (APICredential apicred : apiCredentials.getItems()) {
            assertNotNull(apicred);

            ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
            verify(apiCredential, times(realList.size())).setApiKey(argument.capture());
            assertTrue(argument.getValue().contains("*"));
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
        assertTrue(argument.getValue().contains("*"));
    }

    @Test
    public void testLoginRecovery() throws RestException {
        String email = "test@email.com";
        Manager foundManager = Mockito.mock(Manager.class);

        when(credentialRepository.getManagerByEmail(currentApplication.getUuid(), email)).thenReturn(foundManager);

        control.loginRecovery(email);
        verify(mockEvent).fire(new LoginRecoveryEvent(email, foundManager.getUsername()));
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
        doReturn(false).when(control).isPermitted(any(Permission.class));
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
        doReturn(true).when(control).isPermitted(any(Permission.class));
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
        doReturn(false).when(control).isPermitted(any(Permission.class));

        control.createAPICredential(domainUuid, apiCredential);
        fail();
    }

    @Test(expected = InvalidResourceArgException.class)
    public void testCreateAPICredentialDomainNotFound() throws RestException {
        String domainUuid = "domain-123";
        APICredential apiCredential = Mockito.spy(new APICredential());

        doReturn(true).when(control).isPermitted(any(Permission.class));
        when(credentialRepository.getManagerMembership(currentApplication.getUuid(), domainUuid, security.getCurrentManager().getUuid())).thenReturn(null);

        control.createAPICredential(domainUuid, apiCredential);
        fail();
    }

    @Test
    public void testCreateAPICredential() throws RestException {
        String domainUuid = "domain-123";
        APICredential apiCredential = Mockito.spy(new APICredential());

        ManagerMembership foundMembership = Mockito.mock(ManagerMembership.class);

        when(credentialRepository.getManagerMembership(currentApplication.getUuid(), domainUuid, security.getCurrentManager().getUuid())).thenReturn(foundMembership);
        doReturn(true).when(control).isPermitted(any(Permission.class));

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
}
