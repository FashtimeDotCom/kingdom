/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.credential;

import com.josue.kingdom.credential.entity.LoginRecoveryEvent;
import com.josue.kingdom.credential.entity.PasswordChangeEvent;
import com.josue.kingdom.util.MailService;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;

/**
 *
 * @author Josue
 */
@ApplicationScoped
public class CredentialService extends MailService {

    //TODO load from template
    public void sendPasswordToken(@Observes(during = TransactionPhase.AFTER_SUCCESS) PasswordChangeEvent event) {

        String subject = "Password reset";
        String message = "The token to reset the password is: <b>" + event.getToken() + "</b>";

        send(event.getTargetManager().getEmail(), subject, message);

    }

    public void sendLoginRecovery(@Observes(during = TransactionPhase.AFTER_SUCCESS) LoginRecoveryEvent event) {

        String subject = "Login recovery";
        String message = "Your login is: <b>" + event.getLogin() + "</b>";

        send(event.getTargetEmail(), subject, message);

    }

}
