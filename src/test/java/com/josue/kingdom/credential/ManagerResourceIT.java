/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.credential;

import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.credential.entity.PasswordChangeEvent;
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
public class ManagerResourceIT {

    private static final String DEFAULT_KINGDOM_HEADER = "Kingdom";

    private static final String MANAGERS = "managers";
    private static final String CURRENT = "current";
    private static final String PASSWORD_RESET_REQUEST = "password-request";
    private static final String MANAGER_PASSWORD = "password";
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

        ClientResponse response = RestHelper.doGetRequest(MANAGERS, testInitialLogin);
        RestHelper.assertStatusCode(Response.Status.OK.getStatusCode(), response);

        Manager foundManager = response.getEntity(new GenericType<Manager>() {
        });
        assertNotNull(foundManager);
        assertEquals(testInitialEmail, foundManager.getEmail());
    }

    @Test
    public void testGetCurrentManager() {
        String testInitialManagerUuid = "zb1XuD3CQ3C4pmXmdKQw1g";

        ClientResponse response = RestHelper.doGetRequest(MANAGERS, CURRENT);
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
        ClientResponse response = RestHelper.doPostRequest(null, MANAGERS, testInitialUsername, PASSWORD_RESET_REQUEST);
        RestHelper.assertStatusCode(Response.Status.OK.getStatusCode(), response);
    }

    @Test
    public void testLoginRecover() {
        String testInitialEmail = "manager1@gmail.com";
        ClientResponse response = RestHelper.doPostRequest(null, MANAGERS, testInitialEmail, LOGIN_RECOVER);
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

        ClientResponse response = RestHelper.doPostRequest(manager, MANAGERS, testInitialInvToken);
        RestHelper.assertStatusCode(Response.Status.CREATED.getStatusCode(), response);
        Manager foundManager = response.getEntity(new GenericType<Manager>() {
        });
        assertNotNull(foundManager);
        assertEquals(username, foundManager.getUsername());
        assertEquals(tstInitialInvEmail, foundManager.getEmail());

    }

    @Test
    public void testUpdateManagerPassword() throws Exception {
        String defaultManagerUsername = "manager1";
        String initialTestDataPswToken = "9RtbC5489Er6OLmjXskCLw";
        PasswordChangeEvent event = new PasswordChangeEvent();
        event.setNewPassword("a-new-password");
        event.setToken(initialTestDataPswToken);

        ClientResponse response = RestHelper.doPutRequest(event, MANAGERS, defaultManagerUsername, MANAGER_PASSWORD);
        RestHelper.assertStatusCode(Response.Status.OK.getStatusCode(), response);
    }

}
