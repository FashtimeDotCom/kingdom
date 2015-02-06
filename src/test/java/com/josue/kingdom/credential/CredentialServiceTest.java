/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.credential;

import com.josue.kingdom.application.ApplicationRepository;
import com.josue.kingdom.application.entity.Application;
import com.josue.kingdom.application.entity.ApplicationConfig;
import com.josue.kingdom.credential.entity.LoginRecoveryEvent;
import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.credential.entity.PasswordChangeEvent;
import com.josue.kingdom.invitation.InvitationRepository;
import javax.mail.MessagingException;
import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

/**
 *
 * @author Josue
 */
public class CredentialServiceTest {

    @Mock
    ApplicationRepository appRepository;

    @Mock
    InvitationRepository invRepository;

    @InjectMocks
    CredentialService service = Mockito.spy(new CredentialService());

    Manager targetManager;
    Manager authorManager;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        String targetEmail = "target@email.com";
        targetManager = new Manager();
        targetManager.setEmail(targetEmail);
        targetManager.setFirstName("firstName");
        targetManager.setUsername("a-username");

        authorManager = new Manager();
        authorManager.setFirstName("author-firstName");

    }

    @Test
    public void testSendPasswordToken() throws MessagingException {

        String passwordUrl = "$url";
        String login = "$login";
        String appUrl = "$appurl";
        String template = "<p>" + passwordUrl + " - " + login + " - " + appUrl + "</p>";

        PasswordChangeEvent event = Mockito.spy(new PasswordChangeEvent());
        event.setTargetManager(targetManager);
        event.setApplication(new Application());
        event.setToken("the-token-123");

        ApplicationConfig config = Mockito.spy(new ApplicationConfig());
        config.setPasswordEmailTemplate(template);
        config.setApplicationUrl("http://localhost:8080");

        when(appRepository.getApplicationConfig(event.getApplication().getUuid())).thenReturn(config);
        doNothing().when(service).send(any(String.class), any(String.class), any(String.class));

        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);

        service.sendPasswordToken(event);
        verify(service).send(any(String.class), any(String.class), argument.capture());
        assertFalse(argument.getValue().contains(passwordUrl));
        assertFalse(argument.getValue().contains(login));
        assertFalse(argument.getValue().contains(appUrl));

    }

    @Test
    public void testSendLoginRecovery() throws MessagingException {
        String login = "$login";
        String appUrl = "$appurl";
        String template = "<p>" + login + " - " + appUrl + "</p>";

        ApplicationConfig config = Mockito.spy(new ApplicationConfig());
        config.setLoginRecoveryEmailTemplate(template);
        config.setApplicationUrl("http://localhost:8080");

        LoginRecoveryEvent event = Mockito.spy(new LoginRecoveryEvent());
        event.setApplication(new Application());
        event.setTargetManager(targetManager);

        when(appRepository.getApplicationConfig(event.getApplication().getUuid())).thenReturn(config);
        doNothing().when(service).send(any(String.class), any(String.class), any(String.class));

        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        service.sendLoginRecovery(event);

        verify(service).send(any(String.class), any(String.class), argument.capture());
        assertFalse(argument.getValue().contains(login));
        assertFalse(argument.getValue().contains(appUrl));
    }

}
