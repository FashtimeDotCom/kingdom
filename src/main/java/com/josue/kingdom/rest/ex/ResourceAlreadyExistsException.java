package com.josue.kingdom.rest.ex;

import javax.ws.rs.core.Response;

public class ResourceAlreadyExistsException extends RestException {

    private static final long serialVersionUID = 1L;

    public ResourceAlreadyExistsException(Class<?> entityClass, String entityId) {
        super(entityClass, entityId, String.format(
                "Resource '%s' already exists, id '%s'.",
                entityClass.getSimpleName(), entityId),
                Response.Status.CONFLICT);
    }

    public ResourceAlreadyExistsException(Class<?> entityClass, String field, Object value) {
        super(entityClass, null, String.format(
                "Resource '%s' already exists, field '%s', value '%s'.",
                entityClass.getSimpleName(), field, value),
                Response.Status.CONFLICT);
    }
}
