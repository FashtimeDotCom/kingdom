/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.credential;

import com.josue.kingdom.account.entity.Manager;
import com.josue.kingdom.credential.entity.APICredential;
import com.josue.kingdom.credential.entity.ManagerCredential;
import com.josue.kingdom.domain.entity.APIDomainCredential;
import com.josue.kingdom.rest.ListResource;
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
import org.mockito.Spy;

public class CredentialControlTest {

    private static final Integer DEFAULT_LIMIT = 100;
    private static final Integer DEFAULT_OFFSET = 0;

    @Mock
    CredentialRepository repository;

    @Spy
    ManagerCredential currentCredential = new ManagerCredential();

    @Spy
    Manager manager = new Manager();

    @InjectMocks
    CredentialControl control = new CredentialControl();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        currentCredential.setManager(manager);
    }

//    @Test
//    //TODO move to correct test class
//    public void testGetManager() {
//        //TODO testing nothing
//        String credUuid = "123";
//        Manager mockedManager = mock(Manager.class);
//
//        when(repository.getManager(credUuid)).thenReturn(mockedManager);
//        Manager foundManager = control.getManagerByCredential(credUuid);
//        assertNotNull(mockedManager);
//        assertEquals(mockedManager, foundManager);
//    }
    @Test
    public void testGetApiCredentials() {
        APIDomainCredential apiCredMock = mock(APIDomainCredential.class, Mockito.RETURNS_DEEP_STUBS);
        List<APIDomainCredential> realList = Arrays.asList(apiCredMock, apiCredMock, apiCredMock);

        String domainUuid = "uuid-123";

        when(repository.getAPICredentials(currentCredential.getManager().getUuid(), domainUuid, DEFAULT_LIMIT, DEFAULT_OFFSET)).thenReturn(realList);

        APICredential credMock = mock(APICredential.class);
        when(apiCredMock.getCredential()).thenReturn(credMock);
        when(credMock.getApiKey()).thenReturn(UUID.randomUUID().toString());

        ListResource<APIDomainCredential> apiCredentials = control.getAPICredentials(domainUuid, DEFAULT_LIMIT, DEFAULT_OFFSET);
        verify(repository, times(1)).getAPICredentials(domainUuid, manager.getUuid(), DEFAULT_LIMIT, DEFAULT_OFFSET);
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

        when(repository.getAPICredentials(currentCredential.getManager().getUuid(), DEFAULT_LIMIT, DEFAULT_OFFSET)).thenReturn(realList);

        APICredential credMock = mock(APICredential.class);
        when(apiCredMock.getCredential()).thenReturn(credMock);
        when(credMock.getApiKey()).thenReturn(UUID.randomUUID().toString());

        ListResource<APIDomainCredential> apiCredentials = control.getAPICredentials(DEFAULT_LIMIT, DEFAULT_OFFSET);
        verify(repository, times(1)).getAPICredentials(manager.getUuid(), DEFAULT_LIMIT, DEFAULT_OFFSET);
        assertEquals(realList.size(), apiCredentials.getItems().size());
        for (APIDomainCredential apicred : apiCredentials.getItems()) {
            assertNotNull(apicred);

            ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
            verify(credMock, times(3)).setApiKey(argument.capture());
            assertTrue(argument.getValue().contains("*******"));
        }
    }

    @Test
    public void testGetAPICredential() {
        String apiKeyUuid = "apikey-uuid-123";
        String domainUuid = "domain-uuid-123";
        APIDomainCredential apiCredMock = mock(APIDomainCredential.class, Mockito.RETURNS_DEEP_STUBS);

        when(repository.getAPICredential(currentCredential.getManager().getUuid(), domainUuid, apiKeyUuid)).thenReturn(apiCredMock);

        APICredential credMock = mock(APICredential.class);
        when(apiCredMock.getCredential()).thenReturn(credMock);
        when(credMock.getApiKey()).thenReturn(UUID.randomUUID().toString());

        APIDomainCredential apiCredential = control.getAPICredential(domainUuid, apiKeyUuid);
        verify(repository, times(1)).getAPICredential(manager.getUuid(), domainUuid, apiKeyUuid);

        assertNotNull(apiCredential);

        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(credMock, times(1)).setApiKey(argument.capture());
        assertTrue(argument.getValue().contains("*******"));
    }
}
