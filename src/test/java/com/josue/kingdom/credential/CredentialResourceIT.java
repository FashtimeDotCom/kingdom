/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.credential;

import com.josue.kingdom.credential.entity.APICredential;
import com.josue.kingdom.credential.entity.Credential;
import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.testutils.ArquillianTestBase;
import com.josue.kingdom.testutils.RestHelper;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import javax.ws.rs.core.Response;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Josue
 */
@RunAsClient
@RunWith(Arquillian.class)
public class CredentialResourceIT {

    private static final String CREDENTIALS = "credentials";
    private static final String CURRENT = "current";
    private static final String PASSWORD_RESET = "password-reset";
    private static final String LOGIN_RECOVER = "login-recover";

    @Deployment
    @TargetsContainer("wildfly-managed")
    public static WebArchive createDeployment() {
        return ArquillianTestBase.createDefaultDeployment();
    }

    @Test
    public void testGetCurrentCredential() {
        String testInitialAPICredentialUuid = "7ddb6165-70d2-45c6-9e0a-f4b80b070f24";

        ClientResponse response = RestHelper.doGetRequest(CREDENTIALS, CURRENT);
        RestHelper.assertStatusCode(Response.Status.OK.getStatusCode(), response);

        Credential foundCredential = response.getEntity(new GenericType<APICredential>() {
        });
        assertNotNull(foundCredential);
        assertEquals(testInitialAPICredentialUuid, foundCredential.getUuid());
        //TODO credential (user, psw) should not be returned, test it
    }

    @Test
    public void testGetAccount() {

        String testInitialEmail = "josue.eduardo206@gmail.com";
        String testInitialLogin = "josueeduardo";

        ClientResponse response = RestHelper.doGetRequest(CREDENTIALS, testInitialLogin);
        RestHelper.assertStatusCode(Response.Status.OK.getStatusCode(), response);

        Manager foundManager = response.getEntity(new GenericType<Manager>() {
        });
        assertNotNull(foundManager);
        assertEquals(testInitialEmail, foundManager.getEmail());
    }

    @Test
    public void testPasswordReset() {
        //TODO test-me
    }

    @Test
    public void testLoginRecover() {
        //TODO test-me
    }

    @Test
    public void testCreateAccount() {
        //TODO test-me
    }

}
