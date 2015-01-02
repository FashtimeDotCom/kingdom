/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager;

import com.josue.credential.manager.auth.domain.Domain;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author Josue
 * @param <T>
 */
public abstract class RestBoundary<T> {

    public static final String CONTENT_TYPE = "application/json;charset=utf-8";
    public static final String DEFAULT_LIMIT = "50";
    public static final String DEFAULT_OFFSET = "0";

    @GET
    @Produces(value = CONTENT_TYPE)
    public Response list(@QueryParam("limit") @DefaultValue(DEFAULT_LIMIT) Integer limit,
            @QueryParam("offset") @DefaultValue(DEFAULT_OFFSET) Long offset) throws RestException {

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("{uuid}")
    @Produces(value = CONTENT_TYPE)
    public Response getByUuid(@PathParam("uuid") String uuid) throws RestException {
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Consumes(value = CONTENT_TYPE)
    @Produces(value = CONTENT_TYPE)
    public Response create(Domain domain) throws RestException {
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /*
     * Using PUT for partial updates...
     * fields with value are updated.
     * fields with null value should be removed
     * ommited fields should not be updated
     */
    @PUT
    @Path("{uuid}")
    @Consumes(value = CONTENT_TYPE)
    @Produces(value = CONTENT_TYPE)
    public Response update(@PathParam("{uuid}") String uuid, Domain domain) throws RestException {
        return Response.status(Response.Status.NOT_FOUND).build();
    }

}
