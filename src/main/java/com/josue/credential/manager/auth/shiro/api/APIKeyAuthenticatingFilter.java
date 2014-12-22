/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.auth.shiro.api;

import com.josue.credential.manager.auth.APICredential;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;

/**
 *
 * @author Josue
 */
public class APIKeyAuthenticatingFilter extends AuthenticatingFilter {

    //TODO this should be able to be changed on shiro.ini
    private static final String API_KEY = "ApiKey";

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
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        String token = httpRequest.getHeader(API_KEY);

        APICredential apiToken = new APICredential(token);
        return apiToken;
    }

    //Custom failure response
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {

        PasswordMatcher pm;
        SimpleCredentialsMatcher scm;
        //TODO handle/map specific exceptions
        HttpServletResponse httpResponse = WebUtils.toHttp(response);
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        try {
            //TODO add as json / xml content.. or... find a generic way to return everything
            httpResponse.getWriter().write("INVALID TOKEN: " + e.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(APIKeyAuthenticatingFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        //TODO handle ???
        return true;
    }

}
