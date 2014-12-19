/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.auth;

import com.josue.credential.manager.Resource;
import com.josue.credential.manager.account.Manager;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author Josue
 */
@Entity
public class Domain extends Resource {

    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private DomainStatus status;

//    //TODO other properties
    @ManyToOne
    @JoinColumn(name = "owner_uuid")
    private Manager owner;

    @OneToMany(mappedBy = "domain", cascade = CascadeType.ALL)//TODO check cascade
    private Set<ManagerDomainCredential> domainManagers;

}
