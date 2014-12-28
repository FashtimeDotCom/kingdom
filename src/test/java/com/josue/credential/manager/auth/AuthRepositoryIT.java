/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.auth;

import com.josue.credential.manager.ArquillianTestBase;
import com.josue.credential.manager.InstanceHelper;
import com.josue.credential.manager.auth.credential.APICredential;
import com.josue.credential.manager.auth.credential.APIDomainCredential;
import com.josue.credential.manager.auth.credential.ManagerCredential;
import com.josue.credential.manager.auth.credential.ManagerDomainCredential;
import com.josue.credential.manager.auth.domain.Domain;
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
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Josue
 */
@RunWith(Arquillian.class)
@Transactional(TransactionMode.ROLLBACK)
public class AuthRepositoryIT {

    @Deployment
    @TargetsContainer("wildfly-managed")
    public static WebArchive createDeployment() {
        return ArquillianTestBase.createDefaultDeployment();
    }

    @PersistenceContext
    EntityManager em;

    @Inject
    AuthRepository repository;

    private static final Logger LOG = Logger.getLogger(AuthRepositoryIT.class.getName());

    @Test
    public void testFindApiCredentialByToken() {
        APIDomainCredential apiDomainCredential = InstanceHelper.createFullAPIDomainCredential(repository);

        APICredential foundApiCredentialByToken = repository.findApiCredentialByToken(apiDomainCredential.getCredential().getApiKey());
        assertNotNull(foundApiCredentialByToken);
        assertEquals(apiDomainCredential.getCredential(), foundApiCredentialByToken);
    }

    @Test
    public void testGetApiDomainCredentials() {
        APIDomainCredential apiDomainCredential = InstanceHelper.createFullAPIDomainCredential(repository);

        List<APIDomainCredential> foundApiCredentials = repository.getApiDomainCredentials(apiDomainCredential.getCredential().getUuid());
        assertEquals(1, foundApiCredentials.size());
        for (APIDomainCredential domainCred : foundApiCredentials) {
            assertEquals(apiDomainCredential, domainCred);
        }
    }

    @Test
    public void testFindManagerCredentialByLogin() {
        Manager manager = InstanceHelper.createManager();
        repository.create(manager);

        ManagerCredential credential = InstanceHelper.createManagerCredential(manager);
        repository.create(credential);

        ManagerCredential foundCredential = repository.findManagerCredentialByLogin(credential.getLogin());
        assertEquals(credential, foundCredential);
    }

    @Test
    public void testGetManagerDomainCredentials() {
        Manager manager = InstanceHelper.createManager();
        repository.create(manager);

        ManagerCredential credential = InstanceHelper.createManagerCredential(manager);
        repository.create(credential);

        Domain domain = InstanceHelper.createDomain(manager);
        repository.create(domain);

        Role role = InstanceHelper.createRole();
        repository.create(role);

        ManagerDomainCredential domainCredential = InstanceHelper.createManagerDomainCredential(domain, credential, role);
        repository.create(domainCredential);

        List<ManagerDomainCredential> managerDomainCredentials = repository.getManagerDomainCredentials(credential.getUuid());
        assertEquals(1, managerDomainCredentials.size());
        assertEquals(domainCredential, managerDomainCredentials.get(0));
    }
}
