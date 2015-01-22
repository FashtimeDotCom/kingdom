/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 *
 * @author Josue
 */
public class KingdomAuthToken implements AuthenticationToken {

    private final Object login;
    private final Object password;
    private final String appKey;

    public KingdomAuthToken(Object login, Object password, String appKey) {
        this.login = login;
        this.password = password;
        this.appKey = appKey;
    }

    @Override
    public Object getPrincipal() {
        return login;
    }

    @Override
    public Object getCredentials() {
        return password;
    }

    public String getAppKey() {
        return appKey;
    }

}
