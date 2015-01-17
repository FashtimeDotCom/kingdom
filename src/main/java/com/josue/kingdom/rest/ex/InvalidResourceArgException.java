package com.josue.kingdom.rest.ex;

import javax.ws.rs.core.Response;

public class InvalidResourceArgException extends RestException {

    private static final long serialVersionUID = 1L;

    public InvalidResourceArgException(Class<?> entityClass, String param, String value) {
        super(entityClass, param, String.format(
                "Invalid value '%s' for field '%s'.", value,
                param), Response.Status.BAD_REQUEST);
    }

    public InvalidResourceArgException(Class<?> entityClass, String message) {
        super(entityClass, null, message, Response.Status.BAD_REQUEST);
    }

}
