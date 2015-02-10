/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.invitation;

import com.josue.kingdom.invitation.entity.Invitation;

/**
 *
 * @author Josue
 */
public interface InvitationMailService {

    void sendInvitation(Invitation event);

}
