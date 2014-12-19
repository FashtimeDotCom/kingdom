/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.account;

import com.josue.credential.manager.Resource;
import com.josue.credential.manager.auth.ManagerCredential;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Josue
 */
@Entity
public class Manager extends Resource {

    @NotNull
    @Column(name = "first_name")
    private String firstName;

    @NotNull
    @Column(name = "last_name")
    private String lastName;

    @NotNull
    private String email;

    //Global account
    //Owns the rel
    //store the main login permissions
    @OneToOne
    private ManagerCredential credential;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ManagerCredential getCredential() {
        return credential;
    }

    public void setCredential(ManagerCredential credential) {
        this.credential = credential;
    }

}
