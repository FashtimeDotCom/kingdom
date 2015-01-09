/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.credential;

import com.josue.credential.manager.auth.credential.APICredential;
import com.josue.credential.manager.auth.credential.ManagerCredential;
import com.josue.credential.manager.auth.domain.APIDomainCredential;
import com.josue.credential.manager.auth.manager.Manager;
import com.josue.credential.manager.rest.ListResource;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

public class CredentialControlTest {

    private static final Integer DEFAULT_LIMIT = 100;
    private static final Integer DEFAULT_OFFSET = 0;

    @Mock
    CredentialRepository repository;

    @Mock
    ManagerCredential currentCredential;

    @Mock
    Manager manager;

    @InjectMocks
    CredentialControl control = new CredentialControl();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        currentCredential.setManager(manager);
    }

    @Test
    public void testGetManager() {
        //TODO testing nothing
        String credUuid = "123";
        Manager mockedManager = mock(Manager.class);

        when(repository.getManager(credUuid)).thenReturn(mockedManager);
        Manager foundManager = control.getManagerByCredential(credUuid);
        assertNotNull(mockedManager);
        assertEquals(mockedManager, foundManager);
    }

    @Test
    public void testGetApiCredentials() {
        APIDomainCredential apiCredMock = mock(APIDomainCredential.class, Mockito.RETURNS_DEEP_STUBS);
        List<APIDomainCredential> realList = Arrays.asList(apiCredMock, apiCredMock, apiCredMock);

        String domainUuid = "uuid-123";

        when(currentCredential.getManager()).thenReturn(manager);
        when(repository.getApiCredentials(currentCredential.getManager().getUuid(), domainUuid, DEFAULT_LIMIT, DEFAULT_OFFSET)).thenReturn(realList);

        APICredential credMock = mock(APICredential.class);
        when(apiCredMock.getCredential()).thenReturn(credMock);
        when(credMock.getApiKey()).thenReturn(UUID.randomUUID().toString());

        ListResource<APIDomainCredential> apiCredentials = control.getAPICredentials(domainUuid, DEFAULT_LIMIT, DEFAULT_OFFSET);
        verify(repository, times(1)).getApiCredentials(domainUuid, manager.getUuid(), DEFAULT_LIMIT, DEFAULT_OFFSET);
        assertEquals(realList.size(), apiCredentials.getItems().size());
        for (APIDomainCredential apicred : apiCredentials.getItems()) {
            assertNotNull(apicred);

            ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
            verify(credMock, times(3)).setApiKey(argument.capture());
            assertTrue(argument.getValue().contains("*******"));
        }
    }

    @Test
    public void testGetAPICredentials() {
        APIDomainCredential apiCredMock = mock(APIDomainCredential.class, Mockito.RETURNS_DEEP_STUBS);
        List<APIDomainCredential> realList = Arrays.asList(apiCredMock, apiCredMock, apiCredMock);

        when(currentCredential.getManager()).thenReturn(manager);
        when(repository.getApiCredentials(currentCredential.getManager().getUuid(), DEFAULT_LIMIT, DEFAULT_OFFSET)).thenReturn(realList);

        APICredential credMock = mock(APICredential.class);
        when(apiCredMock.getCredential()).thenReturn(credMock);
        when(credMock.getApiKey()).thenReturn(UUID.randomUUID().toString());

        ListResource<APIDomainCredential> apiCredentials = control.getAPICredentials(DEFAULT_LIMIT, DEFAULT_OFFSET);
        verify(repository, times(1)).getApiCredentials(manager.getUuid(), DEFAULT_LIMIT, DEFAULT_OFFSET);
        assertEquals(realList.size(), apiCredentials.getItems().size());
        for (APIDomainCredential apicred : apiCredentials.getItems()) {
            assertNotNull(apicred);

            ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
            verify(credMock, times(3)).setApiKey(argument.capture());
            assertTrue(argument.getValue().contains("*******"));
        }
    }
}
