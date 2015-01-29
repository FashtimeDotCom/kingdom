/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.security.manager;

import java.util.Arrays;
import java.util.Objects;
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.login);
        hash = 67 * hash + Arrays.hashCode(this.password);
        hash = 67 * hash + Objects.hashCode(this.appUuid);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ManagerToken other = (ManagerToken) obj;
        if (!Objects.equals(this.login, other.login)) {
            return false;
        }
        if (!Arrays.equals(this.password, other.password)) {
            return false;
        }
        if (!Objects.equals(this.appUuid, other.appUuid)) {
            return false;
        }
        return true;
    }

}
