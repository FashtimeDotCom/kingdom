/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.domain;

import com.josue.credential.manager.auth.domain.Domain;
import com.josue.credential.manager.auth.domain.ManagerDomainCredential;
import com.josue.credential.manager.business.credential.CredentialRest;
import com.josue.credential.manager.business.role.RoleRest;
import com.josue.credential.manager.rest.ListResource;
import com.josue.credential.manager.rest.ResponseUtils;
import static com.josue.credential.manager.rest.ResponseUtils.CONTENT_TYPE;
import static com.josue.credential.manager.rest.ResponseUtils.DEFAULT_LIMIT;
import static com.josue.credential.manager.rest.ResponseUtils.DEFAULT_OFFSET;
import com.josue.credential.manager.rest.ex.RestException;
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
    CredentialRest credentialLocator;

    @Inject
    RoleRest roleLocator;

    @Path("{domainUuid}/credentials")
    public CredentialRest credentials() throws RestException {
        return credentialLocator;
    }

    @Path("{domainUuid}/roles")
    public RoleRest roles() throws RestException {
        return roleLocator;
    }
}
