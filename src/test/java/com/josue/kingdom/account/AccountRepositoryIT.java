/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.account;

import com.josue.kingdom.account.entity.Manager;
import com.josue.kingdom.credential.entity.ManagerCredential;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Josue
 */
@RunWith(Arquillian.class)
@Transactional(TransactionMode.ROLLBACK)
public class AccountRepositoryIT {

    @Inject
    AccountRepository repository;

    private static final Integer DEFAULT_LIMIT = 100;
    private static final Integer DEFAULT_OFFSET = 0;

    @Deployment
    @TargetsContainer("wildfly-managed")
    public static WebArchive createDeployment() {
        return ArquillianTestBase.createDefaultDeployment();
    }

    @Test
    public void testGetManagers() {
        Manager man1 = InstanceHelper.createManager();
        repository.create(man1);
        Manager man2 = InstanceHelper.createManager();
        repository.create(man2);

        List<Manager> foundManagers = repository.getManagers(DEFAULT_LIMIT, DEFAULT_OFFSET);
        assertEquals(4, foundManagers.size()); // 2 managers created + 2 managers from liquibase test data
        assertTrue(foundManagers.contains(man1));
        assertTrue(foundManagers.contains(man2));
    }

    @Test
    public void testGetManagerByEmail() {
        Manager man1 = InstanceHelper.createManager();
        repository.create(man1);

        Manager foundManager = repository.getManagerByEmail(man1.getEmail());
        assertEquals(man1, foundManager);
    }

    @Test
    public void testGetManagerByLogin() {
        Manager man1 = InstanceHelper.createManager();
        repository.create(man1);
        ManagerCredential manCred = InstanceHelper.createManagerCredential(man1);
        repository.create(manCred);

        Manager foundManager = repository.getManagerByLogin(manCred.getLogin());
        assertEquals(man1, foundManager);
    }

    @Test
    public void testGetManagerByCredential() {
        Manager man1 = InstanceHelper.createManager();
        repository.create(man1);
        ManagerCredential manCred = InstanceHelper.createManagerCredential(man1);
        repository.create(manCred);

        Manager foundManager = repository.getManagerByCredential(manCred.getUuid());
        assertEquals(man1, foundManager);
    }

}
