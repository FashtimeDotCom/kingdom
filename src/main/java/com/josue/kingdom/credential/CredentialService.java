/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.credential;

import com.josue.kingdom.MailService;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author Josue
 */
@ApplicationScoped
public class CredentialService extends MailService {

    //TODO load from template
    public void sendPasswordReset(String targetEmail, String newPassword) {

        String subject = "Password reset";
        String message = "Your new password is: <b>" + newPassword + "</b>";

        send(targetEmail, subject, message);

    }

    public void sendLoginRecovery(String targetEmail, String login) {

        String subject = "Login recovery";
        String message = "Your login is: <b>" + login + "</b>";

        send(targetEmail, subject, message);

    }

}
