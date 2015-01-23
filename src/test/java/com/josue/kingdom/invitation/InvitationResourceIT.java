/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.invitation;

import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.domain.entity.Domain;
import com.josue.kingdom.domain.entity.DomainPermission;
import com.josue.kingdom.invitation.entity.Invitation;
import com.josue.kingdom.rest.ListResource;
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
public class InvitationResourceIT {

    private static final String INVITATIONS = "invitations";

    @Deployment
    @TargetsContainer("wildfly-managed")
    public static WebArchive createDeployment() {
        return ArquillianTestBase.createDefaultDeployment();
    }

    @Test
    public void testGetInvitations() {
        final String testInitialInvValue = "1811009b-dc81-4686-9297-d5d357049858";

        ClientResponse getDomainsResponse = RestHelper.doGetRequest(INVITATIONS);
        RestHelper.assertStatusCode(Response.Status.OK.getStatusCode(), getDomainsResponse);
        ListResource<Invitation> invitations = getDomainsResponse.getEntity(new GenericType<ListResource<Invitation>>() {
        });
        assertEquals(1, invitations.getItems().size());
        assertEquals(testInitialInvValue, invitations.getItems().get(0).getUuid());

    }

    @Test
    public void testGetInvitation() {
        final String testInitialInvValue = "1811009b-dc81-4686-9297-d5d357049858";

        ClientResponse getDomainsResponse = RestHelper.doGetRequest(INVITATIONS, testInitialInvValue);
        RestHelper.assertStatusCode(Response.Status.OK.getStatusCode(), getDomainsResponse);
        Invitation invitation = getDomainsResponse.getEntity(new GenericType<Invitation>() {
        });
        assertNotNull(invitation);
        assertEquals(testInitialInvValue, invitation.getUuid());
    }

    @Test
    public void testCreateInvitation() {

        Domain testCreatedDomain = new Domain();
        testCreatedDomain.setUuid("70f4b4b0-18d2-4707-824b-b30af193d99b");

        Manager testCreatedAuthorManager = new Manager();
        testCreatedAuthorManager.setUuid("926caa10-43a4-11e4-916c-0800200c9a66");

        DomainPermission testCreatedPermission = new DomainPermission();
        testCreatedPermission.setUuid("d1486e7e-611c-47eb-bba1-28e6d4adca95");

        Invitation invitation = new Invitation();
        invitation.setAuthorManager(testCreatedAuthorManager);
        invitation.setDomain(testCreatedDomain);
        invitation.setPermission(testCreatedPermission);

        Manager manager = new Manager();
        manager.setEmail("test@email.com");
        invitation.setTargetManager(manager);

        ClientResponse getDomainsResponse = RestHelper.doPostRequest(invitation, INVITATIONS);
        RestHelper.assertStatusCode(Response.Status.CREATED.getStatusCode(), getDomainsResponse);
        Invitation foundInvitation = getDomainsResponse.getEntity(new GenericType<Invitation>() {
        });
        assertNotNull(foundInvitation);

    }

}
