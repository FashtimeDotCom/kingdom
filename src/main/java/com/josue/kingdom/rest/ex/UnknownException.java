package com.josue.kingdom.rest.ex;

import javax.ws.rs.core.Response;

public class UnknownException extends RestException {

	private static final long serialVersionUID = 1L;

	public UnknownException(Class<?> entityClass, String entityId) {
		super(entityClass, entityId, String.format(
				"Unknown exception for entity '%s', id '%s'.",
				entityClass.getSimpleName(), entityId),
				Response.Status.INTERNAL_SERVER_ERROR);
	}
}
