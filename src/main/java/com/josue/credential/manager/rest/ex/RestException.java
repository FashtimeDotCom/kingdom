package com.josue.credential.manager.rest.ex;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class RestException extends Exception {

    private static final long serialVersionUID = 1L;

    private Class<?> entityClass;
    private String entityId;
    private String message;

    private Response.Status status;
    private int statuCode;

    public RestException(Class<?> entityClass, String entityId, String message,
            Status status) {
        this.entityClass = entityClass;
        this.entityId = entityId;
        this.message = message;
        this.status = status;
        this.statuCode = status.getStatusCode();
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public Response.Status getStatus() {
        return status;
    }

    public void setStatus(Response.Status status) {
        this.status = status;
    }

    public int getStatuCode() {
        return statuCode;
    }

    public void setStatuCode(int statuCode) {
        this.statuCode = statuCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}
