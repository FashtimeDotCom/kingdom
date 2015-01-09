/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.credential;

import com.josue.credential.manager.auth.domain.APIDomainCredential;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author Josue
 */
@Path("credentials")
@ApplicationScoped
public class CredentialRest {

    @Inject
    CredentialControl control;

    @GET
    @Path("{domaunUuid}/api-credentials")
    public Response getApiCredentialsForDomain(@PathParam("domaunUuid") String domaunUuid) {
        List<APIDomainCredential> apiCredentials = control.getApiCredentialsByManagerDomain(domaunUuid);
        return null;
    }

}
