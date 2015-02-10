/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.rest;

import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Josue
 */
@Path("version")
@ApplicationScoped
public class VersionRest {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVersion() {
        String versionJson = "{\"version\": \"" + UUID.randomUUID().toString() + "\"}";
        return Response.status(Response.Status.OK).entity(versionJson).build();
    }

}
