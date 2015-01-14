/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.rest.ex;

import javax.ws.rs.core.Response;

/**
 *
 * @author Josue
 */
public class AuthenticationException extends RestException {

    public AuthenticationException() {
        super(null, "", String.format(
                "Invalid credentials"), Response.Status.UNAUTHORIZED);
    }

}
