/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom;

import com.josue.kingdom.credential.CredentialService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author Josue
 */
public class MailService {

    @Resource(mappedName = "java:jboss/mail/gmail")
    private Session mailSession;

    private final String systemEmail = "josue.eduardo206@gmail.com";

    public void send(String targetEmail, String subject, String content) {
        try {
            //build content... TODO improve
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(content, "text/html; charset=utf-8");
            Multipart multipart = new MimeMultipart(htmlPart);
            multipart.addBodyPart(htmlPart);

            MimeMessage m = new MimeMessage(mailSession);
            Address from = new InternetAddress(systemEmail);
            Address[] to = new InternetAddress[]{new InternetAddress(targetEmail)};

            m.setFrom(from);
            m.setRecipients(Message.RecipientType.TO, to);
            m.setSubject(subject);
            m.setSentDate(new java.util.Date());
            m.setContent(multipart);
            Transport.send(m);
        } catch (AddressException ex) {
            Logger.getLogger(CredentialService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(CredentialService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
