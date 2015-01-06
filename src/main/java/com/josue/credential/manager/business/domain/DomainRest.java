/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.domain;

import com.josue.credential.manager.RestBoundary;
import static com.josue.credential.manager.RestBoundary.CONTENT_TYPE;
import static com.josue.credential.manager.RestBoundary.DEFAULT_LIMIT;
import static com.josue.credential.manager.RestBoundary.DEFAULT_OFFSET;
import com.josue.credential.manager.auth.domain.Domain;
import com.josue.credential.manager.auth.domain.ManagerDomainCredential;
import com.josue.credential.manager.rest.ResponseUtils;
import com.josue.credential.manager.rest.ex.RestException;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
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
public class DomainRest extends RestBoundary<Domain> {

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

    /*
     *Creates a new Domain
     */
    @Override
    public Response create(Domain domain) throws RestException {
        Domain createdDomain = control.createDomain(domain);
        return ResponseUtils.buildSimpleResponse(createdDomain, Response.Status.CREATED, info);
    }

    /*
     Update an owned Domain, this method supports partial update
     */
    @Override
    public Response update(String uuid, Domain domain) throws RestException {
        Domain updatedDomain = control.updateDomain(uuid, domain);
        return ResponseUtils.buildSimpleResponse(updatedDomain, Response.Status.OK, info);
    }

//    @Override
//    public Response getByUuid(String uuid) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//
}
