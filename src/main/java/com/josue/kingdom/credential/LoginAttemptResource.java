/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.credential;

import com.josue.kingdom.credential.entity.LoginAttempt;
import com.josue.kingdom.credential.entity.SimpleLogin;
import com.josue.kingdom.rest.ListResource;
import com.josue.kingdom.rest.ResponseUtils;
import static com.josue.kingdom.rest.ResponseUtils.CONTENT_TYPE;
import static com.josue.kingdom.rest.ResponseUtils.DEFAULT_LIMIT;
import static com.josue.kingdom.rest.ResponseUtils.DEFAULT_OFFSET;
import com.josue.kingdom.rest.ex.InvalidResourceArgException;
import com.josue.kingdom.rest.ex.RestException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author Josue
 */
@ApplicationScoped

@Path("login-attempts")
public class LoginAttemptResource {

    @Context
    UriInfo info;

    @Inject
    CredentialControl control;

    @POST
    @Produces(value = CONTENT_TYPE)
    public Response login(SimpleLogin simpleLogin) throws RestException {
        return ResponseUtils.buildSimpleResponse(control.login(simpleLogin), Response.Status.OK, info);
    }

    @GET
    @Produces(value = CONTENT_TYPE)
    public Response getLoginAttempts(@QueryParam("login") String login,
            @QueryParam("status") String status,
            @QueryParam("startDate") String startDate,
            @QueryParam("endDate") String endDate,
            @QueryParam("limit") @DefaultValue(DEFAULT_LIMIT) Integer limit,
            @QueryParam("offset") @DefaultValue(DEFAULT_OFFSET) Integer offset) throws RestException {

        Date sDate = parseDateFromString("startDate", startDate);
        Date eDate = parseDateFromString("endDate", endDate);
        ListResource<LoginAttempt> loginAttempts = control.getLoginAttempts(login, status, sDate, eDate, limit, offset);
        return ResponseUtils.buildSimpleResponse(loginAttempts, Response.Status.OK, info);
    }

    //Using specific for this class... if any generalization is needed, remove 'fieldName' param
    private Date parseDateFromString(String fieldName, String dateString) throws InvalidResourceArgException {
        if (dateString == null) {
            return null;
        }

        String compactFormat = "yyyy-MM-dd";
        String extendedFormat = "yyyy-MM-dd hh:mm";
        try {
            if (dateString.matches("^\\d{4}-\\d{2}-\\d{2}")) {
                return new SimpleDateFormat(compactFormat).parse(dateString);
            } else if (dateString.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}")) {
                return new SimpleDateFormat(extendedFormat).parse(dateString);
            } else {
                throw new InvalidResourceArgException(LoginAttempt.class, "Invalid date format for field " + fieldName + " with value '" + dateString + "'. Valid formats are '" + compactFormat + "' and '" + extendedFormat + "'");
            }
        } catch (ParseException ex) {
            throw new InvalidResourceArgException(LoginAttempt.class, "Invalid date format for field " + fieldName + " with value '" + dateString + "'. Valid formats are '" + compactFormat + "' and '" + extendedFormat + "'");
        }
    }
}
