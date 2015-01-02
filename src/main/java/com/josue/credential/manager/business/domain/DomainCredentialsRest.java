/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.domain;

import com.josue.credential.manager.RestBoundary;
import com.josue.credential.manager.auth.domain.Domain;
import com.josue.credential.manager.auth.domain.ManagerDomainCredential;
import com.josue.credential.manager.rest.ResponseUtils;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author Josue
 */
@Path("domain-credentials")
@ApplicationScoped
public class DomainCredentialsRest extends RestBoundary<Domain> {

    @Inject
    DomainControl control;

    @Context
    UriInfo info;

    @Override
    public Response list(Integer limit, Long offset) {
        List<ManagerDomainCredential> foundDomains = control.getJoinedDomains();

        long totalCount = control.countDomainCredentials();
        return ResponseUtils.buildListResourceResponse(foundDomains, Response.Status.OK, info, totalCount, limit, offset);
    }

    @Override
    public Response getByUuid(String uuid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Response create(Domain domain) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Response update(String uuid, Domain domain) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
