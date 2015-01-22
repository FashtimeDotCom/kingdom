package com.josue.kingdom.credential;

import com.josue.kingdom.credential.entity.APICredential;
import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.domain.entity.ManagerMembership;
import com.josue.kingdom.testutils.ArquillianTestBase;
import com.josue.kingdom.testutils.InstanceHelper;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
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
@RunWith(Arquillian.class)
@Transactional(TransactionMode.DISABLED)
public class CredentialRepositoryIT {

    @PersistenceContext
    EntityManager em;

    @Inject
    CredentialRepository repository;

    private static final Integer DEFAULT_LIMIT = 100;
    private static final Integer DEFAULT_OFFSET = 0;

    @Deployment
    @TargetsContainer("wildfly-managed")
    public static WebArchive createDeployment() {
        return ArquillianTestBase.createDefaultDeployment();
    }

    @Test
    public void testGetAPICredentialsByDomain() {
        ManagerMembership membership = InstanceHelper.createFullManagerMembership(repository);

        APICredential apiCredential = InstanceHelper.createAPICredential(membership);
        repository.create(apiCredential);

        List<APICredential> foundAPICredentials = repository.getAPICredentials(InstanceHelper.APP_ID, membership.getDomain().getUuid(), DEFAULT_LIMIT, DEFAULT_OFFSET);
        assertEquals(1, foundAPICredentials.size());
        assertEquals(apiCredential, foundAPICredentials.get(0));
    }

    @Test
    public void testGetAPICredentialsByDomainAndManager() {

        ManagerMembership membership = InstanceHelper.createFullManagerMembership(repository);

        APICredential apiCredential = InstanceHelper.createAPICredential(membership);
        repository.create(apiCredential);

        List<APICredential> foundAPICredentials = repository.getAPICredentials(InstanceHelper.APP_ID, membership.getDomain().getUuid(), membership.getManager().getUuid(), DEFAULT_LIMIT, DEFAULT_OFFSET);
        assertEquals(1, foundAPICredentials.size());
        assertEquals(apiCredential, foundAPICredentials.get(0));

    }

    @Test
    public void testGetAPICredential() {
        ManagerMembership membership = InstanceHelper.createFullManagerMembership(repository);

        APICredential apiCredential = InstanceHelper.createAPICredential(membership);
        repository.create(apiCredential);

        APICredential foundAPICredential = repository.getAPICredential(InstanceHelper.APP_ID, apiCredential.getUuid());
        assertNotNull(foundAPICredential);
        assertEquals(apiCredential, foundAPICredential);
    }

    @Test
    public void testCountAPICredentialByDomainAndManager() {
        ManagerMembership membership = InstanceHelper.createFullManagerMembership(repository);

        APICredential apiCredential1 = InstanceHelper.createAPICredential(membership);
        repository.create(apiCredential1);

        APICredential apiCredential2 = InstanceHelper.createAPICredential(membership);
        repository.create(apiCredential2);

        long count = repository.countAPICredential(InstanceHelper.APP_ID, membership.getDomain().getUuid(), membership.getManager().getUuid());
        assertEquals(2, count);

    }

    @Test
    public void testCountAPICredentialByDomain() {
        ManagerMembership membership = InstanceHelper.createFullManagerMembership(repository);

        APICredential apiCredential1 = InstanceHelper.createAPICredential(membership);
        repository.create(apiCredential1);

        APICredential apiCredential2 = InstanceHelper.createAPICredential(membership);
        repository.create(apiCredential2);

        long count = repository.countAPICredential(InstanceHelper.APP_ID, membership.getDomain().getUuid(), membership.getManager().getUuid());
        assertEquals(2, count);
    }

    @Test
    public void testGetManagers() {
        ManagerMembership membership = InstanceHelper.createFullManagerMembership(repository);
        List<Manager> foundManagers = repository.getManagers(InstanceHelper.APP_ID, DEFAULT_LIMIT, DEFAULT_OFFSET);
        assertTrue(foundManagers.size() >= 1);//App level managers

        boolean hasManager = false;
        for (Manager manager : foundManagers) {
            if (membership.getManager().equals(manager)) {
                hasManager = true;
            }
        }
        assertTrue(hasManager);
    }

    @Test
    public void testGetManagerByEmail() {
        ManagerMembership membership = InstanceHelper.createFullManagerMembership(repository);
        Manager foundManager = repository.getManagerByEmail(InstanceHelper.APP_ID, membership.getManager().getEmail());
        assertNotNull(foundManager);
        assertEquals(membership.getManager(), foundManager);
    }

    @Test
    public void testGetManagerByUsername() {
        ManagerMembership membership = InstanceHelper.createFullManagerMembership(repository);
        Manager foundManager = repository.getManagerByUsername(InstanceHelper.APP_ID, membership.getManager().getUsername());
        assertNotNull(foundManager);
        assertEquals(membership.getManager(), foundManager);
    }

    @Test
    public void testGetManagerMembership() {
        ManagerMembership membership = InstanceHelper.createFullManagerMembership(repository);
        ManagerMembership foundMembership = repository.getManagerMembership(InstanceHelper.APP_ID, membership.getDomain().getUuid(), membership.getManager().getUuid());
        assertNotNull(foundMembership);
        assertEquals(membership, foundMembership);
    }

    @Test
    public void testGetManagerMembershipByManager() {
        ManagerMembership membership = InstanceHelper.createFullManagerMembership(repository);
        List<ManagerMembership> foundMemberships = repository.getManagerMembershipByManager(InstanceHelper.APP_ID, membership.getManager().getUuid());
        assertEquals(1, foundMemberships.size());
        assertEquals(membership, foundMemberships.get(0));
    }

    @Test
    public void testGetManagerMembershipByDomain() {
        ManagerMembership membership = InstanceHelper.createFullManagerMembership(repository);
        List<ManagerMembership> foundMemberships = repository.getManagerMembershipByDomain(InstanceHelper.APP_ID, membership.getDomain().getUuid());
        assertEquals(1, foundMemberships.size());
        assertEquals(membership, foundMemberships.get(0));
    }

}
