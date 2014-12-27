/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.auth;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author Josue
 */
@Entity
@Table(name = "api_domain_credential", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"domain_uuid", "api_credential_uuid"}),
    @UniqueConstraint(columnNames = {"domain_role_id", "api_credential_uuid"})})
public class APIDomainCredential extends DomainCredential {

    private String name;

    @ManyToOne
    @JoinColumn(name = "api_credential_uuid")
    private APICredential credential;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public APICredential getCredential() {
        return credential;
    }

    public void setCredential(APICredential credential) {
        this.credential = credential;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + Objects.hashCode(this.credential);
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
        final APIDomainCredential other = (APIDomainCredential) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.credential, other.credential)) {
            return false;
        }
        return true;
    }

}
