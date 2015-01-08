package com.josue.credential.manager.rest.ex;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/*
 * This class handle all exceptions for the app, should be added to the resources in ApplicationConfig class
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class RestExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {

        if (exception instanceof RestException) {
            RestException restException = (RestException) exception;

            // Set mapped exception bean
            ExceptionBean exceptionBean = new ExceptionBean(
                    restException.getStatuCode(),
                    restException.getStatus(),
                    restException.getMessage());

            return Response.status(restException.getStatus())
                    .entity(exceptionBean).build();

        }

        return Response
                .status(Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ExceptionBean(1, Response.Status.INTERNAL_SERVER_ERROR,
                                exception.getMessage())).build();
    }
}
