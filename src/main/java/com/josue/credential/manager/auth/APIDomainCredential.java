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
@Table(name = "api_domain_credential")
public class APIDomainCredential extends Resource {

    @ManyToOne
    @JoinColumn(name = "domain_uuid")
    private Domain domain;

    @ManyToOne
    @JoinColumn(name = "api_credential_uuid")
    private APICredential credential;

    @OneToOne
    @JoinColumn(name = "role_uuid")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "manager_uuid")
    private Manager manager;

}
