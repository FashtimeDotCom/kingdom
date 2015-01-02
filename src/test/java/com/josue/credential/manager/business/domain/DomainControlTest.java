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
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
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

/**
 *
 * @author iFood
 */
public class DomainControlTest {

    @Mock
    DomainRepository repository;

    @Mock
    ManagerCredential currentCredential;

    @Mock
    Manager manager;

    @InjectMocks
    DomainControl control = new DomainControl();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test//TODO implement logic
    public void testGetOwnedDomains() {
        List<Domain> mockedDomains = Arrays.asList(Mockito.mock(Domain.class));
        when(currentCredential.getManager()).thenReturn(manager);
        when(repository.getOwnedDomainsByManager(currentCredential.getManager().getUuid())).thenReturn(mockedDomains);

        List<Domain> ownedDomains = control.getOwnedDomains();
        assertEquals(mockedDomains.size(), ownedDomains.size());
        verify(repository, times(1)).getOwnedDomainsByManager(currentCredential.getManager().getUuid());
    }

    @Test
    public void testGetJoinedDomains() {
        List<ManagerDomainCredential> domainCredentials = Arrays.asList(Mockito.mock(ManagerDomainCredential.class));
        when(currentCredential.getManager()).thenReturn(manager);
        when(repository.getJoinedDomainsByManager(currentCredential.getManager().getUuid())).thenReturn(domainCredentials);

        List<ManagerDomainCredential> joinedDomains = control.getJoinedDomains();
        for (ManagerDomainCredential mdc : domainCredentials) {
            verify(mdc, times(1)).setCredential(null);
        }
        assertEquals(domainCredentials, joinedDomains);
    }

    @Test
    public void testCreateDomain() {
        Domain domain = new Domain();
        domain.setName("name-123");
        domain.setStatus(DomainStatus.ACTIVE);

        when(currentCredential.getManager()).thenReturn(manager);
        when(repository.find(Manager.class, currentCredential.getManager().getUuid())).thenReturn(manager);

        List<String> mockedDomainNames = Mockito.mock(List.class);
        when(repository.getDomainUuidByName(domain.getName())).thenReturn(mockedDomainNames);
        //Domains name doesnt exist
        when(mockedDomainNames.isEmpty()).thenReturn(true);

        Domain createdDomain = control.createDomain(domain);
        assertEquals(createdDomain.getOwner(), manager);
        verify(mockedDomainNames, times(1)).isEmpty();
        verify(repository, times(1)).create(domain);
    }

    @Test(expected = RuntimeException.class)
    public void testCreateDomainNameAlreadyExists() {
        Domain domain = new Domain();
        domain.setName("name-123");
        domain.setStatus(DomainStatus.ACTIVE);

        when(currentCredential.getManager()).thenReturn(manager);
        when(repository.find(Manager.class, currentCredential.getManager().getUuid())).thenReturn(manager);

        List<String> mockedDomainNames = Mockito.mock(List.class);
        when(repository.getDomainUuidByName(domain.getName())).thenReturn(mockedDomainNames);
        //Domains name doesnt exist
        when(mockedDomainNames.isEmpty()).thenReturn(false);
        control.createDomain(domain);
        fail();
    }

    @Test
    public void testDeleteDomain() {
        String domainUuid = "domain-123";
        Domain mockedDomain = Mockito.mock(Domain.class);

        when(repository.find(Domain.class, domainUuid)).thenReturn(mockedDomain);
        control.deleteDomain(domainUuid);
        verify(repository, times(1)).remove(mockedDomain);
    }

    @Test(expected = RuntimeException.class)
    public void testDeleteDomainNotFound() {
        String domainUuid = "domain-123";

        when(repository.find(Domain.class, domainUuid)).thenReturn(null);
        control.deleteDomain(domainUuid);
        fail();
    }

}
