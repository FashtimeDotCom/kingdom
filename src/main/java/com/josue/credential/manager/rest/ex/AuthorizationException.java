/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.rest.ex;

import javax.ws.rs.core.Response;

/**
 *
 * @author Josue
 */
public class AuthorizationException extends RestException {

    public AuthorizationException() {
        super(null, "", String.format(
                "Access denied for this resource"), Response.Status.FORBIDDEN);
    }

}
