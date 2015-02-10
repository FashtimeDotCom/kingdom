/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.credential;

import com.josue.kingdom.credential.entity.LoginRecoveryEvent;
import com.josue.kingdom.credential.entity.PasswordChangeEvent;

/**
 *
 * @author Josue
 */
public interface CredentialMailService {

    void sendLoginRecovery(LoginRecoveryEvent event);

    void sendPasswordToken(PasswordChangeEvent event);

}
