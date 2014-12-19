/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.auth;

import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
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
//http://stackoverflow.com/questions/1733560/making-foreign-keys-unique-in-jpa
@Table(name = "manager_credential", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"manager_uuid"})
})
public class ManagerCredential extends Credential {

    @NotNull
    private String login;

    @NotNull
    private String password;

    //Informations about this Credential
    @OneToMany(mappedBy = "owner")
    private Set<Domain> ownedDomains;

    @OneToMany(mappedBy = "manager")
    private Set<DomainManagerCredential> domains;

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
    public Object getPrincipal() {
        return login;
    }

    @Override
    public Object getCredentials() {
        //TODO check
        return password;
    }

}
