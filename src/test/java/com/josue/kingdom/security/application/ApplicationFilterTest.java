/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.security.application;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import org.apache.shiro.authc.AuthenticationToken;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author Josue
 */
public class ApplicationFilterTest {

    ApplicationFilter filter = new ApplicationFilter();

    @Test
    public void testCreateToken() {

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);

        String appKey = "application-key";
        String appSecret = "application-secret";
        String combinedValues = appKey + ":" + appSecret;
        String applicationCredential = "Basic " + DatatypeConverter.printBase64Binary(combinedValues.getBytes());

        String managerEmail = "manager1@email.com";
        String managerPassword = "manager1-psw";
        String kingdomHeader = managerEmail + ":" + managerPassword;

        String base64ManagerCredential = DatatypeConverter.printBase64Binary(kingdomHeader.getBytes());

        when(req.getHeader(ApplicationFilter.KINGDOM_HEADER)).thenReturn(base64ManagerCredential);
        when(req.getHeader("Authorization")).thenReturn(applicationCredential);

        AuthenticationToken createToken = filter.createToken(req, resp);
        assertTrue(createToken instanceof ApplicationToken);
        ApplicationToken appToken = (ApplicationToken) createToken;

        assertEquals(appKey, appToken.getPrincipal());
        assertEquals(appSecret, new String((char[]) appToken.getCredentials()));
        assertEquals(managerEmail, appToken.getManagerToken().getPrincipal());
        assertEquals(managerPassword, new String((char[]) appToken.getManagerToken().getCredentials()));

    }

}
