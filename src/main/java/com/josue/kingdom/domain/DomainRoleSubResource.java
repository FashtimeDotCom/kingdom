/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.domain;

import com.josue.kingdom.account.Current;
import com.josue.kingdom.credential.entity.Credential;
import com.josue.kingdom.domain.entity.DomainRole;
import static com.josue.kingdom.rest.ResponseUtils.CONTENT_TYPE;
import com.josue.kingdom.shiro.AccessLevelPermission;
import java.util.Iterator;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.apache.shiro.SecurityUtils;

/**
 *
 * @author Josue
 */
@ApplicationScoped
//TODO rename this class
//This is a subresource locator class... can be accessed throught /domains/123/roles
public class DomainRoleSubResource {

    @Inject
    DomainRepository repository;

    @Inject
    @Current
    Credential credential;

    @GET
    @Produces(value = CONTENT_TYPE)
    //TODO add limit offset ? is it needed ?
    public Response getDomainRoles(@PathParam("domainUuid") String domainUuid) {
        //TODO change this... each domain should have its own roles
        List<DomainRole> roles = repository.findAll(DomainRole.class);
        for (Iterator<DomainRole> iterator = roles.iterator(); iterator.hasNext();) {
            DomainRole role = iterator.next();
            if (!SecurityUtils.getSubject().isPermitted(new AccessLevelPermission(domainUuid, role))) {
                iterator.remove();
            }
        }
        return Response.status(Response.Status.OK).entity(roles).build();
    }

}
