/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.auth;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Josue
 */

/*
 * A Manager can have multiple APICredentials, but only one Credential...
 */
@Entity
@Table(name = "manager_credential")
//http://stackoverflow.com/questions/1733560/making-foreign-keys-unique-in-jpa
public class ManagerCredential extends Credential {

    @NotNull
    private String login;

    @NotNull
    private String password;


    //*** END ***
    @Override
    public Object getPrincipal() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getCredentials() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}
