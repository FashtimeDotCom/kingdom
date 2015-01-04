/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.domain;

import com.josue.credential.manager.ArquillianTestBase;
import com.josue.credential.manager.InstanceHelper;
import com.josue.credential.manager.auth.credential.Credential;
import com.josue.credential.manager.auth.credential.ManagerCredential;
import com.josue.credential.manager.auth.domain.Domain;
import com.josue.credential.manager.auth.domain.ManagerDomainCredential;
import com.josue.credential.manager.auth.manager.Manager;
import com.josue.credential.manager.auth.role.Role;
import java.util.List;
import java.util.logging.Logger;
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
import static org.junit.Assert.assertNull;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Josue
 */
@RunWith(Arquillian.class)
@Transactional(TransactionMode.ROLLBACK)
public class DomainRepositoryIT {

    @Deployment
    @TargetsContainer("wildfly-managed")
    public static WebArchive createDeployment() {
        return ArquillianTestBase.createDefaultDeployment();
    }

    private static final Logger LOG = Logger.getLogger(DomainRepositoryIT.class.getName());

    @PersistenceContext
    EntityManager em;

    @Inject
    DomainRepository repository;

    @Test
    public void testGetDomainsByCredential() {
        ManagerDomainCredential domainCredential = InstanceHelper.createFullManagerDomainCredential(repository);
        Credential credential = domainCredential.getCredential();
        Domain domain = domainCredential.getDomain();

        List<Domain> foundDomains = repository.getDomainsByCredential(credential.getUuid());
        assertEquals(1, foundDomains.size());
        assertEquals(domain, foundDomains.get(0));
    }

    @Test
    public void testGetJoinedDomainsByManager() {
        //The main Manager full tree
        ManagerDomainCredential domainCredential = InstanceHelper.createFullManagerDomainCredential(repository);
        Domain domain = domainCredential.getDomain();

        //The manager to be invited
        Manager invitedManager = InstanceHelper.createManager();
        repository.create(invitedManager);
        ManagerCredential invitedManagerCredential = InstanceHelper.createManagerCredential(invitedManager);
        repository.create(invitedManagerCredential);

        Role simpleRole = InstanceHelper.createRole();
        repository.create(simpleRole);

        //Assign the new manager to the Domain
        ManagerDomainCredential invitedDomainCredential = InstanceHelper.createManagerDomainCredential(domain, invitedManagerCredential, simpleRole);
        repository.create(invitedDomainCredential);

        List<ManagerDomainCredential> foundDomainCredentials = repository.getJoinedDomainsByManager(invitedManagerCredential.getManager().getUuid());
        assertEquals(1, foundDomainCredentials.size());
        assertEquals(invitedDomainCredential, foundDomainCredentials.get(0));

    }

    @Test
    public void testGetOwnedDomainsByManager() {
        ManagerDomainCredential domainCredential = InstanceHelper.createFullManagerDomainCredential(repository);
        Manager manager = domainCredential.getCredential().getManager();

        List<Domain> ownedDomains = repository.getOwnedDomainsByManager(manager.getUuid());
        assertEquals(1, ownedDomains.size());
        assertEquals(domainCredential.getDomain(), ownedDomains.get(0));
    }

    @Test
    public void testCountDomainCredentials() {
        //Domain 1
        ManagerDomainCredential domainCredential1 = InstanceHelper.createFullManagerDomainCredential(repository);
        Domain domain1 = domainCredential1.getDomain();

        //Domain 2
        ManagerDomainCredential domainCredential2 = InstanceHelper.createFullManagerDomainCredential(repository);
        Domain domain2 = domainCredential2.getDomain();

        //The target manager
        Manager invitedManager = InstanceHelper.createManager();
        repository.create(invitedManager);
        ManagerCredential invitedManagerCredential = InstanceHelper.createManagerCredential(invitedManager);
        repository.create(invitedManagerCredential);

        //commom role
        Role simpleRole = InstanceHelper.createRole();
        repository.create(simpleRole);

        //Assign the new manager to the Domain 1
        ManagerDomainCredential invitedDomainCredential1 = InstanceHelper.createManagerDomainCredential(domain1, invitedManagerCredential, simpleRole);
        repository.create(invitedDomainCredential1);

        //Assign the new manager to the Domain 2
        ManagerDomainCredential invitedDomainCredential2 = InstanceHelper.createManagerDomainCredential(domain2, invitedManagerCredential, simpleRole);
        repository.create(invitedDomainCredential2);

        Long count = repository.countDomainCredentials(invitedManager.getUuid());
        assertEquals(new Long(2), count);
    }

    @Test
    public void testCountOwnedDomains() {
        //Domain 1
        ManagerDomainCredential domainCredential = InstanceHelper.createFullManagerDomainCredential(repository);
        Domain domain2 = InstanceHelper.createDomain(domainCredential.getCredential().getManager());
        repository.create(domain2);
        Domain domain3 = InstanceHelper.createDomain(domainCredential.getCredential().getManager());
        repository.create(domain3);

        Long count = repository.countOwnedDomains(domainCredential.getCredential().getManager().getUuid());
        assertEquals(new Long(3), count);
    }

    @Test
    public void testGetDomainUuidByName() {
        Manager manager = InstanceHelper.createManager();
        repository.create(manager);
        ManagerCredential manCred = InstanceHelper.createManagerCredential(manager);
        repository.create(manCred);

        Domain domain = InstanceHelper.createDomain(manager);
        repository.create(domain);

        String domainName = domain.getName();
        String foundUuid = repository.getDomainUuidByName(domainName);
        assertNotNull(foundUuid);
        assertEquals(domain.getUuid(), foundUuid);

        String notFoundUuid = repository.getDomainUuidByName("INEXISTENT-NAME");
        assertNull(notFoundUuid);

        String exatctNameUuid = repository.getDomainUuidByName(domain.getName().substring(0, 2));
        assertNull(exatctNameUuid);

        exatctNameUuid = repository.getDomainUuidByName(domain.getName().substring(2, domain.getName().length()));
        assertNull(exatctNameUuid);

    }
}
