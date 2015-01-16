/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.credential.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

/*
 * A Manager can have multiple APICredentials, but only one Credential... see Credential class
 */
@Entity
@Table(name = "api_credential", uniqueConstraints = @UniqueConstraint(columnNames = {"api_key"}))
public class APICredential extends Credential {

    public APICredential() {
    }

    public APICredential(String apiKey) {
        this.apiKey = apiKey;
    }

    @NotNull
    @Column(name = "api_key")
    private String apiKey;

    @Override
    public void removeNonCreatable() {
        apiKey = null;
        super.setManager(null);
    }

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

    @Override
    public Object getCredentials() {
        return apiKey;
    }

    @Override
    public Object getPrincipal() {
        return apiKey;
    }

}
