/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.domain;

import com.josue.kingdom.domain.entity.Domain;
import com.josue.kingdom.domain.entity.ManagerDomainCredential;
import com.josue.kingdom.rest.ListResource;
import com.josue.kingdom.testutils.ArquillianTestBase;
import com.josue.kingdom.testutils.InstanceHelper;
import com.josue.kingdom.testutils.Logged;
import com.josue.kingdom.testutils.RestHelper;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import java.io.IOException;
import java.util.Date;
import static org.hamcrest.CoreMatchers.not;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Josue
 */
@Logged
@RunAsClient
@RunWith(Arquillian.class)
public class DomainRestIT {

    private static final String DOMAINS = "/domains";
    private static final String OWNED_DOMAINS = "/owned";
    private static final String JOINED_DOMAINS = "/joined";

    @Deployment
    @TargetsContainer("wildfly-managed")
    public static WebArchive createDeployment() {
        return ArquillianTestBase.createDefaultDeployment();
    }

    @Test
    public void testListJoinedDomains() throws IOException {
        ClientResponse response = RestHelper.doGetRequest(DOMAINS, JOINED_DOMAINS);

        assertEquals(200, response.getStatus());

        ListResource<ManagerDomainCredential> manDomainCreds = response.getEntity(new GenericType<ListResource<ManagerDomainCredential>>() {
        });
        assertEquals(2, manDomainCreds.getItems().size());
        for (ManagerDomainCredential mdc : manDomainCreds.getItems()) {
            //Credential should not be returned on rest calls
            assertNull(mdc.getCredential());
        }

    }

    @Test
    public void testListOwnedDomains() throws IOException {
        ClientResponse response = RestHelper.doGetRequest(DOMAINS, OWNED_DOMAINS);

        assertEquals(200, response.getStatus());

        ListResource<Domain> domains = response.getEntity(new GenericType<ListResource<Domain>>() {
        });
        //TODO should generate all Resources stack for testing ?
        assertTrue(domains.getItems().size() >= 1);
    }

    @Test
    public void testCreate() {
        Domain domain = InstanceHelper.createDomain(null);
        ClientResponse response = RestHelper.doPostRequest(domain, DOMAINS);
        assertEquals(201, response.getStatus());

        Domain createdDomain = response.getEntity(new GenericType<Domain>() {
        });
        assertNotNull(createdDomain);
        assertNotNull(createdDomain.getUuid());
        assertEquals(domain.getName(), createdDomain.getName());
    }

    @Test
    public void testUpdate() {
        Domain domain = InstanceHelper.createDomain(null);
        ClientResponse response = RestHelper.doPostRequest(domain, DOMAINS);
        assertEquals(201, response.getStatus());
        Domain createdDomain = response.getEntity(new GenericType<Domain>() {
        });

        createdDomain.setDescription("new description");
        //Cannot be updated
        String newName = "NEW-NAME";
        createdDomain.setName(newName);
        createdDomain.setDateCreated(new Date());

        ClientResponse updateResponse = RestHelper.doPutRequest(createdDomain, DOMAINS, "/" + createdDomain.getUuid());
        assertEquals(200, updateResponse.getStatus());
        Domain updatedDomain = updateResponse.getEntity(new GenericType<Domain>() {
        });

        assertNotNull(updatedDomain.getLastUpdate());
        assertEquals(createdDomain.getDescription(), updatedDomain.getDescription());
        assertThat(newName, not(updatedDomain.getName()));
    }

    @Test
    public void testGetDomainRoles() {
        fail("The test case is a prototype.");
    }
}
