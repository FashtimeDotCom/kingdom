/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.invitation;

import com.josue.kingdom.application.ApplicationRepository;
import com.josue.kingdom.application.entity.ApplicationConfig;
import com.josue.kingdom.invitation.entity.Invitation;
import com.josue.kingdom.invitation.entity.InvitationStatus;
import com.josue.kingdom.util.MailService;
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

    public void sendInvitation(@Observes(during = TransactionPhase.AFTER_SUCCESS) Invitation event) {

        ApplicationConfig config = applicationRepository.getApplicationConfig(event.getApplication().getUuid());
        String rawHtml = config.getInvitationEmailTemplate();
        String parseHtml = rawHtml.replaceAll(INVITATION_URL, config.getAccountCallbackUrl() + TOKEN_PARAM + event.getToken())
                .replaceAll(TARGET_MANAGER_PARAM, event.getTargetManager().getFirstName())
                .replaceAll(AUTHOR_MANAGER_PARAM, event.getAuthorManager().getFirstName())
                .replaceAll(APP_URL, config.getApplicationUrl());

        send(event.getTargetManager().getEmail(), DEFAULT_INVITATION_SUBJECT, parseHtml);
        updateInvitationStatus(event);
    }

    private void updateInvitationStatus(Invitation invitation) {
        invitation.setStatus(InvitationStatus.SENT);
        invitationRepository.update(invitation);
    }

}
