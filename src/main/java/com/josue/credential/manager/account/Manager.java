/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.account;

import com.josue.credential.manager.Resource;
import com.josue.credential.manager.auth.APIDomainCredential;
import com.josue.credential.manager.auth.Domain;
import com.josue.credential.manager.auth.ManagerCredential;
import com.josue.credential.manager.auth.ManagerDomainCredential;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Josue
 */
@Entity
@Table(name = "manager", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"credential_uuid"})
})
public class Manager extends Resource {

    @NotNull
    @Column(name = "first_name")
    private String firstName;

    @NotNull
    @Column(name = "last_name")
    private String lastName;

    @NotNull
    private String email;

    //Global account
    //Owns the rel
    //store the main login permissions
    @OneToOne
    private ManagerCredential credential;
    
    //*** START - Informations about this Credential ***
    //Owned domains
    @OneToMany(mappedBy = "owner")//TODO fetch type Lazy ?
    private Set<Domain> ownedDomains;

    //Domain credentials
    @OneToMany(mappedBy = "manager")
    private Set<ManagerDomainCredential> domains;

    @OneToMany(mappedBy = "manager")
    private Set<APIDomainCredential> apiCredentials;
    //*** END ***

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ManagerCredential getCredential() {
        return credential;
    }

    public void setCredential(ManagerCredential credential) {
        this.credential = credential;
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
