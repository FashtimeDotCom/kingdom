package com.josue.credential.manager.rest.ex;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/*
 * This class handle all exceptions for the app, should be added to the resources in ApplicationConfig class
 */
@Provider
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

        // TODO remove before prod env
        return Response
                .status(Status.BAD_REQUEST)
                .entity(new ExceptionBean(1, Response.Status.NOT_FOUND,
                                exception.getMessage())).build();
    }
}
