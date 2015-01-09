/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.auth.credential;

import com.josue.credential.manager.rest.Resource;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Josue
 */

/*
 * A Manager can have multiple APICredentials, but only one Credential...
 */
@Entity
@Table(name = "manager_credential", uniqueConstraints
        = @UniqueConstraint(columnNames = {"login"}))
//http://stackoverflow.com/questions/1733560/making-foreign-keys-unique-in-jpa
public class ManagerCredential extends Credential {

    @NotNull
    private String login;

    //TODO check convert to char[]
    @NotNull
    private String password;

    public ManagerCredential() {
    }

    public ManagerCredential(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Override
    public void copyUpdatebleFields(Resource newData) {
        if (newData instanceof ManagerCredential) {
            ManagerCredential managerCredential = (ManagerCredential) newData;
            password = managerCredential.password == null ? password : managerCredential.password;
        }
    }

    @Override
    public Object getPrincipal() {
        return login;
    }

    @Override
    public Object getCredentials() {
        return password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.login);
        hash = 67 * hash + Objects.hashCode(this.password);
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
        final ManagerCredential other = (ManagerCredential) obj;
        if (!Objects.equals(this.login, other.login)) {
            return false;
        }
        return Objects.equals(this.password, other.password);
    }

}
