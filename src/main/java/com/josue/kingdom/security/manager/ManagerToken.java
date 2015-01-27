/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.security.manager;

import org.apache.shiro.authc.AuthenticationToken;

/**
 *
 * @author Josue
 */
public class ManagerToken implements AuthenticationToken {

    public static enum CredentialType {

        USERNAME, EMAIL
    }

    private final String login;
    private final char[] password;
    private String appUuid;//Aditional info, not required

    public ManagerToken(String login, char[] password) {
        this.login = login;
        this.password = password;
    }

    public ManagerToken(String login, char[] password, String appUuid) {
        this.login = login;
        this.password = password;
        this.appUuid = appUuid;
    }

    public CredentialType getType() {
        if (isEmail()) {
            return CredentialType.EMAIL;
        }
        return CredentialType.USERNAME;
    }

    @Override
    public Object getPrincipal() {
        return login;
    }

    @Override
    public Object getCredentials() {
        return password;
    }

    private boolean isEmail() {
        if (login != null) {
            if (login.contains("@")) {
                return true;
            }
        }
        return false;
    }

    public String getAppUuid() {
        return appUuid;
    }

    public void setAppUuid(String appUuid) {
        this.appUuid = appUuid;
    }

}
