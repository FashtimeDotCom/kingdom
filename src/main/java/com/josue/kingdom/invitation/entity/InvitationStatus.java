/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.invitation.entity;

/**
 *
 * @author iFood
 */
public enum InvitationStatus {

    CREATED, //Invitation is saved on database
    SENT, //Invitation was sent to target email
    SIGNING_UP, //User doesnt exist, optional phase
    COMPLETED, //Domain and permission assigned to manager
    EXPIRED, //Expired, default is 2 days
    FAILED, // Failed for any reason
}
