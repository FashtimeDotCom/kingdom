/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.account;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Josue
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Resource implements Serializable {

    @Id
    private String uuid;
    private String href;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_created", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date dateCreated;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_update", columnDefinition = "TIMESTAMP")
    private Date lastUpdate;

    @PrePersist
    public void init() {
        this.uuid = UUID.randomUUID().toString();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + (this.uuid != null ? this.uuid.hashCode() : 0);
        hash = 13 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 13 * hash + (this.dateCreated != null ? this.dateCreated.hashCode() : 0);
        hash = 13 * hash + (this.lastUpdate != null ? this.lastUpdate.hashCode() : 0);
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
        final Resource other = (Resource) obj;
        if ((this.uuid == null) ? (other.uuid != null) : !this.uuid.equals(other.uuid)) {
            return false;
        }
        if ((this.href == null) ? (other.href != null) : !this.href.equals(other.href)) {
            return false;
        }
        if (this.dateCreated != other.dateCreated && (this.dateCreated == null || !this.dateCreated.equals(other.dateCreated))) {
            return false;
        }
        if (this.lastUpdate != other.lastUpdate && (this.lastUpdate == null || !this.lastUpdate.equals(other.lastUpdate))) {
            return false;
        }
        return true;
    }

}
