/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.domain;

import com.josue.credential.manager.auth.domain.Domain;
import com.josue.credential.manager.auth.domain.ManagerDomainCredential;
import com.josue.credential.manager.rest.ResponseUtils;
import com.josue.credential.manager.rest.ex.RestException;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author Josue
 */
@Path("domains")
@ApplicationScoped
public class DomainRest {

    private static final String CONTENT_TYPE = "application/json;charset=utf-8";
    private static final String DEFAULT_LIMIT = "50";
    private static final String DEFAULT_OFFSET = "0";

    @Inject
    DomainControl control;

    @Context
    UriInfo info;

    @GET
    @Path("joined")
    @Produces(value = CONTENT_TYPE)
    public Response listJoinedDomains(@QueryParam("limit") @DefaultValue(DEFAULT_LIMIT) Integer limit,
            @QueryParam("offset") @DefaultValue(DEFAULT_OFFSET) Long offset) throws RestException {
        List<ManagerDomainCredential> foundDomains = control.getJoinedDomains();

        long totalCount = control.countDomainCredentials();
        return ResponseUtils.buildListResourceResponse(foundDomains, Response.Status.OK, info, totalCount, limit, offset);
    }

    @GET
    @Path("joined/{uuid}")
    @Produces(value = CONTENT_TYPE)
    public Response listJoinedDomains(@PathParam("uuid") String uuid) throws RestException {
        ManagerDomainCredential foundDomains = control.getJoinedDomainByUuid(uuid);
        return ResponseUtils.buildSimpleResponse(foundDomains, Response.Status.OK, info);
    }

    @GET
    @Path("owned")
    @Produces(value = CONTENT_TYPE)
    public Response getOwnedDomains(@QueryParam("limit") @DefaultValue(DEFAULT_LIMIT) Integer limit,
            @QueryParam("offset") @DefaultValue(DEFAULT_OFFSET) Long offset) throws RestException {
        List<Domain> foundDomains = control.getOwnedDomains();

        long totalCount = control.countOwnedDomains();
        return ResponseUtils.buildListResourceResponse(foundDomains, Response.Status.OK, info, totalCount, limit, offset);
    }

    @GET
    @Path("owned/{uuid}")
    @Produces(value = CONTENT_TYPE)
    public Response getOwnedDomainByUuid(@QueryParam("limit") @DefaultValue(DEFAULT_LIMIT) Integer limit,
            @QueryParam("offset") @DefaultValue(DEFAULT_OFFSET) Long offset) throws RestException {
        List<Domain> foundDomains = control.getOwnedDomains();

        long totalCount = control.countOwnedDomains();
        return ResponseUtils.buildListResourceResponse(foundDomains, Response.Status.OK, info, totalCount, limit, offset);
    }

    @POST
    @Consumes(value = CONTENT_TYPE)
    @Produces(value = CONTENT_TYPE)
    public Response create(Domain domain) throws RestException {
        Domain createdDomain = control.createDomain(domain);
        return ResponseUtils.buildSimpleResponse(createdDomain, Response.Status.CREATED, info);
    }

    /*
     Update an owned Domain, this method supports partial update
     */
    @PUT
    @Path("{uuid}")
    @Consumes(value = CONTENT_TYPE)
    @Produces(value = CONTENT_TYPE)
    public Response update(@PathParam("uuid") String uuid, Domain domain) throws RestException {
        Domain updatedDomain = control.updateDomain(uuid, domain);
        return ResponseUtils.buildSimpleResponse(updatedDomain, Response.Status.OK, info);
    }

    @DELETE
    @Path("{uuid}")
    @Consumes(value = CONTENT_TYPE)
    @Produces(value = CONTENT_TYPE)
    public Response delete(@PathParam("uuid") String uuid) throws RestException {
        control.deleteDomain(uuid);
        return ResponseUtils.buildSimpleResponse(null, Response.Status.NO_CONTENT, info);
    }

}
