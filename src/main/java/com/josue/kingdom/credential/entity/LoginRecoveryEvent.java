/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.credential.entity;

/**
 *
 * @author Josue
 */
public class LoginRecoveryEvent {

    private String targetEmail;
    private String login;

    public LoginRecoveryEvent() {
    }

    public LoginRecoveryEvent(String targetEmail, String login) {
        this.targetEmail = targetEmail;
        this.login = login;
    }

    public String getTargetEmail() {
        return targetEmail;
    }

    public String getLogin() {
        return login;
    }

}
