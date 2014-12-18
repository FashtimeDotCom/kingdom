/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.account;

import com.josue.credential.manager.ArquillianTestBase;
import com.josue.credential.manager.InstanceHelper;
import com.josue.credential.manager.JpaRepository;
import com.josue.credential.manager.account.Manager;
import com.josue.credential.manager.account.ManagerInvitation;
import com.josue.credential.manager.account.ManagerInvitationStatus;
import java.util.UUID;
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
@Transactional(TransactionMode.COMMIT)
public class AccountPersistenceIT {

    @Deployment
    @TargetsContainer("wildfly-managed")
    public static WebArchive createDeployment() {
        return ArquillianTestBase.createDefaultDeployment();
    }

    @PersistenceContext
    EntityManager em;

    @Inject
    JpaRepository repository;

    @Test
    public void testCreateManager() {
        Manager manager = InstanceHelper.createManager();
        repository.create(manager);

        Manager foundManager = repository.find(Manager.class, manager.getUuid());
        assertEquals(manager, foundManager);
    }

    @Test
    public void testManagerInvitation() {
        ManagerInvitation invitation = new ManagerInvitation();

        Manager authorManager = InstanceHelper.createManager();
        repository.create(authorManager);

        invitation.setAuthorManager(authorManager);
        invitation.setTargetEmail("eduardo@gmail.com");
        invitation.setStatus(ManagerInvitationStatus.CREATED);
        invitation.setToken(UUID.randomUUID().toString());

        invitation.setValidUntil(InstanceHelper.mysqlMilliSafeTimestamp());

        repository.create(invitation);

        ManagerInvitation foundInvitation = repository.find(ManagerInvitation.class, invitation.getUuid());
        assertNotNull(invitation.getToken());
        assertNotNull(invitation.getTargetEmail());

        invitation.getValidUntil().compareTo(foundInvitation.getValidUntil());
        invitation.getValidUntil().equals(foundInvitation.getValidUntil());
        if (invitation.getValidUntil().getTime() == foundInvitation.getValidUntil().getTime()) {

        }

        assertEquals(invitation, foundInvitation);

    }

}
