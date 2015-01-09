/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.credential;

import com.josue.credential.manager.auth.domain.APIDomainCredential;
import com.josue.credential.manager.rest.ListResource;
import com.josue.credential.manager.rest.ResponseUtils;
import static com.josue.credential.manager.rest.ResponseUtils.CONTENT_TYPE;
import static com.josue.credential.manager.rest.ResponseUtils.DEFAULT_LIMIT;
import static com.josue.credential.manager.rest.ResponseUtils.DEFAULT_OFFSET;
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
 *
 * This class is a subresource locator, that always have to bel called from
 * DomainRest or any other ROOT endpoint
 */
@ApplicationScoped
public class CredentialRest {

    @Inject
    CredentialControl control;

    @Context
    UriInfo info;

    /**
     *
     * @param domainUuid: The parent Domain UUID
     * @param limit
     * @param offset
     * @return Response
     */
    @GET
    @Path("apikeys")
    @Produces(value = CONTENT_TYPE)
    public Response getAPICredentials(@PathParam("domainUuid") String domainUuid,
            @QueryParam("limit") @DefaultValue(DEFAULT_LIMIT) Integer limit,
            @QueryParam("offset") @DefaultValue(DEFAULT_OFFSET) Integer offset) {
        ListResource<APIDomainCredential> apiCredentials = control.getAPICredentials(domainUuid, limit, offset);
        return ResponseUtils.buildSimpleResponse(apiCredentials, Response.Status.OK, info);
    }

    @GET
    @Path("apikeys/{apikeyUuid}")
    @Produces(value = CONTENT_TYPE)
    public Response getAPICredential(@PathParam("domainUuid") String domainUuid, @PathParam("apikeyUuid") String apikeyUuid) {
        APIDomainCredential apiCredential = control.getAPICredential(domainUuid, apikeyUuid);
        return ResponseUtils.buildSimpleResponse(apiCredential, Response.Status.OK, info);
    }

}
