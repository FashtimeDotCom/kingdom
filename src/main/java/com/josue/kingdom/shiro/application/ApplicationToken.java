/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.shiro.application;

import java.util.Objects;
import org.apache.shiro.authc.AuthenticationToken;

/**
 *
 * @author Josue
 */
public class ApplicationToken implements AuthenticationToken {

    private final Object appKey;
    private final Object appSecret;
    private final ManagerToken managerToken;

    public ApplicationToken(Object appKey, Object appSecret) {
        this.appKey = appKey;
        this.appSecret = appSecret;
        managerToken = null;
    }

    public ApplicationToken(Object appKey, Object appSecret, ManagerToken managerToken) {
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.managerToken = managerToken;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.appKey);
        hash = 97 * hash + Objects.hashCode(this.appSecret);
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
        final ApplicationToken other = (ApplicationToken) obj;
        if (!Objects.equals(this.appKey, other.appKey)) {
            return false;
        }
        return Objects.equals(this.appSecret, other.appSecret);
    }

    @Override
    public Object getPrincipal() {
        return appKey;
    }

    @Override
    public Object getCredentials() {
        return appSecret;
    }

    public ManagerToken getManagerToken() {
        return managerToken;
    }
}
