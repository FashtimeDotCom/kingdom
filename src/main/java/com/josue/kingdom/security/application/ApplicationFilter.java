/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.security.application;

import com.josue.kingdom.security.manager.ManagerToken;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
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
    public static final String KINGDOM_HEADER = "Kingdom";
    private static final String CREDENTIAL_SEPARATOR = ":";

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
        //TODO improve.... check null, etc

        ManagerToken managerToken = null;
        String appCredentials = httpRequest.getHeader(KINGDOM_HEADER);
        if (appCredentials != null) {
            String parsedHeader = new String(DatatypeConverter.parseBase64Binary(appCredentials));
            String[] split = parsedHeader.split(CREDENTIAL_SEPARATOR);
            if (split.length == 2) {
                String managerLogin = parsedHeader.split(CREDENTIAL_SEPARATOR)[0];
                char[] managerPassword = parsedHeader.split(CREDENTIAL_SEPARATOR)[1].toCharArray();
                managerToken = new ManagerToken(managerLogin, managerPassword);
            }
        }

        AuthenticationToken authToken = super.createToken(request, response);
        //TODO validate if its an email or a username
        ApplicationToken apiToken = new ApplicationToken(authToken.getPrincipal(), authToken.getCredentials(), managerToken);
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
            httpResponse.getWriter().write("INVALID TOKEN (TODO): " + e.getMessage());
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
