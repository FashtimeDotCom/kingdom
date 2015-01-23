/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.invitation;

import com.josue.kingdom.domain.entity.DomainPermission;
import com.josue.kingdom.domain.entity.ManagerMembership;
import com.josue.kingdom.invitation.entity.Invitation;
import com.josue.kingdom.testutils.ArquillianTestBase;
import com.josue.kingdom.testutils.InstanceHelper;
import java.util.List;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Josue
 */
@RunWith(Arquillian.class)
@Transactional(TransactionMode.DISABLED)
public class InvitationRepositoryIT {

    @Inject
    InvitationRepository repository;

    @Deployment
    @TargetsContainer("wildfly-managed")
    public static WebArchive createDeployment() {
        return ArquillianTestBase.createDefaultDeployment();
    }

    @Test
    public void testGetInvitations() {
        ManagerMembership membership = InstanceHelper.createFullManagerMembership(repository);
        DomainPermission permision = InstanceHelper.createPermission(membership.getDomain());
        repository.create(permision);

        Invitation invitation1 = InstanceHelper.createInvitation(membership.getDomain(), membership.getManager(), permision);
        repository.create(invitation1.getTargetManager());
        repository.create(invitation1);

        Invitation invitation2 = InstanceHelper.createInvitation(membership.getDomain(), membership.getManager(), permision);
        repository.create(invitation2.getTargetManager());
        repository.create(invitation2);

        List<Invitation> foundInvitations = repository.getInvitations(InstanceHelper.APP_ID, membership.getManager().getUuid(), 10, 0);
        assertEquals(2, foundInvitations.size());
        Assert.assertTrue(foundInvitations.contains(invitation1));
        Assert.assertTrue(foundInvitations.contains(invitation2));
    }

    @Test
    public void testGetInvitationByToken() {
        ManagerMembership manDomCred = InstanceHelper.createFullManagerMembership(repository);
        DomainPermission permision = InstanceHelper.createPermission(manDomCred.getDomain());
        repository.create(permision);

        Invitation invitation = InstanceHelper.createInvitation(manDomCred.getDomain(), manDomCred.getManager(), permision);
        repository.create(invitation.getTargetManager());
        repository.create(invitation);

        Invitation foundInvitation = repository.getInvitationByToken(InstanceHelper.APP_ID, invitation.getToken());
        assertNotNull(foundInvitation);
        assertEquals(invitation, foundInvitation);
    }

    @Test
    public void testGetInvitationsCount() {
        ManagerMembership manDomCred = InstanceHelper.createFullManagerMembership(repository);
        DomainPermission permision = InstanceHelper.createPermission(manDomCred.getDomain());
        repository.create(permision);

        Invitation invitation = InstanceHelper.createInvitation(manDomCred.getDomain(), manDomCred.getManager(), permision);
        repository.create(invitation.getTargetManager());
        repository.create(invitation);

        long count = repository.getInvitationsCount(InstanceHelper.APP_ID, manDomCred.getManager().getUuid());
        assertEquals(1, count);
    }

    @Test
    public void testGetInvitation() {
        ManagerMembership manDomCred = InstanceHelper.createFullManagerMembership(repository);
        DomainPermission permision = InstanceHelper.createPermission(manDomCred.getDomain());
        repository.create(permision);

        Invitation invitation = InstanceHelper.createInvitation(manDomCred.getDomain(), manDomCred.getManager(), permision);
        repository.create(invitation.getTargetManager());
        repository.create(invitation);

        Invitation foundInvitation = repository.getInvitation(InstanceHelper.APP_ID, manDomCred.getDomain().getUuid(), invitation.getTargetManager().getEmail());
        assertNotNull(foundInvitation);
        assertEquals(invitation, foundInvitation);
    }

}
