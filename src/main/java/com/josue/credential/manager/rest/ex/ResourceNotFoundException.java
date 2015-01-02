package com.josue.credential.manager.rest.ex;

import javax.ws.rs.core.Response;

public class ResourceNotFoundException extends RestException {

	private static final long serialVersionUID = 1L;

	public ResourceNotFoundException(Class<?> entityClass, String entityId) {
		super(entityClass, entityId, String.format(
				"Resource '%s' not found, id '%s'.",
				entityClass.getSimpleName(), entityId),
				Response.Status.NOT_FOUND);

	}

}
