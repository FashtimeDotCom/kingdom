/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.domain;

import com.josue.kingdom.domain.entity.Domain;
import com.josue.kingdom.domain.entity.DomainPermission;
import com.josue.kingdom.domain.entity.ManagerDomainCredential;
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
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Josue
 */
@RunAsClient
@RunWith(Arquillian.class)
public class DomainResourceIT {

    private static final String DOMAINS = "/domains";
    private static final String OWNED_DOMAINS = "/owned";
    private static final String JOINED_DOMAINS = "/joined";
    private static final String DOMAIN_PERMISSIONS = "/permissions";

    @Deployment
    @TargetsContainer("wildfly-managed")
    public static WebArchive createDeployment() {
        return ArquillianTestBase.createDefaultDeployment();
    }

    private Domain createDomainWithPermissions() {
        Domain domain = InstanceHelper.createDomain(null);
        ClientResponse domainResponse = RestHelper.doPostRequest(domain, DOMAINS);
        assertEquals(Response.Status.CREATED, domainResponse.getStatus());

        DomainPermission permission = InstanceHelper.createPermission(domain);
        ClientResponse permissionResponse = RestHelper.doPostRequest(permission, DOMAINS, DOMAIN_PERMISSIONS);
        assertEquals(Response.Status.CREATED, permissionResponse.getStatus());

        Domain foundDomain = domainResponse.getEntity(new GenericType<Domain>() {
        });
        assertNotNull(foundDomain);
        return foundDomain;
    }

    //Testing against the self joined domain, when a new one is created
    @Test
    public void testGetJoinedDomains() throws Exception {
        Domain domain = createDomainWithPermissions();

        ClientResponse getDomainsResponse = RestHelper.doGetRequest(DOMAINS, JOINED_DOMAINS);
        assertEquals(Response.Status.OK, getDomainsResponse.getStatus());
        ListResource<ManagerDomainCredential> domains = getDomainsResponse.getEntity(new GenericType<ListResource<ManagerDomainCredential>>() {
        });
        assertTrue(domains.getItems().size() >= 1);
        boolean hasDomain = false;
        for (ManagerDomainCredential d : domains.getItems()) {
            if (d.getDomain() == domain) {
                hasDomain = true;
            }
        }
        assertTrue(hasDomain);
    }

    //Testing against the self joined domain, when a new one is created
    @Test
    public void testGetJoinedDomain() throws Exception {
        Domain domain = createDomainWithPermissions();

        ClientResponse getDomainsResponse = RestHelper.doGetRequest(DOMAINS, JOINED_DOMAINS, "/" + domain.getUuid());
        assertEquals(Response.Status.OK, getDomainsResponse.getStatus());
        ManagerDomainCredential domainCredential = getDomainsResponse.getEntity(new GenericType<ManagerDomainCredential>() {
        });
        assertNotNull(domain);
        assertEquals(domain, domainCredential);
    }

    @Test
    public void testGetOwnedDomains() throws Exception {
        Domain createdDomain = createDomainWithPermissions();

        ClientResponse getDomainsResponse = RestHelper.doGetRequest(DOMAINS, OWNED_DOMAINS);
        assertEquals(Response.Status.OK, getDomainsResponse.getStatus());
        ListResource<Domain> domains = getDomainsResponse.getEntity(new GenericType<ListResource<Domain>>() {
        });
        assertTrue(domains.getItems().size() >= 1);
        assertTrue(domains.getItems().contains(createdDomain));
    }

    @Test
    public void testGetOwnedDomain() throws Exception {
        Domain createdDomain = createDomainWithPermissions();

        ClientResponse getDomainResponse = RestHelper.doGetRequest(DOMAINS, OWNED_DOMAINS, "/" + createdDomain.getUuid());
        assertEquals(Response.Status.OK, getDomainResponse.getStatus());
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

        createdDomain.setDescription("new description");
        //Cannot be updated
        createdDomain.setName("new-name");
        createdDomain.setUuid("illegal-uuid-set");
        createdDomain.setDateCreated(new Date());

        ClientResponse updateResponse = RestHelper.doPutRequest(createdDomain, DOMAINS, "/" + createdDomain.getUuid());
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

        ClientResponse deleteResponse = RestHelper.doDeleteRequest(DOMAINS, "/" + createdDomain.getUuid());
        assertEquals(Response.Status.NO_CONTENT, deleteResponse.getStatus());

        ClientResponse getDomainResponse = RestHelper.doGetRequest(DOMAINS, OWNED_DOMAINS, "/" + createdDomain.getUuid());
        assertEquals(Response.Status.NOT_FOUND, getDomainResponse.getStatus());

    }
}
