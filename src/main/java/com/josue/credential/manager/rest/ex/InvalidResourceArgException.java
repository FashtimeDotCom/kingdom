package com.josue.credential.manager.rest.ex;

import javax.ws.rs.core.Response;

public class InvalidResourceArgException extends RestException {

	private static final long serialVersionUID = 1L;

	public InvalidResourceArgException(Class<?> entityClass, String param, String value) {
		super(entityClass, param, String.format(
				"Invalid value '%s' for parameter '%s'.", value,
				param), Response.Status.BAD_REQUEST);
	}

}
