/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.credential;

import com.josue.kingdom.application.ApplicationRepository;
import com.josue.kingdom.application.entity.ApplicationConfig;
import com.josue.kingdom.credential.entity.LoginRecoveryEvent;
import com.josue.kingdom.credential.entity.PasswordChangeEvent;
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
public class CredentialService extends MailService {

    @Inject
    ApplicationRepository applicationRepository;

    private final String PASSWORD_URL = "\\$url";
    private final String LOGIN_PARAM = "\\$login";
    private final String APP_URL = "\\$appurl";
    private final String DEFAULT_LOGIN_SUBJECT = "Login recovery";
    private final String DEFAULT_PASSWORD_SUBJECT = "Password reset";
    private final String TOKEN_PARAM = "?token=";

    //TODO load from template
    public void sendPasswordToken(@Observes(during = TransactionPhase.AFTER_SUCCESS) PasswordChangeEvent event) {

        ApplicationConfig config = applicationRepository.getApplicationConfig(event.getApplication().getUuid());

        //TODO template can be null
        String template = config.getPasswordEmailTemplate();
        String parsedBody = template.replaceAll(PASSWORD_URL, config.getPasswordCallbackUrl() + TOKEN_PARAM + event.getToken())
                .replaceAll(LOGIN_PARAM, event.getTargetManager().getUsername())
                .replaceAll(APP_URL, config.getApplicationUrl());

        send(event.getTargetManager().getEmail(), DEFAULT_PASSWORD_SUBJECT, parsedBody);
    }

    public void sendLoginRecovery(@Observes(during = TransactionPhase.AFTER_SUCCESS) LoginRecoveryEvent event) {

        ApplicationConfig config = applicationRepository.getApplicationConfig(event.getApplication().getUuid());

        String template = config.getLoginRecoveryEmailTemplate();
        String parsedBody = template.replaceAll(LOGIN_PARAM, event.getTargetManager().getUsername()).replaceAll(APP_URL, config.getApplicationUrl());

        send(event.getTargetManager().getUuid(), DEFAULT_LOGIN_SUBJECT, parsedBody);
    }

}
