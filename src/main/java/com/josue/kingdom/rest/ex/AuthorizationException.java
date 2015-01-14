/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.rest.ex;

import com.josue.kingdom.domain.entity.Role;
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

    public AuthorizationException(Role role) {
        super(null, "", String.format("Requires role {0}", role.getName()), Response.Status.BAD_REQUEST);
    }

}
