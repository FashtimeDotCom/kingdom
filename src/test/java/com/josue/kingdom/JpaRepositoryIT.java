/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom;

import com.josue.kingdom.application.ApplicationRepository;
import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.domain.entity.Domain;
import com.josue.kingdom.domain.entity.DomainPermission;
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
@Transactional(TransactionMode.ROLLBACK)
public class JpaRepositoryIT {

    @PersistenceContext
    EntityManager em;

    @Inject
    ApplicationRepository jpaRepository;

    @Deployment
    @TargetsContainer("wildfly-managed")
    public static WebArchive createDeployment() {
        return ArquillianTestBase.createDefaultDeployment();
    }

    private DomainPermission createDomainPermission() {
        Manager manager = InstanceHelper.createManager();
        jpaRepository.create(manager);
        Domain domain = InstanceHelper.createDomain(manager);
        jpaRepository.create(domain);

        DomainPermission permission = InstanceHelper.createPermission(domain);
        jpaRepository.create(permission);
        assertNotNull(permission.getUuid());
        return permission;
    }

    @Test
    public void testCreate() {
        DomainPermission permission = createDomainPermission();

        DomainPermission foundPermission = jpaRepository.find(DomainPermission.class, InstanceHelper.APP_ID, permission.getUuid());
        assertNotNull(foundPermission);
    }

    @Test
    public void testUpdate() {

        DomainPermission permission = createDomainPermission();

        //Fail prone, if the random methos generate an existing Permission.level
        //For this test purpose its enough
        permission.setLevel(new Random().nextInt(Integer.MAX_VALUE) + 1);
        DomainPermission editedEntity = jpaRepository.update(permission);
        assertEquals(permission.getUuid(), editedEntity.getUuid());
    }

    @Test
    public void testDelete() {

        DomainPermission permission = createDomainPermission();

        jpaRepository.delete(permission);
        DomainPermission foundPermission = jpaRepository.find(DomainPermission.class, InstanceHelper.APP_ID, permission.getUuid());
        assertNull(foundPermission);
    }

    @Test
    public void testFind() {

        DomainPermission permission = createDomainPermission();
        DomainPermission foundPermission = jpaRepository.find(DomainPermission.class, InstanceHelper.APP_ID, permission.getUuid());
        assertEquals(permission, foundPermission);
    }

    @Test
    public void testCount() {
        int total = 15;

        for (int i = 0; i < total; i++) {
            createDomainPermission();
        }

        Long count = jpaRepository.count(DomainPermission.class, InstanceHelper.APP_ID);
        assertTrue(count >= total);
    }

    @Test
    public void testExtractSingleResultFromList() {
        int total = 5;

        DomainPermission somePermission = null;
        for (int i = 0; i < total; i++) {
            somePermission = createDomainPermission();
        }
        assertNotNull(somePermission);

        TypedQuery<DomainPermission> query = em.createQuery("SELECT ro from DomainPermission ro where ro.uuid = :uuid", DomainPermission.class);
        query.setParameter("uuid", somePermission.getUuid());
        List<DomainPermission> resultList = query.getResultList();
        DomainPermission foundPermission = jpaRepository.extractSingleResultFromList(resultList);
        assertEquals(somePermission, foundPermission);
    }

}
