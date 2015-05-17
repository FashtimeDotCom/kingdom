/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.domain;

import com.josue.kingdom.credential.entity.APICredential;
import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.domain.entity.Domain;
import com.josue.kingdom.domain.entity.DomainPermission;
import com.josue.kingdom.domain.entity.ManagerMembership;
import com.josue.kingdom.testutils.ArquillianTestBase;
import com.josue.kingdom.testutils.InstanceHelper;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
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
@Transactional(TransactionMode.ROLLBACK)
public class DomainRepositoryIT {

    private static final Integer DEFAULT_LIMIT = 100;
    private static final Integer DEFAULT_OFFSET = 0;

    @PersistenceContext
    EntityManager em;

    @Inject
    DomainRepository repository;

    @Deployment
    @TargetsContainer("wildfly-managed")
    public static WebArchive createDeployment() {
        return ArquillianTestBase.createDefaultDeployment();
    }

    @Test
    public void testGetJoinedDomains() {
        //The main Manager full tree
        ManagerMembership domainCredential = InstanceHelper.createFullManagerMembership(repository);
        Domain domain = domainCredential.getDomain();

        //The manager to be invited
        Manager invitedManager = InstanceHelper.createManager();
        repository.create(invitedManager);

        DomainPermission simplePermission = InstanceHelper.createPermission(domain);
        repository.create(simplePermission);

        //Assign the new manager to the Domain
        ManagerMembership invitedDomainCredential = InstanceHelper.createManagerMembership(domain, invitedManager, simplePermission);
        repository.create(invitedDomainCredential);

        List<Domain> foundDomainCredentials = repository.getJoinedDomains(InstanceHelper.APP_ID, invitedManager.getUuid(), DEFAULT_LIMIT, DEFAULT_OFFSET);
        assertEquals(1, foundDomainCredentials.size());
        assertEquals(invitedDomainCredential.getDomain(), foundDomainCredentials.get(0));

    }

    @Test
    public void testGetJoinedDomain() {

        //The main Manager full tree
        ManagerMembership domainCredential = InstanceHelper.createFullManagerMembership(repository);
        Domain domain = domainCredential.getDomain();

        //The manager to be invited
        Manager invitedManager = InstanceHelper.createManager();
        repository.create(invitedManager);

        DomainPermission simplePermission = InstanceHelper.createPermission(domain);
        repository.create(simplePermission);

        //Assign the new manager to the Domain
        ManagerMembership invitedMembership = InstanceHelper.createManagerMembership(domain, invitedManager, simplePermission);
        repository.create(invitedMembership);

        Domain joinedDomain = repository.getJoinedDomain(InstanceHelper.APP_ID, domain.getUuid(), invitedManager.getUuid());
        assertEquals(domain, joinedDomain);
    }

    @Test
    public void testGetOwnedDomain() {
        ManagerMembership domainCredential = InstanceHelper.createFullManagerMembership(repository);
        Manager manager = domainCredential.getManager();
        Domain domain = domainCredential.getDomain();

        Domain foundDomain = repository.getOwnedDomain(InstanceHelper.APP_ID, domain.getUuid(), manager.getUuid());
        assertNotNull(foundDomain);
        assertEquals(domain, foundDomain);
    }

    @Test
    public void testGetOwnedDomains() {
        ManagerMembership membership = InstanceHelper.createFullManagerMembership(repository);
        Manager manager = membership.getManager();

        List<Domain> ownedDomains = repository.getOwnedDomains(InstanceHelper.APP_ID, manager.getUuid(), DEFAULT_LIMIT, DEFAULT_OFFSET);
        assertEquals(1, ownedDomains.size());
        assertEquals(membership.getDomain(), ownedDomains.get(0));
    }

    @Test
    public void testCountOwnedDomains() {
        //Domain 1
        ManagerMembership membership = InstanceHelper.createFullManagerMembership(repository);

        Domain domain2 = InstanceHelper.createDomain(membership.getManager());
        repository.create(domain2);

        Domain domain3 = InstanceHelper.createDomain(membership.getManager());
        repository.create(domain3);

        Long count = repository.countOwnedDomains(InstanceHelper.APP_ID, membership.getManager().getUuid());
        assertEquals(Long.valueOf(3), count);
    }

    @Test
    public void testCountJoinedDomains() {
        ManagerMembership membership = InstanceHelper.createFullManagerMembership(repository);
        Long count = repository.countJoinedDomains(InstanceHelper.APP_ID, membership.getManager().getUuid());
        assertEquals(Long.valueOf(1), count);
    }

    @Test
    public void testGetDomainPermissions() {
        ManagerMembership domainCredential = InstanceHelper.createFullManagerMembership(repository);

        List<DomainPermission> foundPermissions = repository.getDomainPermissions(InstanceHelper.APP_ID, domainCredential.getDomain().getUuid(), DEFAULT_LIMIT, DEFAULT_OFFSET);
        assertEquals(1, foundPermissions.size());
        assertTrue(foundPermissions.contains(domainCredential.getPermission()));

    }

    @Test
    public void testGetDomainPermission_String_String() {
        ManagerMembership domainCredential = InstanceHelper.createFullManagerMembership(repository);
        DomainPermission permission = InstanceHelper.createPermission(domainCredential.getDomain());
        repository.create(permission);

        DomainPermission foundPermission = repository.getDomainPermission(InstanceHelper.APP_ID, domainCredential.getDomain().getUuid(), permission.getName());
        assertNotNull(foundPermission);
        assertEquals(permission, foundPermission);
    }

    @Test
    public void testGetDomainPermission_String_int() {
        ManagerMembership domainCredential = InstanceHelper.createFullManagerMembership(repository);
        DomainPermission permission = InstanceHelper.createPermission(domainCredential.getDomain());
        repository.create(permission);

        DomainPermission foundPermission = repository.getDomainPermission(InstanceHelper.APP_ID, domainCredential.getDomain().getUuid(), permission.getLevel());
        assertNotNull(foundPermission);
        assertEquals(permission, foundPermission);
    }

    @Test
    @Transactional(TransactionMode.DISABLED)
    public void testPurgeDomain() {
        ManagerMembership membership = InstanceHelper.createFullManagerMembership(repository);
        APICredential apiCred = InstanceHelper.createAPICredential(membership);
        repository.create(apiCred);

        APICredential apidc = InstanceHelper.createAPICredential(membership);
        repository.create(apidc);

        TypedQuery<Long> managerCountQuery = em.createQuery("SELECT COUNT(man.uuid) FROM Manager man WHERE man.application.uuid = :appUuid", Long.class);
        managerCountQuery.setParameter("appUuid", InstanceHelper.APP_ID);
        long initialManagerCount = managerCountQuery.getSingleResult();
        assertTrue(initialManagerCount > 0);

        repository.purgeDomain(InstanceHelper.APP_ID, membership.getDomain().getUuid());

        TypedQuery<Long> membershipQuery = em.createQuery("SELECT COUNT(membership.uuid) FROM ManagerMembership membership WHERE membership.domain.uuid = :domainUuid AND membership.application.uuid = :appUuid", Long.class);
        membershipQuery.setParameter("domainUuid", membership.getDomain().getUuid());
        membershipQuery.setParameter("appUuid", InstanceHelper.APP_ID);
        long mangerDomainCredCount = membershipQuery.getSingleResult();
        assertEquals(0, mangerDomainCredCount);

        TypedQuery<Long> invitationQuery = em.createQuery("SELECT COUNT(inv.uuid) FROM Invitation inv WHERE inv.domain.uuid = :domainUuid AND inv.application.uuid = :appUuid", Long.class);
        invitationQuery.setParameter("domainUuid", membership.getDomain().getUuid());
        invitationQuery.setParameter("appUuid", InstanceHelper.APP_ID);
        long invitationCount = invitationQuery.getSingleResult();
        assertEquals(0, invitationCount);

        TypedQuery<Long> domainPermissionQuery = em.createQuery("SELECT COUNT(domainperm.uuid) FROM DomainPermission domainperm WHERE domainperm.domain.uuid = :domainUuid AND domainperm.application.uuid = :appUuid", Long.class);
        domainPermissionQuery.setParameter("domainUuid", membership.getDomain().getUuid());
        domainPermissionQuery.setParameter("appUuid", InstanceHelper.APP_ID);
        long permissionCount = domainPermissionQuery.getSingleResult();
        assertEquals(0, permissionCount);

        TypedQuery<Long> domainQuery = em.createQuery("SELECT COUNT(dom.uuid) FROM Domain dom WHERE dom.uuid = :domainUuid AND dom.application.uuid = :appUuid", Long.class);
        domainQuery.setParameter("domainUuid", membership.getDomain().getUuid());
        domainQuery.setParameter("appUuid", InstanceHelper.APP_ID);
        long domainCount = domainQuery.getSingleResult();
        assertEquals(0, domainCount);

        TypedQuery<Long> apicredentialQuery = em.createQuery("SELECT COUNT(cred.uuid) FROM APICredential cred WHERE cred.membership.domain.uuid = :domainUuid AND cred.application.uuid = :appUuid", Long.class);
        apicredentialQuery.setParameter("domainUuid", membership.getDomain().getUuid());
        apicredentialQuery.setParameter("appUuid", InstanceHelper.APP_ID);
        long apiCredCount = apicredentialQuery.getSingleResult();
        assertEquals(0, apiCredCount);

        //should have same Manager count
        long finalManagerCount = managerCountQuery.getSingleResult();
        assertEquals(initialManagerCount, finalManagerCount);
    }

}
