package com.josue.kingdom.rest.ex;

import javax.ws.rs.core.Response;

public class ExceptionBean {

	private Response.Status status;
	private int code;
	private String message;

	public ExceptionBean(int code, Response.Status status, String message) {
		this.status = status;
		this.message = message;
		this.code = code;
	}

	public Response.Status getStatus() {
		return status;
	}

	public void setStatus(Response.Status status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}
