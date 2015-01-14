/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.invitation;

import com.josue.credential.manager.business.account.AccountService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Josue
 */
@ApplicationScoped
public class InvitationService {

    @Resource(mappedName = "java:jboss/mail/gmail")
    private Session mailSession;

    private final String systemEmail = "josue.eduardo206@gmail.com";

    public void sendInvitation(ManagerInvitation invitation) {

        try {
            MimeMessage m = new MimeMessage(mailSession);
            Address from = new InternetAddress(systemEmail);
            Address[] to = new InternetAddress[]{new InternetAddress(invitation.getTargetEmail())};

            m.setFrom(from);
            m.setRecipients(Message.RecipientType.TO, to);
            m.setSubject("DOmain invitation");
            m.setSentDate(new java.util.Date());
            m.setContent("You were invited to join Domain ...TODO... <br /> click the below to accept: <br /> " + getInvitationHref(invitation), "text/plain");
            Transport.send(m);
        } catch (AddressException ex) {
            Logger.getLogger(AccountService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(AccountService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //TODO improve
    private String getInvitationHref(ManagerInvitation invitation) {
        return "http://localhost:8080/credential-manager/signup?token=" + invitation.getToken();
    }

}
