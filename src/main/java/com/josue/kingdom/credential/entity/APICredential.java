/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.credential.entity;

import com.josue.kingdom.domain.entity.ManagerMembership;
import com.josue.kingdom.rest.Resource;
import com.josue.kingdom.rest.TenantResource;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import org.apache.shiro.authc.AuthenticationToken;

/*
 * A Manager can have multiple APICredentials, but only one Credential... see Credential class
 */
@Entity
@Table(name = "api_credential", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"application_uuid", "api_key"}),
    @UniqueConstraint(columnNames = {"application_uuid", "api_key", "membership_uuid"})})
public class APICredential extends TenantResource implements AuthenticationToken {

    private String name;

    @NotNull
    @Column(name = "api_key")
    private String apiKey;

    @ManyToOne(optional = false)
    ManagerMembership membership;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    public APICredential() {
    }

    public APICredential(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public void removeNonCreatable() {
        apiKey = null;
    }

    @Override
    public void copyUpdatable(Resource newData) {
        if (newData instanceof APICredential) {
            APICredential credential = (APICredential) newData;
            status = credential.getStatus() != null ? credential.getStatus() : status;
            name = credential.getName() != null ? credential.getName() : name;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public ManagerMembership getMembership() {
        return membership;
    }

    public void setMembership(ManagerMembership membership) {
        this.membership = membership;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.name);
        hash = 29 * hash + Objects.hashCode(this.apiKey);
        hash = 29 * hash + Objects.hashCode(this.membership);
        hash = 29 * hash + Objects.hashCode(this.status);
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
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.apiKey, other.apiKey)) {
            return false;
        }
        if (!Objects.equals(this.membership, other.membership)) {
            return false;
        }
        return this.status == other.status;
    }

    @Override
    public Object getPrincipal() {
        return apiKey;
    }

    @Override
    public Object getCredentials() {
        return apiKey;
    }

}
