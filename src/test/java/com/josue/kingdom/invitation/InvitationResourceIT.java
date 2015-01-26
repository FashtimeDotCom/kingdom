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
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Response;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
        final String testInitialInvValue = "3e68d3e1-d0fd-48af-81ca-1cd10e1a92f7";

        ClientResponse getDomainsResponse = RestHelper.doGetRequest(INVITATIONS);
        RestHelper.assertStatusCode(Response.Status.OK.getStatusCode(), getDomainsResponse);
        ListResource<Invitation> invitations = getDomainsResponse.getEntity(new GenericType<ListResource<Invitation>>() {
        });
        assertTrue(invitations.getItems().size() >= 1);
        List<String> invUuids = new ArrayList<>();
        for (Invitation inv : invitations.getItems()) {
            invUuids.add(inv.getUuid());
        }
        assertTrue(invUuids.contains(testInitialInvValue));

    }

    @Test
    public void testGetInvitation() {
        final String testInitialInvValue = "3e68d3e1-d0fd-48af-81ca-1cd10e1a92f7";

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
        testCreatedDomain.setUuid("4cf10908-9992-4ce5-8fad-7a8ea1a96b8a");

        Manager testCreatedAuthorManager = new Manager();
        testCreatedAuthorManager.setUuid("cdbd57b8-3dc2-4370-b8a6-65e674a430d6");

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
