/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.domain;

import com.josue.credential.manager.auth.credential.ManagerCredential;
import com.josue.credential.manager.auth.domain.Domain;
import com.josue.credential.manager.auth.domain.DomainStatus;
import com.josue.credential.manager.auth.domain.ManagerDomainCredential;
import com.josue.credential.manager.auth.manager.Manager;
import com.josue.credential.manager.rest.ListResource;
import com.josue.credential.manager.rest.ex.ResourceNotFoundException;
import com.josue.credential.manager.rest.ex.RestException;
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

        when(repository.getOwnedDomainsByManager(currentCredential.getManager().getUuid())).thenReturn(mockedDomains);

        ListResource<Domain> ownedDomains = control.getOwnedDomains(100, 10L);
        assertEquals(mockedDomains.size(), ownedDomains.getItems().size());
        verify(repository, times(1)).getOwnedDomainsByManager(currentCredential.getManager().getUuid());
    }

    @Test
    public void testGetJoinedDomains() {
        List<ManagerDomainCredential> domainCredentials = Arrays.asList(Mockito.mock(ManagerDomainCredential.class));
        when(repository.getJoinedDomainsByManager(currentCredential.getManager().getUuid())).thenReturn(domainCredentials);

        ListResource<ManagerDomainCredential> joinedDomains = control.getJoinedDomains(100, 10L);
        for (ManagerDomainCredential mdc : domainCredentials) {
            verify(mdc, times(1)).setCredential(null);
        }
        assertEquals(domainCredentials, joinedDomains.getItems());
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
        when(repository.edit(actualDomainStub)).thenReturn(actualDomainStub);

        Domain updatedDomain = control.updateDomain(domainUuid, userInput);
        verify(actualDomainStub, times(1)).copyUpdatebleFields(userInput);
        verify(repository, times(1)).edit(actualDomainStub);
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
        control.deleteDomain(domainUuid);
        verify(repository, times(1)).remove(mockedDomain);
    }

    @Test(expected = RestException.class)
    public void testDeleteDomainNotFound() throws RestException {
        String domainUuid = "domain-123";

        when(repository.find(Domain.class, domainUuid)).thenReturn(null);
        control.deleteDomain(domainUuid);
        fail();
    }

    @Test
    public void testGetJoinedDomainsByUuid() {
        String uuid = "123";

        ManagerDomainCredential manDomCred = Mockito.spy(new ManagerDomainCredential());
        when(repository.find(ManagerDomainCredential.class, uuid)).thenReturn(manDomCred);

        control.getJoinedDomainByUuid(uuid);

        verify(manDomCred, times(1)).setCredential(null);
    }
}
