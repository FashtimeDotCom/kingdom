/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.auth;

import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
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
public class ManagerCredential extends Credential {

    @NotNull
    private String login;

    @NotNull
    private String password;

    //*** START - Informations about this Credential ***
    //Owned domains
    @OneToMany(mappedBy = "owner")//TODO fetch type Lazy ?
    private Set<Domain> ownedDomains;

    //Domain credentials
    @OneToMany(mappedBy = "credential")
    private Set<ManagerDomainCredential> domains;

    @OneToMany(mappedBy = "ownerManagerCredential")
    private Set<APIDomainCredential> apiCredentials;

    //*** END ***
    @Override
    public Object getPrincipal() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getCredentials() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

    public Set<Domain> getOwnedDomains() {
        return ownedDomains;
    }

    public void setOwnedDomains(Set<Domain> ownedDomains) {
        this.ownedDomains = ownedDomains;
    }

    public Set<ManagerDomainCredential> getDomains() {
        return domains;
    }

    public void setDomains(Set<ManagerDomainCredential> domains) {
        this.domains = domains;
    }

    public Set<APIDomainCredential> getApiCredentials() {
        return apiCredentials;
    }

    public void setApiCredentials(Set<APIDomainCredential> apiCredentials) {
        this.apiCredentials = apiCredentials;
    }

}
