/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.credential;

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
    public void testGetAccount() {

        String testInitialEmail = "manager1@gmail.com";
        String testInitialLogin = "manager1";

        ClientResponse response = RestHelper.doGetRequest(CREDENTIALS, testInitialLogin);
        RestHelper.assertStatusCode(Response.Status.OK.getStatusCode(), response);

        Manager foundManager = response.getEntity(new GenericType<Manager>() {
        });
        assertNotNull(foundManager);
        assertEquals(testInitialEmail, foundManager.getEmail());
    }

    @Test
    public void testGetCurrentManager() {
        String testInitialManagerUuid = "cdbd57b8-3dc2-4370-b8a6-65e674a430d6";

        ClientResponse response = RestHelper.doGetRequest(CREDENTIALS, CURRENT);
        RestHelper.assertStatusCode(Response.Status.OK.getStatusCode(), response);

        Manager foundManager = response.getEntity(new GenericType<Manager>() {
        });
        assertNotNull(foundManager);
        assertEquals(testInitialManagerUuid, foundManager.getUuid());
        //TODO credential (user, psw) should not be returned, test it
    }

    //Note, this test should run with specific Manager, do not affect the other tests
    @Test
    public void testPasswordReset() {
        String testInitialUsername = "manager2";//secundary manager
        ClientResponse response = RestHelper.doGetRequest(CREDENTIALS, testInitialUsername, PASSWORD_RESET);
        RestHelper.assertStatusCode(Response.Status.OK.getStatusCode(), response);
    }

    @Test
    public void testLoginRecover() {
        String testInitialEmail = "manager1@gmail.com";
        ClientResponse response = RestHelper.doGetRequest(CREDENTIALS, testInitialEmail, LOGIN_RECOVER);
        RestHelper.assertStatusCode(Response.Status.OK.getStatusCode(), response);
    }

    @Test
    public void testCreateManager() {
        String testInitialInvToken = "93f52dc9-496d-46bc-a2f8-ba7394fca966";
        String tstInitialInvEmail = "manager3@gmail.com";

        String username = "manager1-username";
        String password = "psw123";

        Manager manager = new Manager();
        //email is not needed
        manager.setFirstName("new manager name");
        manager.setLastName("a lastname");
        manager.setUsername(username);
        manager.setPassword(password);

        ClientResponse response = RestHelper.doPostRequest(manager, CREDENTIALS, testInitialInvToken);
        RestHelper.assertStatusCode(Response.Status.CREATED.getStatusCode(), response);
        Manager foundManager = response.getEntity(new GenericType<Manager>() {
        });
        assertNotNull(foundManager);
        assertEquals(username, foundManager.getUsername());
        assertEquals(tstInitialInvEmail, foundManager.getEmail());

    }

}
