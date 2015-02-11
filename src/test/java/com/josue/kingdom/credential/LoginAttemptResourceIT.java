/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.credential;

import com.josue.kingdom.credential.entity.LoginAttempt;
import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.credential.entity.SimpleLogin;
import com.josue.kingdom.rest.ListResource;
import com.josue.kingdom.testutils.ArquillianTestBase;
import com.josue.kingdom.testutils.RestHelper;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.Response;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Josue
 */
@RunAsClient
@RunWith(Arquillian.class)
public class LoginAttemptResourceIT {

    private static final String LOGIN_ATTEMPT = "login-attempts";

    @Deployment
    @TargetsContainer("wildfly-managed")
    public static WebArchive createDeployment() {
        return ArquillianTestBase.createDefaultDeployment();
    }

    @Test
    public void testLogin() throws Exception {
        String defaultManagerUsername = "manager1";
        //manager1:pass123
        String initialBase64DataManagerCredentials = "bWFuYWdlcjE6cGFzczEyMw==";

        SimpleLogin simpleLogin = new SimpleLogin();
        simpleLogin.setType(SimpleLogin.LoginType.BASIC);
        simpleLogin.setData(initialBase64DataManagerCredentials);

        ClientResponse response = RestHelper.doPostRequest(simpleLogin, LOGIN_ATTEMPT);
        RestHelper.assertStatusCode(Response.Status.OK.getStatusCode(), response);

        Manager foundManager = response.getEntity(new GenericType<Manager>() {
        });
        assertEquals(defaultManagerUsername, foundManager.getUsername());
    }

    @Test
    public void testGetLoginAttempts() throws Exception {
        String defaulLoginAttempUsernamee = "anymanagerlogin123";

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("login", defaulLoginAttempUsernamee);

        ClientResponse response = RestHelper.doGetRequest(queryParams, LOGIN_ATTEMPT);
        RestHelper.assertStatusCode(Response.Status.OK.getStatusCode(), response);

        ListResource<LoginAttempt> foundAttempts = response.getEntity(new GenericType<ListResource<LoginAttempt>>() {
        });
        assertEquals(1, foundAttempts.getItems().size());
        assertEquals(defaulLoginAttempUsernamee, foundAttempts.getItems().get(0).getLogin());
    }

}
