/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.rest;

import com.josue.kingdom.application.entity.Application;
import java.util.Objects;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

/**
 *
 * @author Josue
 */
@MappedSuperclass
public class TenantResource extends Resource {

    @OneToOne(fetch = FetchType.LAZY, optional = false, targetEntity = Application.class)
    @JoinColumn(updatable = false)
    private Resource application;

    public Resource getApplication() {
        return application;
    }

    public void setApplication(Resource application) {
        this.application = application;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.application);
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
        final TenantResource other = (TenantResource) obj;
        return Objects.equals(this.application, other.application);
    }

}
