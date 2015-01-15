/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.account;

import com.josue.kingdom.account.entity.Manager;
import com.josue.kingdom.credential.AuthRepository;
import com.josue.kingdom.credential.entity.APICredential;
import com.josue.kingdom.credential.entity.ManagerCredential;
import com.josue.kingdom.domain.entity.APIDomainCredential;
import com.josue.kingdom.domain.entity.Domain;
import com.josue.kingdom.domain.entity.DomainCredential;
import com.josue.kingdom.domain.entity.DomainRole;
import com.josue.kingdom.domain.entity.ManagerDomainCredential;
import com.josue.kingdom.testutils.ArquillianTestBase;
import com.josue.kingdom.testutils.InstanceHelper;
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

        APICredential foundApiCredentialByToken = repository.getAPICredentialByToken(apiDomainCredential.getCredential().getApiKey());
        assertNotNull(foundApiCredentialByToken);
        assertEquals(apiDomainCredential.getCredential(), foundApiCredentialByToken);
    }

    @Test
    public void testGetApiDomainCredentials() {
        APIDomainCredential apiDomainCredential = InstanceHelper.createFullAPIDomainCredential(repository);

        List<DomainCredential> foundApiCredentials = repository.getAPIDomainCredentials(apiDomainCredential.getCredential().getUuid());
        assertEquals(1, foundApiCredentials.size());
        for (DomainCredential domainCred : foundApiCredentials) {
            assertEquals(apiDomainCredential, domainCred);
        }
    }

    @Test
    public void testFindManagerCredentialByLogin() {
        Manager manager = InstanceHelper.createManager();
        repository.create(manager);

        ManagerCredential credential = InstanceHelper.createManagerCredential(manager);
        repository.create(credential);

        ManagerCredential foundCredential = repository.getManagerCredentialByLogin(credential.getLogin());
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

        DomainRole role = InstanceHelper.createRole(domain);
        repository.create(role);

        ManagerDomainCredential domainCredential = InstanceHelper.createManagerDomainCredential(domain, credential, role);
        repository.create(domainCredential);

        List<DomainCredential> managerDomainCredentials = repository.getManagerDomainCredentials(credential.getUuid());
        assertEquals(1, managerDomainCredentials.size());
        assertEquals(domainCredential, managerDomainCredentials.get(0));
    }
}
