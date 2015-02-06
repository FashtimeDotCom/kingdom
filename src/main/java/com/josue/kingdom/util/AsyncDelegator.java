/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.util;

import com.josue.kingdom.credential.CredentialService;
import com.josue.kingdom.credential.entity.LoginRecoveryEvent;
import com.josue.kingdom.credential.entity.PasswordChangeEvent;
import com.josue.kingdom.invitation.InvitationService;
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
    InvitationService invitationService;

    @Inject
    CredentialService credentialService;

    public void observeInvitation(@Observes(during = TransactionPhase.AFTER_SUCCESS) final Invitation event) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                invitationService.sendInvitation(event);
            }
        });
    }

    public void observePasswordChange(@Observes(during = TransactionPhase.AFTER_SUCCESS) final PasswordChangeEvent event) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                credentialService.sendPasswordToken(event);
            }
        });
    }

    public void observeLoginRecovery(@Observes(during = TransactionPhase.AFTER_SUCCESS) final LoginRecoveryEvent event) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                credentialService.sendLoginRecovery(event);
            }
        });
    }

}
