package com.josue.kingdom.rest.ex;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class RestException extends Exception {

    private static final long serialVersionUID = 1L;

    private final Class<?> entityClass;
    private final String entityId;
    private final String message;

    private final Response.Status status;
    private final int statuCode;

    public RestException(Class<?> entityClass, String entityId, String message,
            Status status) {
        this.entityClass = entityClass;
        this.entityId = entityId;
        this.message = message;
        this.status = status;
        this.statuCode = status.getStatusCode();
    }

    public Class getEntityClass() {
        return entityClass;
    }

    public String getEntityId() {
        return entityId;
    }

    public Response.Status getStatus() {
        return status;
    }

    public int getStatuCode() {
        return statuCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}
