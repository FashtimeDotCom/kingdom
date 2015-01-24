/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom;

import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.domain.entity.Domain;
import com.josue.kingdom.domain.entity.DomainPermission;
import com.josue.kingdom.security.AuthRepository;
import com.josue.kingdom.testutils.ArquillianTestBase;
import com.josue.kingdom.testutils.InstanceHelper;
import java.util.List;
import java.util.Random;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Josue
 */
@RunWith(Arquillian.class)
@Transactional(TransactionMode.DISABLED)
public class JpaRepositoryIT {

    @PersistenceContext
    EntityManager em;

    @Inject
    AuthRepository repo;

    @Deployment
    @TargetsContainer("wildfly-managed")
    public static WebArchive createDeployment() {
        return ArquillianTestBase.createDefaultDeployment();
    }

    private DomainPermission simpleCreate(JpaRepository repo) {
        Manager manager = InstanceHelper.createManager();
        repo.create(manager);
        Domain domain = InstanceHelper.createDomain(manager);
        repo.create(domain);

        DomainPermission permission = InstanceHelper.createPermission(domain);
        repo.create(permission);
        assertNotNull(permission.getUuid());
        return permission;
    }

    @Test
    public void testCreate() {
        DomainPermission permission = simpleCreate(repo);

        DomainPermission foundPermission = repo.find(DomainPermission.class, InstanceHelper.APP_ID, permission.getUuid());
        assertNotNull(foundPermission);
    }

    @Test
    public void testUpdate() {

        DomainPermission permission = simpleCreate(repo);

        //Fail prone, if the random methos generate an existing Permission.level
        //For this test purpose its enough
        permission.setLevel(new Random().nextInt(Integer.MAX_VALUE) + 1);
        DomainPermission editedEntity = repo.update(permission);
        assertEquals(permission.getUuid(), editedEntity.getUuid());
    }

    @Test
    public void testDelete() {

        DomainPermission permission = simpleCreate(repo);

        repo.delete(permission);
        DomainPermission foundPermission = repo.find(DomainPermission.class, InstanceHelper.APP_ID, permission.getUuid());
        assertNull(foundPermission);
    }

    @Test
    public void testFind() {

        DomainPermission permission = simpleCreate(repo);
        DomainPermission foundPermission = repo.find(DomainPermission.class, InstanceHelper.APP_ID, permission.getUuid());
        assertEquals(permission, foundPermission);
    }

    @Test
    public void testCount() {
        int total = 15;

        for (int i = 0; i < total; i++) {
            simpleCreate(repo);
        }

        Long count = repo.count(DomainPermission.class, InstanceHelper.APP_ID);
        assertTrue(count >= total);
    }

    @Test
    public void testExtractSingleResultFromList() {
        int total = 5;

        DomainPermission somePermission = null;
        for (int i = 0; i < total; i++) {
            somePermission = simpleCreate(repo);
        }
        assertNotNull(somePermission);

        TypedQuery<DomainPermission> query = em.createQuery("SELECT ro from DomainPermission ro where ro.uuid = :uuid", DomainPermission.class);
        query.setParameter("uuid", somePermission.getUuid());
        List<DomainPermission> resultList = query.getResultList();
        DomainPermission foundPermission = repo.extractSingleResultFromList(resultList);
        assertEquals(somePermission, foundPermission);
    }

}
