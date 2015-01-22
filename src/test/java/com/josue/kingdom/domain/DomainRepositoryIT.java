///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.josue.kingdom.domain;
//
//import com.josue.kingdom.credential.entity.APICredential;
//import com.josue.kingdom.credential.entity.Manager;
//import com.josue.kingdom.domain.entity.Domain;
//import com.josue.kingdom.domain.entity.DomainPermission;
//import com.josue.kingdom.domain.entity.ManagerMembership;
//import com.josue.kingdom.testutils.ArquillianTestBase;
//import com.josue.kingdom.testutils.InstanceHelper;
//import java.util.List;
//import java.util.logging.Logger;
//import javax.inject.Inject;
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import javax.persistence.Query;
//import org.jboss.arquillian.container.test.api.Deployment;
//import org.jboss.arquillian.container.test.api.TargetsContainer;
//import org.jboss.arquillian.junit.Arquillian;
//import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
//import org.jboss.arquillian.transaction.api.annotation.Transactional;
//import org.jboss.shrinkwrap.api.spec.WebArchive;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertNull;
//import static org.junit.Assert.assertTrue;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
///**
// *
// * @author Josue
// */
//@RunWith(Arquillian.class)
//@Transactional(TransactionMode.DISABLED)
//public class DomainRepositoryIT {
//
//    private static final Integer DEFAULT_LIMIT = 100;
//    private static final Integer DEFAULT_OFFSET = 0;
//
//    private static final Logger LOG = Logger.getLogger(DomainRepositoryIT.class.getName());
//
//    @PersistenceContext
//    EntityManager em;
//
//    @Inject
//    DomainRepository repository;
//
//    @Deployment
//    @TargetsContainer("wildfly-managed")
//    public static WebArchive createDeployment() {
//        return ArquillianTestBase.createDefaultDeployment();
//    }
//
//    @Test
//    public void testGetJoinedDomains() {
//        //The main Manager full tree
//        ManagerMembership domainCredential = InstanceHelper.createFullManagerMembership(repository);
//        Domain domain = domainCredential.getDomain();
//
//        //The manager to be invited
//        Manager invitedManager = InstanceHelper.createManager();
//        repository.create(invitedManager);
//        Manager invitedManager = InstanceHelper.createManager(invitedManager);
//        repository.create(invitedManager);
//
//        DomainPermission simplePermission = InstanceHelper.createPermission(domain);
//        repository.create(simplePermission);
//
//        //Assign the new manager to the Domain
//        ManagerMembership invitedDomainCredential = InstanceHelper.createManagerDomainCredential(domain, invitedManager, simplePermission);
//        repository.create(invitedDomainCredential);
//
//        List<Domain> foundDomainCredentials = repository.getJoinedDomains(invitedManager.getManager().getUuid(), DEFAULT_LIMIT, DEFAULT_OFFSET);
//        assertEquals(1, foundDomainCredentials.size());
//        assertEquals(invitedDomainCredential.getDomain(), foundDomainCredentials.get(0));
//
//    }
//
//    @Test
//    public void testGetJoinedDomain() {
//
//        //The main Manager full tree
//        ManagerMembership domainCredential = InstanceHelper.createFullManagerMembership(repository);
//        Domain domain = domainCredential.getDomain();
//
//        //The manager to be invited
//        Manager invitedManager = InstanceHelper.createManager();
//        repository.create(invitedManager);
//        Manager invitedManager = InstanceHelper.createManager(invitedManager);
//        repository.create(invitedManager);
//
//        DomainPermission simplePermission = InstanceHelper.createPermission(domain);
//        repository.create(simplePermission);
//
//        //Assign the new manager to the Domain
//        ManagerMembership invitedDomainCredential = InstanceHelper.createManagerDomainCredential(domain, invitedManager, simplePermission);
//        repository.create(invitedDomainCredential);
//
//        Domain joinedDomain = repository.getJoinedDomain(invitedManager.getManager().getUuid(), domain.getUuid());
//        assertEquals(domain, joinedDomain);
//    }
//
//    @Test
//    public void testGetOwnedDomain() {
//        ManagerMembership domainCredential = InstanceHelper.createFullManagerMembership(repository);
//        Manager manager = domainCredential.getManager();
//        Domain domain = domainCredential.getDomain();
//
//        Domain foundDomain = repository.getOwnedDomain(domain.getUuid(), manager.getUuid());
//        assertNotNull(foundDomain);
//        assertEquals(domain, foundDomain);
//    }
//
//    @Test
//    public void testGetOwnedDomains() {
//        ManagerMembership domainCredential = InstanceHelper.createFullManagerMembership(repository);
//        Manager manager = domainCredential.getCredential().getManager();
//
//        List<Domain> ownedDomains = repository.getOwnedDomains(manager.getUuid(), DEFAULT_LIMIT, DEFAULT_OFFSET);
//        assertEquals(1, ownedDomains.size());
//        assertEquals(domainCredential.getDomain(), ownedDomains.get(0));
//    }
//
//    @Test
//    public void testGetDomainByName() {
//        Manager manager = InstanceHelper.createManager();
//        repository.create(manager);
//        Manager manCred = InstanceHelper.createManager(manager);
//        repository.create(manCred);
//
//        Domain domain = InstanceHelper.createDomain(manager);
//        repository.create(domain);
//
//        String domainName = domain.getName();
//        Domain foundDomain = repository.getDomainByName(domainName);
//        assertNotNull(foundDomain);
//        assertEquals(domain, foundDomain);
//
//        Domain notFoundDomain = repository.getDomainByName("INEXISTENT-NAME");
//        assertNull(notFoundDomain);
//
//        notFoundDomain = repository.getDomainByName(domain.getName().substring(0, 2));
//        assertNull(notFoundDomain);
//
//        notFoundDomain = repository.getDomainByName(domain.getName().substring(2, domain.getName().length()));
//        assertNull(notFoundDomain);
//    }
//
//    @Test
//    public void testCountDomainCredentials() {
//        //Domain 1
//        ManagerMembership domainCredential1 = InstanceHelper.createFullManagerMembership(repository);
//        Domain domain1 = domainCredential1.getDomain();
//
//        //Domain 2
//        ManagerMembership domainCredential2 = InstanceHelper.createFullManagerMembership(repository);
//        Domain domain2 = domainCredential2.getDomain();
//
//        //The target manager
//        Manager invitedManager = InstanceHelper.createManager();
//        repository.create(invitedManager);
//        Manager invitedManager = InstanceHelper.createManager(invitedManager);
//        repository.create(invitedManager);
//
//        DomainPermission simplePermission1 = InstanceHelper.createPermission(domain1);
//        repository.create(simplePermission1);
//
//        DomainPermission simplePermission2 = InstanceHelper.createPermission(domain2);
//        repository.create(simplePermission2);
//
//        //Assign the new manager to the Domain 1
//        ManagerMembership invitedDomainCredential1 = InstanceHelper.createManagerDomainCredential(domain1, invitedManager, simplePermission1);
//        repository.create(invitedDomainCredential1);
//
//        //Assign the new manager to the Domain 2
//        ManagerMembership invitedDomainCredential2 = InstanceHelper.createManagerDomainCredential(domain2, invitedManager, simplePermission2);
//        repository.create(invitedDomainCredential2);
//
//        Long count = repository.countDomainCredentials(invitedManager.getUuid());
//        assertEquals(new Long(2), count);
//    }
//
//    @Test
//    public void testCountOwnedDomains() {
//        //Domain 1
//        ManagerMembership domainCredential = InstanceHelper.createFullManagerMembership(repository);
//        Domain domain2 = InstanceHelper.createDomain(domainCredential.getCredential().getManager());
//        repository.create(domain2);
//        Domain domain3 = InstanceHelper.createDomain(domainCredential.getCredential().getManager());
//        repository.create(domain3);
//
//        Long count = repository.countOwnedDomains(domainCredential.getCredential().getManager().getUuid());
//        assertEquals(new Long(3), count);
//    }
//
//    @Test
//    public void testGetDomainPermissions() {
//        ManagerMembership domainCredential = InstanceHelper.createFullManagerMembership(repository);
//
//        List<DomainPermission> foundPermissions = repository.getDomainPermissions(domainCredential.getDomain().getUuid());
//        assertEquals(1, foundPermissions.size());
//        assertTrue(foundPermissions.contains(domainCredential.getPermission()));
//
//    }
//
//    @Test
//    public void testGetDomainPermission_String_String() {
//        ManagerMembership domainCredential = InstanceHelper.createFullManagerMembership(repository);
//        DomainPermission permission = InstanceHelper.createPermission(domainCredential.getDomain());
//        repository.create(permission);
//
//        DomainPermission foundPermission = repository.getDomainPermission(domainCredential.getDomain().getUuid(), permission.getName());
//        assertNotNull(foundPermission);
//        assertEquals(permission, foundPermission);
//    }
//
//    @Test
//    public void testGetDomainPermission_String_int() {
//        ManagerMembership domainCredential = InstanceHelper.createFullManagerMembership(repository);
//        DomainPermission permission = InstanceHelper.createPermission(domainCredential.getDomain());
//        repository.create(permission);
//
//        DomainPermission foundPermission = repository.getDomainPermission(domainCredential.getDomain().getUuid(), permission.getLevel());
//        assertNotNull(foundPermission);
//        assertEquals(permission, foundPermission);
//    }
//
//    @Test
//    public void testGetAPICredentials() {
//        APICredential apiDomCred = InstanceHelper.createFullAPICredential(repository);
//        List<APICredential> foundApiDomCred = repository.getAPICredentials(apiDomCred.getDomain().getUuid());
//        assertEquals(1, foundApiDomCred.size());
//        assertEquals(apiDomCred, foundApiDomCred.get(0));
//    }
//
//    @Test
//    @Transactional(TransactionMode.DISABLED)
//    public void testPurgeDomain() {
//        ManagerMembership domainCredential = InstanceHelper.createFullManagerMembership(repository);
//        APICredential apiCred = InstanceHelper.createAPICredential(domainCredential.getCredential().getManager());
//        repository.create(apiCred);
//        APICredential apidc = InstanceHelper.createAPICredential(domainCredential.getDomain(), apiCred, domainCredential.getPermission());
//        repository.create(apidc);
//
//        repository.purgeDomain(domainCredential.getDomain());
//
//        List<APICredential> foundApiDomCred = repository.getAPICredentials(domainCredential.getDomain().getUuid());
//        assertEquals(0, foundApiDomCred.size());
//
//        Query q1 = em.createQuery("SELECT COUNT(mandomcred) FROM ManagerDomainCredential mandomcred WHERE mandomcred.domain.uuid = :domainUuid");
//        q1.setParameter("domainUuid", domainCredential.getDomain().getUuid());
//        long mangerDomainCredCount = (long) q1.getSingleResult();
//        assertEquals(0, mangerDomainCredCount);
//
//        Query q2 = em.createQuery("SELECT COUNT(inv) FROM Invitation inv WHERE inv.domain.uuid = :domainUuid");
//        q2.setParameter("domainUuid", domainCredential.getDomain().getUuid());
//        long invitationCount = (long) q2.getSingleResult();
//        assertEquals(0, invitationCount);
//
//        Query q3 = em.createQuery("SELECT COUNT(domainperm) FROM DomainPermission domainperm WHERE domainperm.domain.uuid = :domainUuid");
//        q3.setParameter("domainUuid", domainCredential.getDomain().getUuid());
//        long permissionCount = (long) q3.getSingleResult();
//        assertEquals(0, permissionCount);
//
//        Query q4 = em.createQuery("SELECT COUNT(dom) FROM Domain dom WHERE dom.uuid = :domainUuid");
//        q4.setParameter("domainUuid", domainCredential.getDomain().getUuid());
//        long domainCount = (long) q4.getSingleResult();
//        assertEquals(0, domainCount);
//    }
//
//}
