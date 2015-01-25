/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.application.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.domain.entity.Domain;
import com.josue.kingdom.rest.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import org.apache.shiro.authc.AuthenticationToken;

/**
 *
 * @author Josue
 */
@Entity
@Table(name = "application", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"app_key"})
})
public class Application extends Resource implements AuthenticationToken {

    private String name;
    @Column(name = "app_key")
    @NotNull
    private String appKey;

    @NotNull
    private String secret;// unique
    private String company;
    @NotNull
    private String email;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @OneToMany(mappedBy = "application", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Domain> domains = new ArrayList<>();
    @OneToMany(mappedBy = "application", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Manager> managers = new ArrayList<>();

    public Application() {
    }

    public Application(String uuid) {
        super.setUuid(uuid);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    @JsonIgnore
    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public List<Domain> getDomains() {
        return domains;
    }

    public void setDomains(List<Domain> domains) {
        this.domains = domains;
    }

    public List<Manager> getManagers() {
        return managers;
    }

    public void setManagers(List<Manager> managers) {
        this.managers = managers;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.name);
        hash = 83 * hash + Objects.hashCode(this.secret);
        hash = 83 * hash + Objects.hashCode(this.company);
        hash = 83 * hash + Objects.hashCode(this.email);
        hash = 83 * hash + Objects.hashCode(this.status);
        hash = 83 * hash + Objects.hashCode(this.domains);
        hash = 83 * hash + Objects.hashCode(this.managers);
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
        final Application other = (Application) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.secret, other.secret)) {
            return false;
        }
        if (!Objects.equals(this.company, other.company)) {
            return false;
        }
        if (!Objects.equals(this.email, other.email)) {
            return false;
        }
        if (this.status != other.status) {
            return false;
        }
        if (!Objects.equals(this.domains, other.domains)) {
            return false;
        }
        return Objects.equals(this.managers, other.managers);
    }

    @Override
    public Object getPrincipal() {
        return appKey;
    }

    @Override
    public Object getCredentials() {
        return secret;
    }

}
