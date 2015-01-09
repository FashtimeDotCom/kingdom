package com.josue.credential.manager.rest;

import java.net.URI;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

//TODO check href creation
public class ResponseUtils {

    public static final String CONTENT_TYPE = "application/json;charset=utf-8";
    public static final String DEFAULT_LIMIT = "50";
    public static final String DEFAULT_OFFSET = "0";

    public static Response buildSimpleResponse(Resource resource,
            Response.Status status, UriInfo uriInfo) {

        if (resource == null) {
            return Response.status(status).build();
        }

        URI uri;

        // pagination
        if (resource instanceof ListResource<?>) {
            ListResource<Resource> res = (ListResource<Resource>) resource;
            for (Resource item : res.getItems()) {
                ResponseUtils.buildSimpleResponse(item, status, uriInfo);
            }
        } else {
            //TODO check for subresources path
            uri = uriInfo.getAbsolutePathBuilder().path(resource.getUuid()).build();
            resource.setHref(uri.getPath());
        }
        return Response.status(status).entity(resource).build();
    }

}
