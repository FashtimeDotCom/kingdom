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
public class PasswordResetEvent {

    private String targetEmail;
    private String newPassword;

    public PasswordResetEvent() {

    }

    public PasswordResetEvent(String targetEmail, String content) {
        this.targetEmail = targetEmail;
        this.newPassword = content;
    }

    public String getTargetEmail() {
        return targetEmail;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
