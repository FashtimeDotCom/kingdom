/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.invitation;

import com.josue.kingdom.invitation.entity.Invitation;
import com.josue.kingdom.rest.ListResource;
import com.josue.kingdom.rest.ResponseUtils;
import static com.josue.kingdom.rest.ResponseUtils.CONTENT_TYPE;
import static com.josue.kingdom.rest.ResponseUtils.DEFAULT_LIMIT;
import static com.josue.kingdom.rest.ResponseUtils.DEFAULT_OFFSET;
import com.josue.kingdom.rest.ex.ResourceNotFoundException;
import com.josue.kingdom.rest.ex.RestException;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
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
@Path("invitations")
public class InvitationResource {

    @Context
    UriInfo info;

    @Inject
    InvitationControl control;

    @GET
    @Produces(value = CONTENT_TYPE)
    public Response getInvitations(@QueryParam("limit") @DefaultValue(DEFAULT_LIMIT) Integer limit,
            @QueryParam("offset") @DefaultValue(DEFAULT_OFFSET) Integer offset) {
        ListResource<Invitation> invitations = control.getInvitations(limit, offset);
        return ResponseUtils.buildSimpleResponse(invitations, Response.Status.OK, info);
    }

    @GET
    @Path("{uuid}")
    @Produces(value = CONTENT_TYPE)
    public Response getInvitation(@PathParam(("uuid")) String uuid) {
        Invitation invitation = control.getInvitation(uuid);
        return ResponseUtils.buildSimpleResponse(invitation, Response.Status.OK, info);
    }

    @POST
    @Produces(value = CONTENT_TYPE)
    @Consumes(value = CONTENT_TYPE)
    public Response createInvitation(Invitation invitation) throws RestException {
        Invitation createdInvitation = control.createInvitation(invitation);
        return ResponseUtils.buildSimpleResponse(createdInvitation, Response.Status.OK, info);
    }

    //TODO s it useful ?
    @PUT
    @Path("{uuid}")
    @Consumes(value = CONTENT_TYPE)
    public Response updateInvitation(@PathParam(("uuid")) String uuid, Invitation invitation) throws ResourceNotFoundException {
        Invitation updatedInvitation = control.updateInvitation(uuid, invitation);
        return ResponseUtils.buildSimpleResponse(updatedInvitation, Response.Status.OK, info);
    }
}
