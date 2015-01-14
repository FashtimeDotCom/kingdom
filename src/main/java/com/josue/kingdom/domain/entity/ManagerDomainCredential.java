/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.domain.entity;

import com.josue.kingdom.credential.entity.ManagerCredential;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author Josue
 */
@Entity
@Table(name = "manager_domain_credential", uniqueConstraints
        = @UniqueConstraint(columnNames = {"domain_uuid", "manager_credential_uuid"}))
//http://stackoverflow.com/questions/5127129/mapping-many-to-many-association-table-with-extra-columns
public class ManagerDomainCredential extends DomainCredential {

    @ManyToOne
    @JoinColumn(name = "manager_credential_uuid")
    private ManagerCredential credential;

    public ManagerCredential getCredential() {
        return credential;
    }

    public void setCredential(ManagerCredential credential) {
        this.credential = credential;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.credential);
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
        final ManagerDomainCredential other = (ManagerDomainCredential) obj;
        if (!Objects.equals(this.credential, other.credential)) {
            return false;
        }
        return true;
    }

}
