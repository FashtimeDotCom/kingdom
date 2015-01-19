/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.credential.mock;

import com.josue.kingdom.credential.CredentialService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.inject.Specializes;

/**
 *
 * @author Josue
 */
@Specializes
public class CredentialServiceMock extends CredentialService {

    private static final Logger logger = Logger.getLogger(CredentialServiceMock.class.getName());

    @Override
    public void sendLoginRecovery(String targetEmail, String login) {
        logger.info("************ CREDENTIALSERVICE MOCK - sendLoginRecovery()************");
        logger.log(Level.INFO, "TARGET EMAIL: {0}", targetEmail);
        logger.log(Level.INFO, "LOGIN: {0}", login);
    }

    @Override
    public void sendPasswordReset(String targetEmail, String newPassword) {
        logger.info("************ CREDENTIALSERVICE MOCK - sendPasswordReset() ************");
        logger.log(Level.INFO, "TARGET EMAIL: {0}", targetEmail);
        logger.log(Level.INFO, "NEW PASSWORD: {0}", newPassword);

    }

}
