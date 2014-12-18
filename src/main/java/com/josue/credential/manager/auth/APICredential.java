/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.auth;

import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

/**
 *
 * @author Josue
 */
@Entity
@Table(name = "api_credential")
/*
 * A Manager can have multiple APICredentials, but only one Credential... see Credential class
 */
@AssociationOverride(name = "manager",
        joinColumns = @JoinColumn(name = "manager_uuid", unique = false))
public class APICredential extends Credential {

    @Column(name = "api_key")
    private String apiKey;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.apiKey != null ? this.apiKey.hashCode() : 0);
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
        final APICredential other = (APICredential) obj;
        return !((this.apiKey == null) ? (other.apiKey != null) : !this.apiKey.equals(other.apiKey));
    }

}