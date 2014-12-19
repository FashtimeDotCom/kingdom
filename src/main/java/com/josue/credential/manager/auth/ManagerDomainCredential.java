/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.auth;

import com.josue.credential.manager.Resource;
import com.josue.credential.manager.account.Manager;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Josue
 */
@Entity
@Table(name = "manager_domain_credential")
//http://stackoverflow.com/questions/5127129/mapping-many-to-many-association-table-with-extra-columns
public class ManagerDomainCredential extends Resource {

    @ManyToOne
    @JoinColumn(name = "domain_uuid")
    private Domain domain;

    @ManyToOne
    @JoinColumn(name = "manager_uuid")
    private Manager manager;

    //Role for this domain
    @OneToOne
    @JoinColumn(name = "domain_role_uuid")
    private Role role;

}
