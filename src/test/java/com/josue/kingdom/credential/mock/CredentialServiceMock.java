/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.credential.mock;

import com.josue.kingdom.credential.CredentialService;
import com.josue.kingdom.credential.entity.LoginRecoveryEvent;
import com.josue.kingdom.credential.entity.PasswordResetEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.Specializes;

/**
 *
 * @author Josue
 */
@Specializes
public class CredentialServiceMock extends CredentialService {

    private static final Logger logger = Logger.getLogger(CredentialServiceMock.class.getName());

    //TODO load from template
    @Override
    public void sendPasswordReset(@Observes(during = TransactionPhase.AFTER_SUCCESS) PasswordResetEvent event) {
        logger.info("************ CREDENTIALSERVICE MOCK - sendLoginRecovery()************");
        logger.log(Level.INFO, "TARGET EMAIL: {0}", event.getTargetEmail());
        logger.log(Level.INFO, "LOGIN: {0}", event.getNewPassword());

    }

    @Override
    public void sendLoginRecovery(@Observes(during = TransactionPhase.AFTER_SUCCESS) LoginRecoveryEvent event) {
        logger.info("************ CREDENTIALSERVICE MOCK - sendPasswordReset() ************");
        logger.log(Level.INFO, "TARGET EMAIL: {0}", event.getTargetEmail());
        logger.log(Level.INFO, "NEW PASSWORD: {0}", event.getLogin());

    }

}
