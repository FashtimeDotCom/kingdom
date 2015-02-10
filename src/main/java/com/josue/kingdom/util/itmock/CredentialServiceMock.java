/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.util.itmock;

import com.josue.kingdom.credential.CredentialMailService;
import com.josue.kingdom.credential.entity.LoginRecoveryEvent;
import com.josue.kingdom.credential.entity.PasswordChangeEvent;
import com.josue.kingdom.util.env.Environment;
import com.josue.kingdom.util.env.Stage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Josue
 */
@Environment(stage = Stage.TEST)
public class CredentialServiceMock implements CredentialMailService {

    private static final Logger logger = Logger.getLogger(CredentialServiceMock.class.getName());

    //TODO load from template
    @Override
    public void sendPasswordToken(PasswordChangeEvent event) {
        logger.info("************ CREDENTIALSERVICE MOCK - sendLoginRecovery()************");
        logger.log(Level.INFO, "TARGET EMAIL: {0}", event.getTargetManager().getEmail());
        logger.log(Level.INFO, "TOKEN: {0}", event.getToken());
    }

    @Override
    public void sendLoginRecovery(LoginRecoveryEvent event) {
        logger.info("************ CREDENTIALSERVICE MOCK - sendPasswordReset() ************");
        logger.log(Level.INFO, "TARGET EMAIL: {0}", event.getTargetManager().getEmail());
        logger.log(Level.INFO, "USERNAME: {0}", event.getTargetManager().getUsername());
    }
}
