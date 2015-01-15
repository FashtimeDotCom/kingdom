/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom;

import com.josue.kingdom.credential.AuthRepository;
import com.josue.kingdom.domain.entity.DomainRole;
import com.josue.kingdom.testutils.ArquillianTestBase;
import com.josue.kingdom.testutils.InstanceHelper;
import com.josue.kingdom.testutils.Logged;
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
@Logged
@RunWith(Arquillian.class)
@Transactional(TransactionMode.ROLLBACK)
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

    private DomainRole simpleCreate(JpaRepository repo) {
        DomainRole role = InstanceHelper.createRole();
        repo.create(role);
        assertNotNull(role.getUuid());
        return role;
    }

    @Test
    public void testCreate() {

        DomainRole role = simpleCreate(repo);

        DomainRole foundRole = repo.find(DomainRole.class, role.getUuid());
        assertNotNull(foundRole);
    }

    @Test
    public void testUpdate() {

        DomainRole role = simpleCreate(repo);

        //Fail prone, if the random methos generate an existing Role.level
        //For this test purpose its enough
        role.setLevel(new Random().nextInt(Integer.MAX_VALUE) + 1);
        DomainRole editedEntity = repo.update(role);
        assertEquals(role.getUuid(), editedEntity.getUuid());
    }

    @Test
    public void testDelete() {

        DomainRole role = simpleCreate(repo);

        repo.delete(role);
        DomainRole foundRole = repo.find(DomainRole.class, role.getUuid());
        assertNull(foundRole);
    }

    @Test
    public void testFind() {

        DomainRole role = simpleCreate(repo);
        DomainRole foundRole = repo.find(DomainRole.class, role.getUuid());
        assertEquals(role, foundRole);
    }

    @Test
    public void testFindAll() {

        DomainRole role1 = InstanceHelper.createRole();
        repo.create(role1);
        DomainRole role2 = InstanceHelper.createRole();
        repo.create(role2);

        List<DomainRole> foundRoles = repo.findAll(DomainRole.class);
        assertTrue(foundRoles.size() >= 2);
        assertTrue(foundRoles.contains(role1));
        assertTrue(foundRoles.contains(role2));
    }

    @Test
    public void testFindRange() {
        int total = 100;

        for (int i = 0; i < total; i++) {
            simpleCreate(repo);
        }

        //All the tests below are not appropriate, and its not precise too
        // because the test environment pre-load some data
        int limit = 50;
        int offset = 50;
        List<DomainRole> foundRoles1 = repo.findRange(DomainRole.class, limit, offset);
        assertTrue(foundRoles1.size() >= 50);

        limit = 100;
        offset = 50;
        List<DomainRole> foundRoles2 = repo.findRange(DomainRole.class, limit, offset);
        assertTrue(foundRoles2.size() >= 50);

        limit = 100;
        offset = 0;
        List<DomainRole> foundRoles3 = repo.findRange(DomainRole.class, limit, offset);
        assertTrue(foundRoles3.size() >= limit);

        limit = 1;
        offset = 99;
        List<DomainRole> foundRoles4 = repo.findRange(DomainRole.class, limit, offset);
        assertTrue(foundRoles4.size() >= limit);

        limit = 10;
        offset = 95;
        List<DomainRole> foundRoles5 = repo.findRange(DomainRole.class, limit, offset);
        assertTrue(foundRoles5.size() >= 5);

    }

    @Test
    public void testCount() {
        int total = 15;

        for (int i = 0; i < total; i++) {
            simpleCreate(repo);
        }

        Long count = repo.count(DomainRole.class);
        assertTrue(count >= total);
    }

    @Test
    public void testExtractSingleResultFromList() {
        int total = 5;

        DomainRole someRole = null;
        for (int i = 0; i < total; i++) {
            someRole = simpleCreate(repo);
        }
        assertNotNull(someRole);

        TypedQuery<DomainRole> query = em.createQuery("SELECT ro from Role ro where ro.uuid = :uuid", DomainRole.class);
        query.setParameter("uuid", someRole.getUuid());
        List<DomainRole> resultList = query.getResultList();
        DomainRole foundRole = repo.extractSingleResultFromList(resultList);
        assertEquals(someRole, foundRole);
    }

}
