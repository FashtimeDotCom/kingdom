package com.josue.kingdom.rest.ex;

import javax.ws.rs.core.Response;

public class ResourceNotFoundException extends RestException {

    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(Class<?> entityClass, String entityId) {
        super(entityClass, entityId, String.format(
                "Resource '%s' not found, id '%s'.",
                entityClass.getSimpleName(), entityId),
                Response.Status.NOT_FOUND);

    }

    public ResourceNotFoundException(Class<?> entityClass, String field, Object value) {
        super(entityClass, null, String.format(
                "Resource '%s' not found for field '%s', value %s.",
                entityClass.getSimpleName(), field, value),
                Response.Status.NOT_FOUND);

    }

    public ResourceNotFoundException(Class<?> entityClass, String field, Object value, String message) {
        super(entityClass, null, message,
                Response.Status.NOT_FOUND);

    }

}
