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
}
