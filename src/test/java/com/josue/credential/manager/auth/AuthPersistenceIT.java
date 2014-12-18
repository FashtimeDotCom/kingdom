/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.auth;

import com.josue.credential.manager.ArquillianTestBase;
import com.josue.credential.manager.InstanceHelper;
import com.josue.credential.manager.JpaRepository;
import com.josue.credential.manager.account.Manager;
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
    JpaRepository repository;

    @Test
    public void testRole() {
        Role role = InstanceHelper.createRole();
        repository.create(role);

        Role foundRole = repository.find(Role.class, role.getId());

        assertEquals(role, foundRole);
    }

    @Test
    public void testApiCredential() {
        Manager manager = InstanceHelper.createManager();
        repository.create(manager);

        Role role = InstanceHelper.createRole();
        repository.create(role);

        APICredential credential = new APICredential();
        credential.setApiKey(UUID.randomUUID().toString());
        credential.setRole(role);
        credential.setStatus(CredentialStatus.ACTIVE);
        credential.setManager(manager);

        repository.create(credential);

        APICredential foundCredential = repository.find(APICredential.class, credential.getUuid());
        assertEquals(credential, foundCredential);
    }

    @Test
    public void testManagerCredential() {
        Manager manager = InstanceHelper.createManager();
        repository.create(manager);

        Role role = InstanceHelper.createRole();
        repository.create(role);

        ManagerCredential credential = new ManagerCredential();
        credential.setLogin("user.login");
        credential.setManager(manager);
        credential.setPassword("manager-psw-123");
        credential.setRole(role);
        credential.setStatus(CredentialStatus.ACTIVE);

        repository.create(credential);

        ManagerCredential foundCredential = repository.find(ManagerCredential.class, credential.getUuid());
        assertEquals(credential, foundCredential);
    }

}
