/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.domain;

import com.josue.credential.manager.ArquillianTestBase;
import com.josue.credential.manager.InstanceHelper;
import com.josue.credential.manager.Logged;
import com.josue.credential.manager.RestHelper;
import com.josue.credential.manager.auth.domain.Domain;
import com.josue.credential.manager.auth.domain.ManagerDomainCredential;
import com.josue.credential.manager.rest.ListResource;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import static org.hamcrest.CoreMatchers.not;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Josue
 */
@Logged
@RunWith(Arquillian.class)
@RunAsClient
public class DomainRestIT {

    private static final String MEDIA_TYPE = "application/json;charset=utf-8";

    private static final String DOMAINS = "/domains";
    private static final String OWNED_DOMAINS = "/owned";
    private static final String JOINED_DOMAINS = "/joined";

    private Client client;
    private WebResource webResource;

    private ObjectMapper mapper;

    private static final Logger LOG = Logger.getLogger(DomainRestIT.class.getName());

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
        assertEquals(1, domains.getItems().size());
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
        Assert.assertThat(newName, not(updatedDomain.getName()));

    }
}
