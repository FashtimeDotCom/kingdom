/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.credential;

import com.josue.credential.manager.auth.domain.APIDomainCredential;
import com.josue.credential.manager.rest.ListResource;
import com.josue.credential.manager.rest.ResponseUtils;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author Josue
 */
@Path("credentials")
@ApplicationScoped
public class CredentialRest {

    @Inject
    CredentialControl control;

    @Context
    UriInfo info;

    @GET
    @Path("{domaunUuid}/api-credentials")
    public Response getApiCredentialsForDomain(@PathParam("domaunUuid") String domaunUuid) {
        ListResource<APIDomainCredential> apiCredentials = control.getAPICredentials(domaunUuid, Integer.SIZE, Integer.SIZE);
        return ResponseUtils.buildSimpleResponse(apiCredentials, Response.Status.OK, info);
    }

}
