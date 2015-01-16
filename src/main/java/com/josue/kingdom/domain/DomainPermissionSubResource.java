/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.domain;

import com.josue.kingdom.domain.entity.DomainPermission;
import com.josue.kingdom.rest.ListResource;
import com.josue.kingdom.rest.ResponseUtils;
import static com.josue.kingdom.rest.ResponseUtils.CONTENT_TYPE;
import com.josue.kingdom.rest.ex.RestException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
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
@ApplicationScoped
//TODO rename this class
//This is a subresource locator class... can be accessed throught /domains/123/permissions
public class DomainPermissionSubResource {

    @Context
    UriInfo info;

    @Inject
    DomainControl control;

    @GET
    @Produces(value = CONTENT_TYPE)
    //TODO add limit offset ? is it needed ?
    public Response getGrantedDomainPermissions(@PathParam("domainUuid") String domainUuid) {
        ListResource<DomainPermission> domainPermissions = control.getDomainPermissions(domainUuid);
        return ResponseUtils.buildSimpleResponse(domainPermissions, Response.Status.OK, info);
    }

    @POST
    @Produces(value = CONTENT_TYPE)
    public Response createDomainPermission(@PathParam("domainUuid") String domainUuid, DomainPermission domainPermission) throws RestException {
        DomainPermission createdDomainPermission = control.createDomainPermission(domainUuid, domainPermission);
        return ResponseUtils.buildSimpleResponse(createdDomainPermission, Response.Status.OK, info);
    }

    @PUT
    @Path("{permissionUuid}")
    @Produces(value = CONTENT_TYPE)
    public Response updateDomainPermission(@PathParam("domainUuid") String domainUuid,
            @PathParam("permissionUuid") String permissionUuid, DomainPermission domainPermission) throws RestException {
        DomainPermission updatedDomainPermission = control.updateDomainPermission(domainUuid, permissionUuid, domainPermission);
        return ResponseUtils.buildSimpleResponse(updatedDomainPermission, Response.Status.OK, info);
    }

    @DELETE
    @Path("{permissionUuid}")
    @Produces(value = CONTENT_TYPE)
    public Response deleteDomainPermission(@PathParam("domainUuid") String domainUuid,
            @PathParam("permissionUuid") String permissionUuid,
            @QueryParam("replacement") String replacementRoleUuid,
            DomainPermission domainPermission) throws RestException {

        control.deleteDomainPermission(domainUuid, permissionUuid, replacementRoleUuid);
        return Response.noContent().build();
    }

}
