/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.domain;

import com.josue.kingdom.credential.APICredentialSubResource;
import com.josue.kingdom.domain.entity.Domain;
import com.josue.kingdom.domain.entity.ManagerDomainCredential;
import com.josue.kingdom.rest.ListResource;
import com.josue.kingdom.rest.ResponseUtils;
import static com.josue.kingdom.rest.ResponseUtils.CONTENT_TYPE;
import static com.josue.kingdom.rest.ResponseUtils.DEFAULT_LIMIT;
import static com.josue.kingdom.rest.ResponseUtils.DEFAULT_OFFSET;
import com.josue.kingdom.rest.ex.RestException;
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
public class DomainResource {

    @Inject
    DomainControl control;

    @Context
    UriInfo info;

    @GET
    @Path("joined")
    @Produces(value = CONTENT_TYPE)
    public Response listJoinedDomains(@QueryParam("limit") @DefaultValue(DEFAULT_LIMIT) Integer limit,
            @QueryParam("offset") @DefaultValue(DEFAULT_OFFSET) Integer offset) throws RestException {
        ListResource<ManagerDomainCredential> foundDomains = control.getJoinedDomains(limit, offset);

        return ResponseUtils.buildSimpleResponse(foundDomains, Response.Status.OK, info);
    }

    @GET
    @Path("joined/{uuid}")
    @Produces(value = CONTENT_TYPE)
    public Response listJoinedDomainByUuid(@PathParam("uuid") String uuid) throws RestException {
        ManagerDomainCredential foundDomains = control.getJoinedDomain(uuid);
        return ResponseUtils.buildSimpleResponse(foundDomains, Response.Status.OK, info);
    }

    @GET
    @Path("owned")
    @Produces(value = CONTENT_TYPE)
    public Response getOwnedDomains(@QueryParam("limit") @DefaultValue(DEFAULT_LIMIT) Integer limit,
            @QueryParam("offset") @DefaultValue(DEFAULT_OFFSET) Integer offset) throws RestException {
        ListResource<Domain> foundDomains = control.getOwnedDomains(limit, offset);

        return ResponseUtils.buildSimpleResponse(foundDomains, Response.Status.OK, info);
    }

    @GET
    @Path("owned/{uuid}")
    @Produces(value = CONTENT_TYPE)
    public Response getOwnedDomainByUuid(@QueryParam("limit") @DefaultValue(DEFAULT_LIMIT) Integer limit,
            @QueryParam("offset") @DefaultValue(DEFAULT_OFFSET) Integer offset) throws RestException {
        ListResource<Domain> foundDomains = control.getOwnedDomains(limit, offset);

        return ResponseUtils.buildSimpleResponse(foundDomains, Response.Status.OK, info);
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

    //##### SUB RESOURCE LOCATORS #####
    @Inject
    APICredentialSubResource credentialLocator;

    @Inject
    DomainRoleSubResource roleLocator;

    @Path("{domainUuid}/apikeys")
    public APICredentialSubResource credentials() throws RestException {
        return credentialLocator;
    }

    @Path("{domainUuid}/roles")
    public DomainRoleSubResource roles() throws RestException {
        return roleLocator;
    }
}