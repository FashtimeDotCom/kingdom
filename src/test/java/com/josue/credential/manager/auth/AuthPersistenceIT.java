/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.auth;

import com.josue.credential.manager.ArquillianTestBase;
import com.josue.credential.manager.InstanceHelper;
import com.josue.credential.manager.account.AccountRepository;
import com.josue.credential.manager.account.Manager;
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
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Josue
 */
@RunWith(Arquillian.class)
@Transactional(TransactionMode.COMMIT)
public class AuthPersistenceIT {

    @Deployment
    @TargetsContainer("wildfly-managed")
    public static WebArchive createDeployment() {
        return ArquillianTestBase.createDefaultDeployment();
    }

    @PersistenceContext
    EntityManager em;

    @Inject
    AccountRepository repository;

    @Test
    public void testRole() {
        Role role = InstanceHelper.createRole();
        repository.create(role);

        Role foundRole = repository.find(Role.class, role.getId());
        assertEquals(role, foundRole);
    }

    @Test
    public void testDomain() {
        Manager manager = InstanceHelper.createManager();
        ManagerCredential credential = InstanceHelper.createManagerCredential(manager);
        repository.create(credential);

        Domain domain = InstanceHelper.createDomain(manager);
        repository.create(domain);

        Domain foundDomain = repository.find(Domain.class, domain.getUuid());
        assertEquals(domain, foundDomain);

    }

    @Test
    public void testApiCredential() {
        Manager manager = InstanceHelper.createManager();
        ManagerCredential credential = InstanceHelper.createManagerCredential(manager);
        repository.create(credential);

        APICredential foundCredential = repository.find(APICredential.class, credential.getUuid());
        assertEquals(credential, foundCredential);
    }

    @Test
    public void testManagerCredential() {
        Manager manager = InstanceHelper.createManager();
        ManagerCredential credential = InstanceHelper.createManagerCredential(manager);
        repository.create(credential);

        ManagerCredential foundCredential = repository.find(ManagerCredential.class, credential.getUuid());
        assertEquals(credential, foundCredential);
    }

    @Test
    public void testApiDomainCredential() {
        Manager manager = InstanceHelper.createManager();
        ManagerCredential credential = InstanceHelper.createManagerCredential(manager);
        repository.create(credential);

        Domain domain = InstanceHelper.createDomain(manager);
        repository.create(domain);

        APICredential credapiCredential = InstanceHelper.createAPICredential(manager);
        repository.create(credapiCredential);

        Role role = InstanceHelper.createRole();
        repository.create(role);

        APIDomainCredential domainCredential = InstanceHelper.createAPIDomainCredential(domain, credapiCredential, role);
        repository.create(domainCredential);

        APIDomainCredential foundDomainCredential = repository.find(APIDomainCredential.class, domainCredential.getUuid());
        assertEquals(domainCredential, foundDomainCredential);
    }

    @Test
    public void testManagerDomainCredential() {
        Manager manager = InstanceHelper.createManager();
        ManagerCredential credential = InstanceHelper.createManagerCredential(manager);
        repository.create(credential);

        Domain domain = InstanceHelper.createDomain(manager);
        repository.create(domain);

        ManagerCredential managerCredential = InstanceHelper.createManagerCredential(manager);
        repository.create(managerCredential);

        Role role = InstanceHelper.createRole();
        repository.create(role);

        ManagerDomainCredential domainCredential = InstanceHelper.createManagerDomainCredential(domain, managerCredential, role);
        repository.create(domainCredential);

        ManagerDomainCredential foundDomainCredential = repository.find(ManagerDomainCredential.class, domainCredential.getUuid());
        assertEquals(domainCredential, foundDomainCredential);
    }
}
