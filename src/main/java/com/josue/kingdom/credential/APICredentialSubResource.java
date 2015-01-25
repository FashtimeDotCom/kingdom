/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.credential;

import com.josue.kingdom.credential.entity.APICredential;
import com.josue.kingdom.rest.ListResource;
import com.josue.kingdom.rest.ResponseUtils;
import static com.josue.kingdom.rest.ResponseUtils.CONTENT_TYPE;
import static com.josue.kingdom.rest.ResponseUtils.DEFAULT_LIMIT;
import static com.josue.kingdom.rest.ResponseUtils.DEFAULT_OFFSET;
import com.josue.kingdom.rest.ex.RestException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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
 *
 * This class is a sub resource locator, that always have to bel called from
 * DomainResource or any other ROOT endpoint
 */
@ApplicationScoped
public class APICredentialSubResource {

    @Inject
    CredentialControl control;

    @Context
    UriInfo info;

    @GET
    @Produces(value = CONTENT_TYPE)
    public Response getAPICredentials(@PathParam("domainUuid") String domainUuid,
            @QueryParam("limit") @DefaultValue(DEFAULT_LIMIT) Integer limit,
            @QueryParam("offset") @DefaultValue(DEFAULT_OFFSET) Integer offset) throws RestException {
        ListResource<APICredential> apiCredentials = control.getAPICredentials(domainUuid, limit, offset);
        return ResponseUtils.buildSimpleResponse(apiCredentials, Response.Status.OK, info);
    }

    @POST
    @Produces(value = CONTENT_TYPE)
    public Response createAPICredential(@PathParam("domainUuid") String domainUuid, APICredential apiDomCred) throws RestException {
        APICredential apiCredential = control.createAPICredential(domainUuid, apiDomCred);
        return ResponseUtils.buildSimpleResponse(apiCredential, Response.Status.OK, info);
    }

    @PUT
    @Path("{uuid}")
    @Produces(value = CONTENT_TYPE)
    public Response updateAPICredential(@PathParam("domainUuid") String domainUuid, @PathParam("uuid") String uuid, APICredential apiDomCred) throws RestException {
        APICredential apiCredential = control.updateAPICredential(domainUuid, uuid, apiDomCred);
        return ResponseUtils.buildSimpleResponse(apiCredential, Response.Status.OK, info);
    }

    //TODO change @PathParam to have a meaningful name for APICredential resource
    @DELETE
    @Path("{apikeyUuid}")
    @Produces(value = CONTENT_TYPE)
    public Response deleteAPICredential(@PathParam("domainUuid") String domainUuid, @PathParam("apikeyUuid") String apikeyUuid) throws RestException {
        control.deleteAPICredential(domainUuid, apikeyUuid);
        return ResponseUtils.buildSimpleResponse(null, Response.Status.NO_CONTENT, info);
    }

    @GET
    @Path("{apikeyUuid}")
    @Produces(value = CONTENT_TYPE)//DomainUuid is not used in this case because APICredential is unique amoung all domains
    public Response getAPICredential(@PathParam("domainUuid") String domainUuid, @PathParam("apikeyUuid") String apikeyUuid) {
        APICredential apiCredential = control.getAPICredential(apikeyUuid);
        return ResponseUtils.buildSimpleResponse(apiCredential, Response.Status.OK, info);
    }

}
