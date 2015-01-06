/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager;

import com.josue.credential.manager.auth.role.Role;
import com.josue.credential.manager.testutils.ArquillianTestBase;
import com.josue.credential.manager.testutils.InstanceHelper;
import com.josue.credential.manager.testutils.Logged;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.enterprise.inject.Instance;
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
    Instance<JpaRepository> repositoryBeans;

    /*
     Resolve any JPARepository, just want to avoid annotations
     */
    private JpaRepository resolveJpaRepository() {
        Iterator<JpaRepository> iterator = repositoryBeans.iterator();
        while (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    @Deployment
    @TargetsContainer("wildfly-managed")
    public static WebArchive createDeployment() {
        return ArquillianTestBase.createDefaultDeployment();
    }

    private Role simpleCreate(JpaRepository repo) {
        Role role = InstanceHelper.createRole();
        repo.create(role);
        assertNotNull(role.getId());
        return role;
    }

    @Test
    public void testCreate() {
        JpaRepository repo = resolveJpaRepository();

        Role role = simpleCreate(repo);

        Role foundRole = repo.find(Role.class, role.getId());
        assertNotNull(foundRole);
    }

    @Test
    public void testEdit() {
        JpaRepository repo = resolveJpaRepository();

        Role role = simpleCreate(repo);

        //Fail prone, if the random methos generate an existing Role.level
        //For this test purpose its enough
        role.setLevel(new Random().nextInt(Integer.MAX_VALUE) + 1);
        Role editedEntity = repo.edit(role);
        assertEquals(role.getId(), editedEntity.getId());
    }

    @Test
    public void testRemove() {
        JpaRepository repo = resolveJpaRepository();

        Role role = simpleCreate(repo);

        repo.remove(role);
        Role foundRole = repo.find(Role.class, role.getId());
        assertNull(foundRole);
    }

    @Test
    public void testFind() {
        JpaRepository repo = resolveJpaRepository();

        Role role = simpleCreate(repo);
        Role foundRole = repo.find(Role.class, role.getId());
        assertEquals(role, foundRole);
    }

    @Test
    public void testFindAll() {
        JpaRepository repo = resolveJpaRepository();

        Role role1 = InstanceHelper.createRole();
        repo.create(role1);
        Role role2 = InstanceHelper.createRole();
        repo.create(role2);

        List<Role> foundRoles = repo.findAll(Role.class);
        assertTrue(foundRoles.size() >= 2);
        assertTrue(foundRoles.contains(role1));
        assertTrue(foundRoles.contains(role2));
    }

    @Test
    public void testFindRange() {
        JpaRepository repo = resolveJpaRepository();
        int total = 100;

        for (int i = 0; i < total; i++) {
            simpleCreate(repo);
        }

        //All the tests below are not appropriate, and its not precise too
        // because the test environment pre-load some data
        int limit = 50;
        int offset = 50;
        List<Role> foundRoles1 = repo.findRange(Role.class, limit, offset);
        assertTrue(foundRoles1.size() >= 50);

        limit = 100;
        offset = 50;
        List<Role> foundRoles2 = repo.findRange(Role.class, limit, offset);
        assertTrue(foundRoles2.size() >= 50);

        limit = 100;
        offset = 0;
        List<Role> foundRoles3 = repo.findRange(Role.class, limit, offset);
        assertTrue(foundRoles3.size() >= limit);

        limit = 1;
        offset = 99;
        List<Role> foundRoles4 = repo.findRange(Role.class, limit, offset);
        assertTrue(foundRoles4.size() >= limit);

        limit = 10;
        offset = 95;
        List<Role> foundRoles5 = repo.findRange(Role.class, limit, offset);
        assertTrue(foundRoles5.size() >= 5);

    }

    @Test
    public void testCount() {
        JpaRepository repo = resolveJpaRepository();
        int total = 15;

        for (int i = 0; i < total; i++) {
            simpleCreate(repo);
        }

        int count = repo.count(Role.class);
        assertTrue(count >= total);
    }

    @Test
    public void testExtractSingleResultFromList() {
        JpaRepository repo = resolveJpaRepository();
        int total = 5;

        Role someRole = null;
        for (int i = 0; i < total; i++) {
            someRole = simpleCreate(repo);
        }
        assertNotNull(someRole);

        TypedQuery<Role> query = repo.em.createQuery("SELECT ro from Role ro where ro.id = :id", Role.class);
        query.setParameter("id", someRole.getId());

        Role foundRole = repo.extractSingleResultFromList(query);
        assertEquals(someRole, foundRole);

    }

}
