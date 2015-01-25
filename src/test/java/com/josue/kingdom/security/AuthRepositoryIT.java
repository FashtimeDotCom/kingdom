/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.security;

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
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Josue
 */
@RunWith(Arquillian.class)
@Transactional(TransactionMode.DISABLED)
public class AuthRepositoryIT {

    @PersistenceContext
    EntityManager em;

    @Inject
    AuthRepository repository;

    @Deployment
    @TargetsContainer("wildfly-managed")
    public static WebArchive createDeployment() {
        return ArquillianTestBase.createDefaultDeployment();
    }

    public AuthRepositoryIT() {
    }

    @Test//TODO using email only.... update to handle both email ans username
    public void testGetManager() {
        Manager manager = InstanceHelper.createManager();
        repository.create(manager);

        Manager foundManager = repository.getManager(InstanceHelper.APP_ID, manager.getEmail(), manager.getPassword());
        assertNotNull(foundManager);
        assertEquals(manager, foundManager);
    }

    @Test
    public void testGetManagerMemberships() {
        ManagerMembership membership = InstanceHelper.createFullManagerMembership(repository);
        List<ManagerMembership> foundMemberships = repository.getManagerMemberships(InstanceHelper.APP_ID, membership.getManager().getUuid());
        assertEquals(1, foundMemberships.size());
        assertEquals(membership, foundMemberships.get(0));
    }

    @Test
    public void testGetAPICredentialByKey() {
        ManagerMembership membership = InstanceHelper.createFullManagerMembership(repository);
        APICredential credential = InstanceHelper.createAPICredential(membership);
        repository.create(credential);

        APICredential foundAPICredential = repository.getAPICredentialByKey(InstanceHelper.APP_ID, credential.getApiKey());
        assertNotNull(foundAPICredential);
        assertEquals(credential, foundAPICredential);

    }

}
