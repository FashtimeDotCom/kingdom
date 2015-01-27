/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.rest;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Josue
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Resource implements Serializable {

    @Id
    private String uuid;

    @Transient
    private String href;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_created", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date dateCreated;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_update", columnDefinition = "TIMESTAMP")
    private Date lastUpdate;

    @PrePersist
    public void init() {
        this.uuid = base64FromUUID();
        this.dateCreated = new Date();
    }

    @PreUpdate
    public void updateLastUpdate() {
        this.lastUpdate = new Date();
    }

    public static Resource fromHref(String href) {
        Resource res = new Resource();
        res.setHref(href);
        return res;
    }

    public static String base64FromUUID() {
        UUID rand = UUID.randomUUID();
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(rand.getMostSignificantBits());
        bb.putLong(rand.getLeastSignificantBits());
        String base64 = DatatypeConverter.printBase64Binary(bb.array());
        return base64.substring(0, base64.length() - 2).replace("/", "_").replace("+", "-");
    }

    public static Resource fromResource(Resource resource) {
        if (resource != null) {
            Resource res = new Resource();
            res.setUuid(resource.getUuid());
            res.setHref(resource.getHref());
            res.setLastUpdate(resource.getLastUpdate());
            res.setDateCreated(resource.getDateCreated());
        }
        return null;
    }

    protected void copyUpdatable(Resource newData) {
        //Do nothing
    }

    public void removeNonCreatable() {
        //Simply remove system fields
        uuid = null;
        dateCreated = null;
        href = null;
        lastUpdate = null;
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
        if (dateCreated != null) {
            return new Date(dateCreated.getTime());
        }
        return null;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getLastUpdate() {
        if (dateCreated != null) {
            return new Date(lastUpdate.getTime());
        }
        return null;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.uuid);
        hash = 19 * hash + Objects.hashCode(this.href);
        hash = 19 * hash + Objects.hashCode(this.dateCreated);
        hash = 19 * hash + Objects.hashCode(this.lastUpdate);
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
        if (!Objects.equals(this.uuid, other.uuid)) {
            return false;
        }
        if (!Objects.equals(this.href, other.href)) {
            return false;
        }
        if (!Objects.equals(this.dateCreated, other.dateCreated)) {
            return false;
        }
        return Objects.equals(this.lastUpdate, other.lastUpdate);
    }
}
