/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.domain;

import com.josue.credential.manager.ArquillianTestBase;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
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

    private static final String DOMAIN_CREDENTIAL = "/domains";
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
        ClientResponse response = RestHelper.doRequest(DOMAIN_CREDENTIAL, JOINED_DOMAINS);

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
        ClientResponse response = RestHelper.doRequest(DOMAIN_CREDENTIAL, OWNED_DOMAINS);

        assertEquals(200, response.getStatus());

        ListResource<Domain> domains = response.getEntity(new GenericType<ListResource<Domain>>() {
        });
        assertEquals(1, domains.getItems().size());
    }

//    @Test
//    public void testGetOwnedDomains() {
//        ClientResponse response = RestHelper.getWebResource().path(DOMAIN_CREDENTIAL).path(OWNED_DOMAINS).type(MEDIA_TYPE).header(API_KEY, "tmfkrkileqo65hjl9udm550hip")
//                .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
//
//        assertEquals(200, response.getStatus());
//        ListResource<ManagerDomainCredential> manDomainCreds = response.getEntity(new GenericType<ListResource<ManagerDomainCredential>>() {
//        });
//        assertTrue(manDomainCreds.getItems().size() > 0);
//
//        String uuid = manDomainCreds.getItems().get(0).getUuid();
//        ClientResponse responseByUuid = RestHelper.getWebResource().path(DOMAIN_CREDENTIAL).path(uuid).type(MEDIA_TYPE).header(API_KEY, "tmfkrkileqo65hjl9udm550hip")
//                .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
//
//        ManagerDomainCredential manDomCre = responseByUuid.getEntity(new GenericType<ManagerDomainCredential>() {
//        });
//        assertEquals(200, response.getStatus());
//        assertNotNull(manDomCre);
//        assertNull(manDomCre.getCredential());
//        assertEquals(manDomainCreds.getItems().get(0), manDomCre);
//
//    }
    @Test
    public void testCreate() {
        fail("The test case is a prototype.");
    }

    @Test
    public void testUpdate() {
        fail("The test case is a prototype.");
    }

    private <T> T getEntity(TypeReference<T> type, ClientResponse response) {
        try {
            return mapper.readValue(response.getEntity(String.class), type);
        } catch (IOException ex) {
            Logger.getLogger(DomainRestIT.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
