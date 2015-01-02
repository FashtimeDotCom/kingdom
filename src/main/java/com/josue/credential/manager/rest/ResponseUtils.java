package com.josue.credential.manager.rest;

import java.net.URI;
import java.util.List;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

public class ResponseUtils {

    public static Response buildListResourceResponse(List<? extends Resource> resources,
            Response.Status status, UriInfo uriInfo, long totalCount, int limit, long offset) {

        ListResource<? extends Resource> listResource = new ListResource<>(resources);
        PaginationUtils paginationUtils = new PaginationUtils(limit, offset);
        paginationUtils.fillPagination(listResource, totalCount);
        return ResponseUtils.buildSimpleResponse(listResource, Response.Status.OK, uriInfo);
    }

    public static Response buildSimpleResponse(Resource resource,
            Response.Status status, UriInfo uriInfo) {

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
