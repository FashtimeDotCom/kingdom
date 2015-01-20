/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.invitation.mock;

import com.josue.kingdom.invitation.InvitationService;
import com.josue.kingdom.invitation.entity.Invitation;
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
public class InvitationServiceMock extends InvitationService {

    private static final Logger logger = Logger.getLogger(InvitationServiceMock.class.getName());

    @Override
    public void sendInvitation(@Observes(during = TransactionPhase.AFTER_SUCCESS) Invitation invitation) {
        //DO nothing
        logger.info("******** MOCKED INVITATION SERVICE ***********");
        logger.log(Level.INFO, "TARGET EMAIL: {0}", invitation.getTargetEmail());
        logger.log(Level.INFO, "DOMAIN UUID : {0}", invitation.getDomain().getUuid());
        logger.log(Level.INFO, "TOKEN : {0}", invitation.getToken());
        logger.log(Level.INFO, "AUTHOR MANAGER : {0}", invitation.getAuthorManager().getUuid());
        logger.log(Level.INFO, "PERMISSION : {0}", invitation.getPermission().getName());
    }

}
