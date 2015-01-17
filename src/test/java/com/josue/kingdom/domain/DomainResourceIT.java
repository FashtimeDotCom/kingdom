/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.domain;

import com.josue.kingdom.domain.entity.Domain;
import com.josue.kingdom.rest.ListResource;
import com.josue.kingdom.testutils.ArquillianTestBase;
import com.josue.kingdom.testutils.InstanceHelper;
import com.josue.kingdom.testutils.RestHelper;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import java.util.Date;
import javax.ws.rs.core.Response;
import static org.hamcrest.CoreMatchers.not;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Josue
 */
@RunAsClient
@RunWith(Arquillian.class)
public class DomainResourceIT {

    private static final String DOMAINS = "domains";
    private static final String OWNED_DOMAINS = "owned";
    private static final String JOINED_DOMAINS = "joined";

    @Deployment
    @TargetsContainer("wildfly-managed")
    public static WebArchive createDeployment() {
        return ArquillianTestBase.createDefaultDeployment();
    }

    @Before
    public void init() {
        String s = "";
    }

    private Domain createDomainWithPermissions() {
        Domain domain = InstanceHelper.createDomain(null);
        ClientResponse domainResponse = RestHelper.doPostRequest(domain, DOMAINS);
        RestHelper.assertStatusCode(Response.Status.CREATED.getStatusCode(), domainResponse);

        Domain createdDomain = domainResponse.getEntity(new GenericType<Domain>() {
        });
        assertNotNull(createdDomain);
        return createdDomain;
    }

    //Testing against the self joined domain, when a new one is created
    @Test
    public void testGetJoinedDomains() throws Exception {

        ClientResponse getDomainsResponse = RestHelper.doGetRequest(DOMAINS, JOINED_DOMAINS);
        RestHelper.assertStatusCode(Response.Status.OK.getStatusCode(), getDomainsResponse);
        ListResource<Domain> domains = getDomainsResponse.getEntity(new GenericType<ListResource<Domain>>() {
        });
        //Values based on Liquibase test changelog
        assertTrue(domains.getItems().size() == 2);
    }

    //Testing against the self joined domain, when a new one is created
    @Test
    public void testGetJoinedDomain() throws Exception {
        Domain domain = createDomainWithPermissions();

        ClientResponse getDomainsResponse = RestHelper.doGetRequest(DOMAINS, JOINED_DOMAINS, domain.getUuid());
        RestHelper.assertStatusCode(Response.Status.OK.getStatusCode(), getDomainsResponse);
        Domain foundDomain = getDomainsResponse.getEntity(new GenericType<Domain>() {
        });
        assertNotNull(domain);
        assertEquals(domain, foundDomain);
    }

    @Test
    public void testGetOwnedDomains() throws Exception {
        Domain createdDomain = createDomainWithPermissions();

        ClientResponse getDomainsResponse = RestHelper.doGetRequest(DOMAINS, OWNED_DOMAINS);
        RestHelper.assertStatusCode(Response.Status.OK.getStatusCode(), getDomainsResponse);
        ListResource<Domain> domains = getDomainsResponse.getEntity(new GenericType<ListResource<Domain>>() {
        });
        assertTrue(domains.getItems().size() >= 1);
        assertTrue(domains.getItems().contains(createdDomain));
    }

    @Test
    public void testGetOwnedDomain() throws Exception {
        Domain createdDomain = createDomainWithPermissions();

        ClientResponse getDomainResponse = RestHelper.doGetRequest(DOMAINS, OWNED_DOMAINS, createdDomain.getUuid());
        RestHelper.assertStatusCode(Response.Status.OK.getStatusCode(), getDomainResponse);
        Domain foundDomain = getDomainResponse.getEntity(new GenericType<Domain>() {
        });
        assertEquals(createdDomain, foundDomain);
    }

    @Test
    public void testCreateDomain() throws Exception {
        createDomainWithPermissions();
    }

    @Test
    public void testUpdateDomain() throws Exception {
        Domain createdDomain = createDomainWithPermissions();
        String domainUuid = createdDomain.getUuid();
        createdDomain.setDescription("new description");
        //Cannot be updated
        createdDomain.setName("new-name");
        createdDomain.setUuid("illegal-uuid-set");
        createdDomain.setDateCreated(new Date());

        ClientResponse updateResponse = RestHelper.doPutRequest(createdDomain, DOMAINS, domainUuid);
        RestHelper.assertStatusCode(Response.Status.OK.getStatusCode(), updateResponse);

        Domain updatedDomain = updateResponse.getEntity(new GenericType<Domain>() {
        });
        assertNotNull(updatedDomain.getLastUpdate());
        assertEquals(updatedDomain.getDescription(), updatedDomain.getDescription());
        assertThat(createdDomain.getName(), not(updatedDomain.getName()));
        assertThat(createdDomain.getUuid(), not(updatedDomain.getUuid()));
    }

    @Test
    public void testDeleteDomain() throws Exception {
        Domain createdDomain = createDomainWithPermissions();

        ClientResponse deleteResponse = RestHelper.doDeleteRequest(DOMAINS, createdDomain.getUuid());
        RestHelper.assertStatusCode(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse);

        ClientResponse getDomainResponse = RestHelper.doGetRequest(DOMAINS, OWNED_DOMAINS, createdDomain.getUuid());
        RestHelper.assertStatusCode(Response.Status.NOT_FOUND.getStatusCode(), getDomainResponse);
    }
}
