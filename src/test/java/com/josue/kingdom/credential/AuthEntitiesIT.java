/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.credential;

import com.josue.kingdom.account.entity.Manager;
import com.josue.kingdom.credential.CredentialRepository;
import com.josue.kingdom.credential.entity.APICredential;
import com.josue.kingdom.credential.entity.ManagerCredential;
import com.josue.kingdom.domain.entity.APIDomainCredential;
import com.josue.kingdom.domain.entity.Domain;
import com.josue.kingdom.domain.entity.DomainPermission;
import com.josue.kingdom.domain.entity.ManagerDomainCredential;
import com.josue.kingdom.invitation.entity.Invitation;
import com.josue.kingdom.invitation.entity.InvitationStatus;
import com.josue.kingdom.testutils.ArquillianTestBase;
import com.josue.kingdom.testutils.InstanceHelper;
import java.util.UUID;
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
public class AuthEntitiesIT {

    @Deployment
    @TargetsContainer("wildfly-managed")
    public static WebArchive createDeployment() {
        return ArquillianTestBase.createDefaultDeployment();
    }

    private static final Logger LOG = Logger.getLogger(AuthEntitiesIT.class.getName());

    @PersistenceContext
    EntityManager em;

    //Here we can use any JPARepository, since only super class methods are used
    @Inject
    CredentialRepository repository;

    @Test
    public void testDomainPermission() {
        Manager owner = InstanceHelper.createManager();
        repository.create(owner);
        Domain domain = InstanceHelper.createDomain(owner);
        repository.create(domain);

        DomainPermission permission = InstanceHelper.createRole(domain);
        repository.create(permission);

        DomainPermission foundRole = repository.find(DomainPermission.class, permission.getUuid());
        assertEquals(permission, foundRole);
    }

    @Test
    public void testDomain() {
        Manager manager = InstanceHelper.createManager();
        repository.create(manager);

        ManagerCredential credential = InstanceHelper.createManagerCredential(manager);
        repository.create(credential);

        Domain domain = InstanceHelper.createDomain(manager);
        repository.create(domain);

        Domain foundDomain = repository.find(Domain.class, domain.getUuid());
        assertEquals(domain, foundDomain);

    }

    @Test
    public void testAPICredential() {
        Manager manager = InstanceHelper.createManager();
        repository.create(manager);

        ManagerCredential credential = InstanceHelper.createManagerCredential(manager);
        repository.create(credential);

        APICredential apiCredential = InstanceHelper.createAPICredential(manager);
        repository.create(apiCredential);

        APICredential foundCredential = repository.find(APICredential.class, apiCredential.getUuid());
        assertEquals(apiCredential, foundCredential);
    }

    @Test
    public void testManagerCredential() {
        Manager manager = InstanceHelper.createManager();
        repository.create(manager);

        ManagerCredential credential = InstanceHelper.createManagerCredential(manager);
        repository.create(credential);

        ManagerCredential foundCredential = repository.find(ManagerCredential.class, credential.getUuid());
        assertEquals(credential, foundCredential);
    }

    @Test
    public void testAPIDomainCredential() {
        Manager manager = InstanceHelper.createManager();
        repository.create(manager);

        ManagerCredential credential = InstanceHelper.createManagerCredential(manager);
        repository.create(credential);

        Domain domain = InstanceHelper.createDomain(manager);
        repository.create(domain);

        APICredential credapiCredential = InstanceHelper.createAPICredential(manager);
        repository.create(credapiCredential);

        DomainPermission permission = InstanceHelper.createRole(domain);
        repository.create(permission);

        APIDomainCredential domainCredential = InstanceHelper.createAPIDomainCredential(domain, credapiCredential, permission);
        repository.create(domainCredential);

        APIDomainCredential foundDomainCredential = repository.find(APIDomainCredential.class, domainCredential.getUuid());
        assertEquals(domainCredential, foundDomainCredential);
    }

    @Test
    public void testManagerDomainCredential() {
        Manager manager = InstanceHelper.createManager();
        repository.create(manager);

        ManagerCredential credential = InstanceHelper.createManagerCredential(manager);
        repository.create(credential);

        Domain domain = InstanceHelper.createDomain(manager);
        repository.create(domain);

        ManagerCredential managerCredential = InstanceHelper.createManagerCredential(manager);
        repository.create(managerCredential);

        DomainPermission permission = InstanceHelper.createRole(domain);
        permission.setDomain(domain);
        repository.create(permission);

        ManagerDomainCredential domainCredential = InstanceHelper.createManagerDomainCredential(domain, managerCredential, permission);
        repository.create(domainCredential);

        ManagerDomainCredential foundDomainCredential = repository.find(ManagerDomainCredential.class, domainCredential.getUuid());
        assertEquals(domainCredential, foundDomainCredential);
    }

    @Test
    public void testCreateManager() {

        Manager manager = InstanceHelper.createManager();
        repository.create(manager);

        ManagerCredential credential = InstanceHelper.createManagerCredential(manager);
        repository.create(credential);

        Manager foundManager = repository.find(Manager.class, manager.getUuid());
        assertEquals(manager, foundManager);
    }

    //TODO test invitation with existing manager and different Domains
    @Test
    public void testManagerInvitation() {

        Manager authorManager = InstanceHelper.createManager();
        repository.create(authorManager);

        ManagerCredential credential = InstanceHelper.createManagerCredential(authorManager);
        repository.create(credential);

        Domain domain = InstanceHelper.createDomain(authorManager);
        repository.create(domain);

        DomainPermission permission = InstanceHelper.createRole(domain);
        repository.create(permission);

        Invitation invitation = new Invitation();
        invitation.setAuthorManager(authorManager);
        invitation.setTargetEmail("eduardo@gmail.com");
        invitation.setStatus(InvitationStatus.CREATED);
        invitation.setToken(UUID.randomUUID().toString());
        invitation.setDomain(domain);
        invitation.setRole(permission);

        invitation.setValidUntil(InstanceHelper.mysqlMilliSafeTimestamp());
        repository.create(invitation);

        Invitation foundInvitation = repository.find(Invitation.class, invitation.getUuid());
        assertNotNull(invitation.getToken());
        assertNotNull(invitation.getTargetEmail());

        assertEquals(invitation, foundInvitation);

    }
}
