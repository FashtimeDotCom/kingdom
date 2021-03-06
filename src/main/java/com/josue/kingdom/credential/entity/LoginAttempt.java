/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.credential.entity;

import com.josue.kingdom.rest.TenantResource;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 *
 * @author Josue
 */
@Entity
@Table(name = "login_attempt")
public class LoginAttempt extends TenantResource {

    public static enum LoginStatus {

        SUCCESSFUL, FAILED
    }

    private String login;

    @Enumerated(EnumType.STRING)
    private LoginStatus status;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public LoginStatus getStatus() {
        return status;
    }

    public void setStatus(LoginStatus status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.login);
        hash = 17 * hash + Objects.hashCode(this.status);
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
        final LoginAttempt other = (LoginAttempt) obj;

        if (!Objects.equals(this.login, other.login)) {
            return false;
        }
        return this.status == other.status;
    }

}
