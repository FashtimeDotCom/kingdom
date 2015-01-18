/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.invitation;

import com.josue.kingdom.invitation.entity.Invitation;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import org.mockito.Mockito;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import org.mockito.MockitoAnnotations;

/**
 *
 * @author Josue
 */
public class InvitationServiceTest {

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSendInvitation() {
        InvitationService invitationService = Mockito.spy(new InvitationService());
        String targetEmail = "target@email.com";
        Invitation invitation = Mockito.spy(new Invitation());
        invitation.setTargetEmail(targetEmail);

        doNothing().when(invitationService).send(eq(targetEmail), any(String.class), any(String.class));
        invitationService.sendInvitation(invitation);

        verify(invitationService).send(eq(targetEmail), any(String.class), any(String.class));
    }

}
