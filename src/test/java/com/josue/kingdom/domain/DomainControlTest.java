/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.domain;

import com.josue.kingdom.application.entity.Application;
import com.josue.kingdom.credential.entity.AccountStatus;
import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.domain.entity.Domain;
import com.josue.kingdom.domain.entity.DomainPermission;
import com.josue.kingdom.domain.entity.DomainStatus;
import com.josue.kingdom.rest.ListResource;
import com.josue.kingdom.rest.ex.AuthorizationException;
import com.josue.kingdom.rest.ex.InvalidResourceArgException;
import com.josue.kingdom.rest.ex.ResourceAlreadyExistsException;
import com.josue.kingdom.rest.ex.ResourceNotFoundException;
import com.josue.kingdom.rest.ex.RestException;
import com.josue.kingdom.security.KingdomSecurity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

/**
 *
 * @author iFood
 */
public class DomainControlTest {

    private static final Integer DEFAULT_LIMIT = 100;
    private static final Integer DEFAULT_OFFSET = 0;

    @Mock
    DomainRepository repository;

    @Spy
    KingdomSecurity security;

    @InjectMocks
    DomainControl control = new DomainControl();

    private Manager currentManager;
    private Application currentApplication;

    @Before
    public void init() throws RestException {

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

    @Test//TODO implement logic
    public void testGetOwnedDomains() throws RestException {
        List<Domain> mockedDomains = Arrays.asList(Mockito.mock(Domain.class));

        when(repository.getOwnedDomains(currentApplication.getUuid(), currentManager.getUuid(), DEFAULT_LIMIT, DEFAULT_OFFSET)).thenReturn(mockedDomains);
        ListResource<Domain> ownedDomains = control.getOwnedDomains(DEFAULT_LIMIT, DEFAULT_OFFSET);
        verify(repository, times(1)).getOwnedDomains(currentApplication.getUuid(), currentManager.getUuid(), DEFAULT_LIMIT, DEFAULT_OFFSET);
        assertEquals(mockedDomains.size(), ownedDomains.getItems().size());
    }

    @Test
    public void testCreateDomain() throws RestException {
        Domain domain = Mockito.spy(new Domain());
        domain.setName("name-123");
        domain.setStatus(DomainStatus.ACTIVE);
        //Non creatable fields
        domain.setLastUpdate(new Date());
        domain.setDateCreated(new Date());
        domain.setOwner(new Manager());

        when(repository.find(Manager.class, currentApplication.getUuid(), currentManager.getUuid())).thenReturn(currentManager);

        Domain createdDomain = control.createDomain(domain);
        assertEquals(createdDomain.getOwner(), currentManager);
        assertNull(domain.getDateCreated());
        assertNull(domain.getLastUpdate());
        verify(domain).setApplication(currentApplication);
        verify(repository).create(domain);

    }

    @Test
    public void testUpdateDomain() throws RestException {
        String domainUuid = "domain-123";

        Domain domain = new Domain();
        domain.setStatus(DomainStatus.ACTIVE);
        //Non updatable fields
        domain.setName("name-123");
        domain.setLastUpdate(new Date());
        domain.setDateCreated(new Date());
        Manager man1 = new Manager();
        man1.setEmail("man1@email.com");
        domain.setOwner(man1);

        //Simple stub, so we can track the call of this object
        Domain actualDomainStub = Mockito.spy(domain);
        actualDomainStub.setOwner(currentManager);

        Domain userInput = new Domain();
        userInput.setStatus(DomainStatus.INACTIVE);
        //Non updatable fields
        userInput.setName("userInput-name-123");
        userInput.setLastUpdate(new Date());
        userInput.setDateCreated(new Date());
        Manager man2 = new Manager();
        man1.setEmail("man2@email.com");
        userInput.setOwner(man2);

        when(repository.find(Manager.class, currentApplication.getUuid(), currentManager.getUuid())).thenReturn(currentManager);

        when(repository.find(Domain.class, currentApplication.getUuid(), domainUuid)).thenReturn(actualDomainStub);
        when(repository.update(actualDomainStub)).thenReturn(actualDomainStub);

        Domain updatedDomain = control.updateDomain(domainUuid, userInput);
        verify(actualDomainStub, times(1)).copyUpdatable(userInput);
        verify(repository, times(1)).update(actualDomainStub);
        assertEquals(userInput.getStatus(), updatedDomain.getStatus());
        assertThat(userInput.getName(), not(updatedDomain.getName()));
        assertThat(userInput.getOwner(), not(updatedDomain.getOwner()));

    }

    @Test(expected = ResourceNotFoundException.class)
    public void testUpdateDomainNotFound() throws RestException {

        Domain domain = Mockito.mock(Domain.class);
        String domainUuid = "uuid-123";

        when(repository.find(Manager.class, currentApplication.getUuid(), currentManager.getUuid())).thenReturn(currentManager);
        when(repository.find(Domain.class, currentApplication.getUuid(), domainUuid)).thenReturn(null);
        control.updateDomain(domainUuid, domain);
        fail("ResourceNotFoundException should be thrown");
    }

    @Test
    public void testDeleteDomain() throws RestException {
        String domainUuid = "domain-123";
        Domain mockedDomain = Mockito.mock(Domain.class);

        when(repository.find(Domain.class, currentApplication.getUuid(), domainUuid)).thenReturn(mockedDomain);
        when(mockedDomain.getOwner()).thenReturn(currentManager);

        control.deleteDomain(domainUuid);
        verify(repository, times(1)).purgeDomain(currentApplication.getUuid(), mockedDomain.getUuid());
    }

    @Test(expected = RestException.class)
    public void testDeleteDomainNotFound() throws RestException {
        String domainUuid = "domain-123";

        when(repository.find(Domain.class, currentApplication.getUuid(), domainUuid)).thenReturn(null);
        control.deleteDomain(domainUuid);
        fail();
    }

    @Test
    public void testGetJoinedDomain() throws RestException {
        String domainUuid = "123";

        Domain mockedDomain = Mockito.mock(Domain.class);
        when(repository.getJoinedDomain(currentApplication.getUuid(), domainUuid, currentManager.getUuid())).thenReturn(mockedDomain);
        Domain joinedDomain = control.getJoinedDomain(domainUuid);
        assertEquals(mockedDomain, joinedDomain);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetJoinedDomainNotFound() throws RestException {
        String uuid = "123";

        when(repository.find(Domain.class, currentApplication.getUuid(), uuid)).thenReturn(null);
        control.getJoinedDomain(uuid);
        fail();
    }

    @Test(expected = InvalidResourceArgException.class)
    public void testCreateDomainPermissionLevelZero() throws RestException {
        String domainUuid = "domain-123";
        int permissionLevel = 0;

        Domain spyDomain = Mockito.spy(new Domain());
        spyDomain.setOwner(currentManager);

        DomainPermission domPerm = Mockito.mock(DomainPermission.class);

        when(repository.find(Domain.class, currentApplication.getUuid(), domainUuid)).thenReturn(spyDomain);
        when(domPerm.getLevel()).thenReturn(permissionLevel);
        control.createDomainPermission(domainUuid, domPerm);
        fail();
    }

    @Test(expected = ResourceAlreadyExistsException.class)
    public void testCreateDomainPermissionAlreadyExists() throws RestException {
        String domainUuid = "domain-123";
        int permissionLevel = 1;
        Domain spyDomain = Mockito.spy(new Domain());
        spyDomain.setOwner(currentManager);

        DomainPermission domPerm = Mockito.mock(DomainPermission.class);

        when(repository.find(Domain.class, currentApplication.getUuid(), domainUuid)).thenReturn(spyDomain);
        when(domPerm.getLevel()).thenReturn(1);
        when(repository.getDomainPermission(currentApplication.getUuid(), domainUuid, permissionLevel)).thenReturn(new DomainPermission());
        control.createDomainPermission(domainUuid, domPerm);
        fail();

    }

    @Test
    public void testCreateDomainPermission() throws RestException {
        String domainUuid = "domain-123";
        int permissionLevel = 1;
        Domain spyDomain = Mockito.spy(new Domain());
        spyDomain.setOwner(currentManager);

        DomainPermission spyDomPerm = Mockito.spy(new DomainPermission());

        when(repository.find(Domain.class, currentApplication.getUuid(), domainUuid)).thenReturn(spyDomain);
        when(spyDomPerm.getLevel()).thenReturn(1);
        when(repository.getDomainPermission(currentApplication.getUuid(), domainUuid, permissionLevel)).thenReturn(null);

        control.createDomainPermission(domainUuid, spyDomPerm);

        verify(spyDomPerm).removeNonCreatable();
        verify(spyDomPerm).setApplication(currentApplication);
        verify(repository).create(spyDomPerm);
        assertEquals(spyDomain, spyDomPerm.getDomain());

    }

    @Test(expected = ResourceAlreadyExistsException.class)
    public void testUpdateDomainPermissionAlreadyExists() throws RestException {
        String domainUuid = "domain-123";

        Domain foundDomain = Mockito.mock(Domain.class);
        DomainPermission domPerm = Mockito.mock(DomainPermission.class);
        when(repository.find(Domain.class, currentApplication.getUuid(), domainUuid)).thenReturn(foundDomain);
        when(foundDomain.getOwner()).thenReturn(currentManager);
        when(repository.getDomainPermission(currentApplication.getUuid(), domainUuid, domPerm.getLevel())).thenReturn(domPerm);
        control.updateDomainPermission(domainUuid, null, domPerm);
        fail();
    }

    @Test(expected = InvalidResourceArgException.class)
    public void testUpdateDomainPermissionInvalidLevel() throws RestException {
        String domainUuid = "domain-123";
        int permLevel = 0;

        Domain foundDomain = Mockito.mock(Domain.class);
        DomainPermission domPerm = Mockito.mock(DomainPermission.class);
        when(repository.find(Domain.class, currentApplication.getUuid(), domainUuid)).thenReturn(foundDomain);
        when(foundDomain.getOwner()).thenReturn(currentManager);
        when(repository.getDomainPermission(currentApplication.getUuid(), domainUuid, domPerm.getLevel())).thenReturn(null);
        when(domPerm.getLevel()).thenReturn(permLevel);

        control.updateDomainPermission(domainUuid, null, domPerm);
        fail();
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testUpdateDomainPermissionNotFound() throws RestException {
        String domainUuid = "domain-123";
        String permissionUuid = "perm-123";
        int permLevel = 1;

        Domain foundDomain = Mockito.mock(Domain.class);
        DomainPermission domPerm = Mockito.mock(DomainPermission.class);
        when(repository.find(Domain.class, currentApplication.getUuid(), domainUuid)).thenReturn(foundDomain);
        when(foundDomain.getOwner()).thenReturn(currentManager);
        when(repository.getDomainPermission(currentApplication.getUuid(), domainUuid, domPerm.getLevel())).thenReturn(null);
        when(domPerm.getLevel()).thenReturn(permLevel);
        when(repository.find(DomainPermission.class, currentApplication.getUuid(), permissionUuid)).thenReturn(null);

        control.updateDomainPermission(domainUuid, permissionUuid, domPerm);
        fail();
    }

    @Test
    public void testUpdateDomainPermission() throws RestException {
        String domainUuid = "domain-123";
        String permissionUuid = "perm-123";
        int permLevel = 1;

        Domain foundDomain = Mockito.mock(Domain.class);
        DomainPermission domPerm = Mockito.mock(DomainPermission.class);
        DomainPermission foundDomainPermission = Mockito.mock(DomainPermission.class);

        when(repository.find(Domain.class, currentApplication.getUuid(), domainUuid)).thenReturn(foundDomain);
        when(foundDomain.getOwner()).thenReturn(currentManager);
        when(repository.getDomainPermission(currentApplication.getUuid(), domainUuid, domPerm.getLevel())).thenReturn(null);
        when(domPerm.getLevel()).thenReturn(permLevel);
        when(repository.find(DomainPermission.class, currentApplication.getUuid(), permissionUuid)).thenReturn(foundDomainPermission);

        control.updateDomainPermission(domainUuid, permissionUuid, domPerm);

        verify(foundDomainPermission).copyUpdatable(domPerm);
        verify(repository).update(foundDomainPermission);

    }

    @Test(expected = RestException.class)
    public void testDeleteDomainPermissionSinglePerm() throws RestException {
        String domainUuid = "domain-123";
        int permissionsCount = 1;

        Domain foundDomain = Mockito.mock(Domain.class);

        List<DomainPermission> permissions = Mockito.mock(List.class);

        when(repository.find(Domain.class, currentApplication.getUuid(), domainUuid)).thenReturn(foundDomain);
        when(foundDomain.getOwner()).thenReturn(currentManager);
        when(repository.getDomainPermissions(currentApplication.getUuid(), domainUuid, DEFAULT_LIMIT, DEFAULT_OFFSET)).thenReturn(permissions);
        when(permissions.size()).thenReturn(permissionsCount);

        control.deleteDomainPermission(domainUuid, null, null);
        fail();
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testDeleteDomainPermissionNotFound() throws RestException {
        String domainUuid = "domain-123";

        Domain foundDomain = Mockito.mock(Domain.class);
        DomainPermission mockedDomainPerm = Mockito.mock(DomainPermission.class);
        List<DomainPermission> permissions = Mockito.spy(new ArrayList<DomainPermission>());
        permissions.add(mockedDomainPerm);
        permissions.add(mockedDomainPerm);

        when(repository.find(Domain.class, currentApplication.getUuid(), domainUuid)).thenReturn(foundDomain);
        when(foundDomain.getOwner()).thenReturn(currentManager);
        when(repository.getDomainPermissions(currentApplication.getUuid(), domainUuid, DEFAULT_LIMIT, DEFAULT_OFFSET)).thenReturn(permissions);
        when(mockedDomainPerm.getUuid()).thenReturn(""); //if (r.getUuid().equals(permissionUuid)) ** FALSE **

        control.deleteDomainPermission(domainUuid, null, null);

        fail();
    }

    @Test
    public void testDeleteDomainPermissionNoReplacement() throws RestException {
        String domainUuid = "domain-123";
        String domPermUuid = "dom-perm-123";

        Domain foundDomain = Mockito.mock(Domain.class);
        DomainPermission mockedDomainPerm1 = Mockito.mock(DomainPermission.class);
        DomainPermission replacementPermissionMock = Mockito.mock(DomainPermission.class);

        List<DomainPermission> permissions = Mockito.spy(new ArrayList<DomainPermission>());
        permissions.add(mockedDomainPerm1);
        permissions.add(replacementPermissionMock);

        //repository.getDomainPermissions(currentManager.getApplication().getUuid(), domainUuid, 10, 0);
        when(repository.find(Domain.class, currentApplication.getUuid(), domainUuid)).thenReturn(foundDomain);
        when(foundDomain.getOwner()).thenReturn(currentManager);
        when(repository.getDomainPermissions(eq(currentApplication.getUuid()), eq(domainUuid), any(Integer.class), any(Integer.class))).thenReturn(permissions);
        when(mockedDomainPerm1.getUuid()).thenReturn(domPermUuid); //if (r.getUuid().equals(permissionUuid)) ** TRUE **
        when(replacementPermissionMock.getUuid()).thenReturn(""); //no replacement

        control.deleteDomainPermission(domainUuid, domPermUuid, null);
        verify(repository).delete(mockedDomainPerm1);
    }

    @Test(expected = RestException.class)
    public void testDeleteDomainPermission() throws RestException {
        String domainUuid = "domain-123";
        String domPermUuid = "dom-perm-123";
        String replacementDomPermUuid = "replace-dom-perm-123";

        Domain foundDomain = Mockito.mock(Domain.class);
        DomainPermission mockedDomainPerm1 = Mockito.mock(DomainPermission.class);
        DomainPermission replacementPermissionMock = Mockito.mock(DomainPermission.class);

        List<DomainPermission> permissions = Mockito.spy(new ArrayList<DomainPermission>());
        permissions.add(mockedDomainPerm1);
        permissions.add(replacementPermissionMock);

        when(repository.find(Domain.class, currentApplication.getUuid(), domainUuid)).thenReturn(foundDomain);
        when(foundDomain.getOwner()).thenReturn(currentManager);
        when(repository.getDomainPermissions(currentApplication.getUuid(), domainUuid, DEFAULT_LIMIT, DEFAULT_OFFSET)).thenReturn(permissions);
        //replacement
        when(mockedDomainPerm1.getUuid()).thenReturn(domPermUuid); //if (r.getUuid().equals(permissionUuid)) ** TRUE **
        when(replacementPermissionMock.getUuid()).thenReturn(replacementDomPermUuid); //if (r.getUuid().equals(permissionUuid)) ** TRUE **

        control.deleteDomainPermission(domainUuid, domPermUuid, replacementDomPermUuid);
        verify(repository).delete(mockedDomainPerm1);
    }

    @Test
    public void testDeleteDomainPermissionWithValidReplacement() throws RestException {
        String domainUuid = "domain-123";
        String domPermUuid = "dom-perm-123";
        String replacementDomPermUuid = "replace-dom-perm-123";

        Domain foundDomain = Mockito.mock(Domain.class);
        DomainPermission mockedDomainPerm = Mockito.spy(new DomainPermission());
        mockedDomainPerm.setUuid(domPermUuid);

        DomainPermission mockedReplacementDomainPerm = Mockito.spy(new DomainPermission());
        mockedReplacementDomainPerm.setUuid(replacementDomPermUuid);

        List<DomainPermission> permissions = Mockito.spy(new ArrayList<DomainPermission>());
        permissions.add(mockedDomainPerm);
        permissions.add(mockedReplacementDomainPerm);

        when(repository.find(Domain.class, currentApplication.getUuid(), domainUuid)).thenReturn(foundDomain);
        when(foundDomain.getOwner()).thenReturn(currentManager);
        when(repository.getDomainPermissions(eq(currentApplication.getUuid()), eq(domainUuid), any(Integer.class), any(Integer.class))).thenReturn(permissions);
        //replacement
        when(mockedDomainPerm.getUuid()).thenReturn(domPermUuid); //if (r.getUuid().equals(permissionUuid)) ** TRUE **
        when(mockedReplacementDomainPerm.getUuid()).thenReturn(replacementDomPermUuid);

        control.deleteDomainPermission(domainUuid, domPermUuid, replacementDomPermUuid);
        //TODO improve IMPl and this test... not fully working
        verify(repository).delete(mockedDomainPerm);
    }

    @Test
    public void testGetDomainPermissions() {
        String domainUuid = "domain-123";
        List<DomainPermission> permissions = Mockito.mock(List.class);
        int listSize = 10;
        when(repository.getDomainPermissions(currentApplication.getUuid(), domainUuid, DEFAULT_LIMIT, DEFAULT_OFFSET)).thenReturn(permissions);
        when(permissions.size()).thenReturn(listSize);
        ListResource<DomainPermission> domainPermissions = control.getDomainPermissions(domainUuid, DEFAULT_LIMIT, DEFAULT_OFFSET);
        assertEquals(permissions, domainPermissions.getItems());
        assertEquals(listSize, domainPermissions.getTotalCount());
    }

    @Test
    public void testCheckOwnerAccess() throws RestException {
        Domain mockDomain = Mockito.mock(Domain.class);
        when(mockDomain.getOwner()).thenReturn(currentManager);
        control.checkOwnerAccess(mockDomain);
    }

    @Test(expected = AuthorizationException.class)
    public void testCheckOwnerAccessNotOwner() throws RestException {
        Domain mockDomain = Mockito.mock(Domain.class);
        when(mockDomain.getOwner()).thenReturn(new Manager());
        control.checkOwnerAccess(mockDomain);
        fail();

    }

    @Test
    public void testCheckDomainExists() throws RestException {
        String domainUuid = "domain-123";
        when(repository.find(Domain.class, currentApplication.getUuid(), domainUuid)).thenReturn(new Domain());
        control.checkDomainExists(domainUuid);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testCheckDomainExistsNot() throws RestException {
        String domainUuid = "domain-123";
        when(repository.find(Domain.class, currentApplication.getUuid(), domainUuid)).thenReturn(null);
        control.checkDomainExists(domainUuid);
    }

    @Test
    public void testGetDomains() throws RestException {
        String nameQuery = "a domain name";
        List<Domain> foundDomains = Mockito.mock(List.class);
        long size = 1L;

        when(repository.countDomains(currentApplication.getUuid())).thenReturn(size);
        when(repository.getDomains(currentApplication.getUuid(), nameQuery, DEFAULT_LIMIT, DEFAULT_OFFSET)).thenReturn(foundDomains);
        ListResource<Domain> domains = control.getDomains(nameQuery, DEFAULT_LIMIT, DEFAULT_OFFSET);
        assertEquals(foundDomains, domains.getItems());
        assertEquals(size, domains.getTotalCount());
    }

    @Test
    public void testGetJoinedDomains() throws RestException {
        String nameQuery = "a domain name";
        List<Domain> foundDomains = Mockito.mock(List.class);
        long size = 1L;

        when(repository.countJoinedDomains(currentApplication.getUuid(), currentManager.getUuid())).thenReturn(size);
        when(repository.getJoinedDomains(currentApplication.getUuid(), currentManager.getUuid(), DEFAULT_LIMIT, DEFAULT_OFFSET)).thenReturn(foundDomains);
        ListResource<Domain> domains = control.getJoinedDomains(DEFAULT_LIMIT, DEFAULT_OFFSET);
        assertEquals(foundDomains, domains.getItems());
        assertEquals(size, domains.getTotalCount());
    }

    @Test
    public void testGetOwnedDomain() throws RestException {
        String domainUuid = "123";

        Domain mockedDomain = Mockito.mock(Domain.class);
        when(repository.getJoinedDomain(currentApplication.getUuid(), domainUuid, currentManager.getUuid())).thenReturn(mockedDomain);
        Domain ownedDomain = control.getJoinedDomain(domainUuid);
        assertEquals(mockedDomain, ownedDomain);
    }
}
