/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.invitation;

import com.josue.kingdom.MailService;
import com.josue.kingdom.application.ApplicationRepository;
import com.josue.kingdom.application.entity.ApplicationConfig;
import com.josue.kingdom.invitation.entity.Invitation;
import com.josue.kingdom.invitation.entity.InvitationStatus;
import com.josue.kingdom.util.env.Environment;
import com.josue.kingdom.util.env.Stage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.mail.MessagingException;

/**
 *
 * @author Josue
 */
@Environment(stage = Stage.PRODUCTION)
public class InvitationService extends MailService implements InvitationMailService {

    private final String INVITATION_URL = "\\$url";
    private final String AUTHOR_MANAGER_PARAM = "\\$authorManager";
    private final String TARGET_MANAGER_PARAM = "\\$targetManager";
    private final String APP_URL = "\\$appurl";

    private final String DEFAULT_INVITATION_SUBJECT = "You have a new invitation !";
    private final String TOKEN_PARAM = "?token=";

    @Inject
    InvitationRepository invitationRepository;

    @Inject
    ApplicationRepository applicationRepository;

    @Override
    public void sendInvitation(Invitation event) {
        try {
            ApplicationConfig config = applicationRepository.getApplicationConfig(event.getApplication().getUuid());
            String rawHtml = config.getInvitationEmailTemplate();
            String parseHtml = rawHtml.replaceAll(INVITATION_URL, config.getAccountCallbackUrl() + TOKEN_PARAM + event.getToken())
                    .replaceAll(TARGET_MANAGER_PARAM, event.getTargetManager().getFirstName())
                    .replaceAll(AUTHOR_MANAGER_PARAM, event.getAuthorManager().getFirstName())
                    .replaceAll(APP_URL, config.getApplicationUrl());

            //Async starts from MailService
            send(event.getTargetManager().getEmail(), DEFAULT_INVITATION_SUBJECT, parseHtml);
            updateInvitationStatus(event);
        } catch (MessagingException ex) {
            Logger.getLogger(InvitationService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void updateInvitationStatus(Invitation invitation) {
        invitation.setStatus(InvitationStatus.SENT);
        invitationRepository.update(invitation);
    }
}
