/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.util;

import com.josue.kingdom.credential.CredentialMailService;
import com.josue.kingdom.credential.entity.LoginRecoveryEvent;
import com.josue.kingdom.credential.entity.PasswordChangeEvent;
import com.josue.kingdom.invitation.InvitationMailService;
import com.josue.kingdom.invitation.entity.Invitation;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;

/**
 *
 * @author Josue
 */
public class AsyncDelegator {

    @Resource(lookup = "java:comp/DefaultManagedExecutorService")
    private ManagedExecutorService executor;

    @Inject
    CredentialMailService credMailService;

    @Inject
    InvitationMailService invMailService;

    public void observeInvitation(@Observes(during = TransactionPhase.AFTER_SUCCESS) final Invitation event) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                invMailService.sendInvitation(event);
            }
        });
    }

    public void observePasswordChange(@Observes(during = TransactionPhase.AFTER_SUCCESS) final PasswordChangeEvent event) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                credMailService.sendPasswordToken(event);
            }
        });
    }

    public void observeLoginRecovery(@Observes(during = TransactionPhase.AFTER_SUCCESS) final LoginRecoveryEvent event) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                credMailService.sendLoginRecovery(event);
            }
        });
    }

}
