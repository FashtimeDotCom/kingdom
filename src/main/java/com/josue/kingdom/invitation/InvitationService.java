/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.invitation;

import com.josue.kingdom.MailService;
import com.josue.kingdom.invitation.entity.Invitation;
import com.josue.kingdom.invitation.entity.InvitationStatus;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;

/**
 *
 * @author Josue
 */
@ApplicationScoped
public class InvitationService extends MailService {

    @Inject
    InvitationRepository invitationRepository;

    public void sendInvitation(@Observes(during = TransactionPhase.AFTER_SUCCESS) Invitation invitation) {

        String message = "You were invited to join Domain ...TODO... <br /> click the below to accept: <br /> " + getInvitationHref(invitation);
        String subject = "Password reset";
        send(invitation.getTargetEmail(), subject, message);
        updateInvitationStatus(invitation);
    }

    //TODO improve
    private String getInvitationHref(Invitation invitation) {
        return "http://localhost:8080/kingdom/signup?token=" + invitation.getToken();
    }

    private void updateInvitationStatus(Invitation invitation) {
        invitation.setStatus(InvitationStatus.SENT);
        invitationRepository.update(invitation);
    }

}
