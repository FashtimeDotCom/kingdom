/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.shiro.application;

import org.apache.shiro.authc.AuthenticationToken;

/**
 *
 * @author Josue
 */
public class ManagerToken implements AuthenticationToken {

    private final String username;
    private final String email;
    private final char[] password;

    public ManagerToken(String username, String email, char[] password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    @Override
    public Object getPrincipal() {
        if (email == null) {
            return username;
        }
        return email;
    }

    @Override
    public Object getCredentials() {
        return password;
    }

}
