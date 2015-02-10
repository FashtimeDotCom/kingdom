/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.util.itmock;

import com.josue.kingdom.invitation.InvitationMailService;
import com.josue.kingdom.invitation.entity.Invitation;
import com.josue.kingdom.util.env.Environment;
import com.josue.kingdom.util.env.Stage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Josue
 */
@Environment(stage = Stage.TEST)
public class InvitationServiceMock implements InvitationMailService {

    private static final Logger logger = Logger.getLogger(InvitationServiceMock.class.getName());

    @Override
    public void sendInvitation(Invitation invitation) {
        //DO nothing
        logger.info("******** MOCKED INVITATION SERVICE ***********");
        logger.log(Level.INFO, "TARGET EMAIL: {0}", invitation.getTargetManager().getEmail());
        logger.log(Level.INFO, "DOMAIN UUID : {0}", invitation.getDomain().getUuid());
        logger.log(Level.INFO, "TOKEN : {0}", invitation.getToken());
        logger.log(Level.INFO, "AUTHOR MANAGER : {0}", invitation.getAuthorManager().getUuid());
        logger.log(Level.INFO, "PERMISSION : {0}", invitation.getPermission().getName());
    }
}
