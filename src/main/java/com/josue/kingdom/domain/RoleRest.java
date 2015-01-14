/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.domain;

import com.josue.kingdom.domain.entity.Role;
import static com.josue.kingdom.rest.ResponseUtils.CONTENT_TYPE;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 *
 * @author Josue
 */
@Path("roles")
public class RoleRest {

    @Inject
    RoleRepository repository;

    @GET
    @Produces(value = CONTENT_TYPE)
    public Response getSystemRoles() {
        List<Role> roles = repository.findAll(Role.class);
        return Response.status(Response.Status.OK).entity(roles).build();
    }
}
