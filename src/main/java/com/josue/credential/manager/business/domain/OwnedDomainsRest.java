/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.domain;

import com.josue.credential.manager.RestBoundary;
import com.josue.credential.manager.auth.domain.Domain;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 *
 * @author Josue
 */
@Path("owned-domains")
@ApplicationScoped
public class OwnedDomainsRest extends RestBoundary<Domain> {

    @Inject
    DomainControl control;

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
