/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.domain;

import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.credential.entity.ManagerCredential;
import com.josue.kingdom.domain.entity.Domain;
import com.josue.kingdom.domain.entity.DomainStatus;
import com.josue.kingdom.domain.entity.ManagerDomainCredential;
import com.josue.kingdom.rest.ListResource;
import com.josue.kingdom.rest.ex.ResourceNotFoundException;
import com.josue.kingdom.rest.ex.RestException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
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
 * @author iFood
 */
public class DomainControlTest {

    private static final Integer DEFAULT_LIMIT = 100;
    private static final Integer DEFAULT_OFFSET = 0;

    @Mock
    DomainRepository repository;

    @Spy
    ManagerCredential currentCredential = new ManagerCredential();

    @Spy
    Manager manager = new Manager();

    @InjectMocks
    DomainControl control = new DomainControl();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        currentCredential.setManager(manager);
    }

    @Test//TODO implement logic
    public void testGetOwnedDomains() {
        List<Domain> mockedDomains = Arrays.asList(Mockito.mock(Domain.class));

        when(repository.getOwnedDomains(currentCredential.getManager().getUuid(), DEFAULT_LIMIT, DEFAULT_OFFSET)).thenReturn(mockedDomains);
        ListResource<Domain> ownedDomains = control.getOwnedDomains(DEFAULT_LIMIT, DEFAULT_OFFSET);
        verify(repository, times(1)).getOwnedDomains(currentCredential.getManager().getUuid(), DEFAULT_LIMIT, DEFAULT_OFFSET);
        assertEquals(mockedDomains.size(), ownedDomains.getItems().size());
    }

    @Test
    public void testGetJoinedDomains() {
        List<Domain> domains = Arrays.asList(Mockito.mock(Domain.class));
        when(repository.getJoinedDomains(currentCredential.getManager().getUuid(), DEFAULT_LIMIT, DEFAULT_OFFSET)).thenReturn(domains);

        ListResource<Domain> joinedDomains = control.getJoinedDomains(DEFAULT_LIMIT, DEFAULT_OFFSET);
        verify(repository, times(1)).getDomainCredentials(manager.getUuid(), DEFAULT_LIMIT, DEFAULT_OFFSET);
        assertEquals(domains, joinedDomains.getItems());
    }

    @Test
    public void testCreateDomain() throws RestException {
        Domain domain = new Domain();
        domain.setName("name-123");
        domain.setStatus(DomainStatus.ACTIVE);
        //Non creatable fields
        domain.setLastUpdate(new Date());
        domain.setDateCreated(new Date());
        domain.setOwner(new Manager());

        when(repository.find(Manager.class, currentCredential.getManager().getUuid())).thenReturn(manager);

        Domain mockedDomain = null;
        when(repository.getDomainByName(domain.getName())).thenReturn(mockedDomain);

        Domain createdDomain = control.createDomain(domain);
        assertEquals(createdDomain.getOwner(), manager);
        assertNull(domain.getDateCreated());
        assertNull(domain.getLastUpdate());
        verify(repository, times(1)).create(domain);

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

        Domain userInput = new Domain();
        userInput.setStatus(DomainStatus.INACTIVE);
        //Non updatable fields
        userInput.setName("userInput-name-123");
        userInput.setLastUpdate(new Date());
        userInput.setDateCreated(new Date());
        Manager man2 = new Manager();
        man1.setEmail("man2@email.com");
        userInput.setOwner(man2);

        when(repository.find(Manager.class, currentCredential.getManager().getUuid())).thenReturn(manager);

        when(repository.find(Domain.class, domainUuid)).thenReturn(actualDomainStub);
        when(repository.update(actualDomainStub)).thenReturn(actualDomainStub);
        when(repository.find(Domain.class, domainUuid)).thenReturn(actualDomainStub);
        when(currentCredential.getManager()).thenReturn(man1);

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

        when(repository.find(Manager.class, currentCredential.getManager().getUuid())).thenReturn(manager);
        when(repository.find(Domain.class, domainUuid)).thenReturn(null);
        control.updateDomain(domainUuid, domain);
        fail("ResourceNotFoundException should be thrown");
    }

    @Test(expected = RestException.class)
    public void testCreateDomainNameAlreadyExists() throws RestException {
        Domain domain = new Domain();
        domain.setName("name-123");
        domain.setStatus(DomainStatus.ACTIVE);

        when(repository.find(Manager.class, currentCredential.getManager().getUuid())).thenReturn(manager);

        Domain mockedDomain = Mockito.mock(Domain.class);
        when(repository.getDomainByName(domain.getName())).thenReturn(mockedDomain);

        control.createDomain(domain);
        fail();
    }

    @Test
    public void testDeleteDomain() throws RestException {
        String domainUuid = "domain-123";
        Domain mockedDomain = Mockito.mock(Domain.class);

        when(repository.find(Domain.class, domainUuid)).thenReturn(mockedDomain);
        when(mockedDomain.getOwner()).thenReturn(manager);

        control.deleteDomain(domainUuid);
        verify(repository, times(1)).delete(mockedDomain);
    }

    @Test(expected = RestException.class)
    public void testDeleteDomainNotFound() throws RestException {
        String domainUuid = "domain-123";

        when(repository.find(Domain.class, domainUuid)).thenReturn(null);
        control.deleteDomain(domainUuid);
        fail();
    }

    @Test
    public void testGetJoinedDomain() throws RestException {
        String uuid = "123";

        ManagerDomainCredential manDomCred = Mockito.spy(new ManagerDomainCredential());
        when(repository.find(ManagerDomainCredential.class, uuid)).thenReturn(manDomCred);

        control.getJoinedDomain(uuid);

        verify(manDomCred, times(1)).setCredential(null);
    }

    @Test
    public void testCreateDomainPermission() throws Exception {

        fail("The test case is a prototype.");
    }

    @Test
    public void testUpdateDomainPermission() throws Exception {

        fail("The test case is a prototype.");
    }

    @Test
    public void testDeleteDomainPermission() throws Exception {

        fail("The test case is a prototype.");
    }

    @Test
    public void testGetDomainPermissions() {

        fail("The test case is a prototype.");
    }

    @Test
    public void testCheckOwnerAccess() throws Exception {

        fail("The test case is a prototype.");
    }

    @Test
    public void testCheckDomainExists() throws Exception {

        fail("The test case is a prototype.");
    }
}
