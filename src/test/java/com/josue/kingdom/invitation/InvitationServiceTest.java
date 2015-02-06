/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.invitation;

import com.josue.kingdom.application.ApplicationRepository;
import com.josue.kingdom.application.entity.Application;
import com.josue.kingdom.application.entity.ApplicationConfig;
import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.invitation.entity.Invitation;
import com.josue.kingdom.invitation.entity.InvitationStatus;
import javax.mail.MessagingException;
import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
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
public class InvitationServiceTest {

    @Mock
    ApplicationRepository appRepository;

    @Mock
    InvitationRepository invRepository;

    @InjectMocks
    InvitationService invitationService = Mockito.spy(new InvitationService());

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSendInvitation() throws MessagingException {

        String targetEmail = "target@email.com";
        Manager targetManager = new Manager();
        targetManager.setEmail(targetEmail);
        targetManager.setFirstName("firstName");

        Manager authorManager = new Manager();
        authorManager.setFirstName("author-firstName");

        Invitation event = Mockito.spy(new Invitation());
        event.setTargetManager(targetManager);
        event.setApplication(new Application());
        event.setAuthorManager(authorManager);
        event.setToken("the-token-123");

        String authorManagerParam = "$authorManager";
        String targetManagerParam = "$targetManager";
        String appUrl = "$appurl";
        String template = "<p>" + authorManagerParam + " - " + targetManagerParam + " - " + appUrl + "</p>";

        ApplicationConfig config = Mockito.spy(new ApplicationConfig());
        config.setInvitationEmailTemplate(template);
        config.setApplicationUrl("http://localhost:8080");

        doNothing().when(invitationService).send(any(String.class), any(String.class), any(String.class));
        when(appRepository.getApplicationConfig(event.getApplication().getUuid())).thenReturn(config);

        doNothing().when(invitationService).send(eq(targetEmail), any(String.class), any(String.class));
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        invitationService.sendInvitation(event);

        verify(invitationService).send(any(String.class), any(String.class), argument.capture());
        assertFalse(argument.getValue().contains(authorManagerParam));
        assertFalse(argument.getValue().contains(targetManagerParam));

        verify(invitationService).send(eq(targetEmail), any(String.class), any(String.class));
        verify(event).setStatus(InvitationStatus.SENT);
        verify(invRepository).update(event);
    }

}
