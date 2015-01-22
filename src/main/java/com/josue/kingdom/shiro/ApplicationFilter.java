/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.shiro;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

/**
 *
 * @author Josue
 */
public class ApplicationFilter extends BasicHttpAuthenticationFilter {

    //TODO this should be able to be changed on shiro.ini
    private static final String APP_ID = "app_id";

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        boolean loggedIn = executeLogin(request, response);
        return loggedIn;

//        if (apiToken.getPrincipal() == null) {
//            return validToken;
//        } else if (apiToken.getPrincipal().equals("123")) {
//            validToken = true;
//        }
    }

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        String appId = httpRequest.getHeader(APP_ID);

        AuthenticationToken authToken = super.createToken(request, response);
        KingdomAuthToken apiToken = new KingdomAuthToken(authToken.getPrincipal(), authToken.getCredentials(), appId);
        return apiToken;
    }

    //Custom failure response
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {

        //TODO handle/map specific exceptions
        HttpServletResponse httpResponse = WebUtils.toHttp(response);
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        try {
            //TODO add as json / xml content.. or... find a generic way to return everything
            httpResponse.getWriter().write("INVALID TOKEN: " + e.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(ApplicationFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        //TODO handle ???
        return true;
    }

}
