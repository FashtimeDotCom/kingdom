/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager;

import com.josue.credential.manager.account.Manager;
import com.josue.credential.manager.account.ManagerInvitation;
import com.josue.credential.manager.account.ManagerInvitationStatus;
import com.josue.credential.manager.auth.APICredential;
import com.josue.credential.manager.auth.CredentialStatus;
import com.josue.credential.manager.auth.ManagerCredential;
import com.josue.credential.manager.auth.Role;
import com.josue.credential.manager.liquibase.LiquibaseHelper;
import com.josue.credential.manager.liquibase.LiquibaseTestHelper;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.jboss.shrinkwrap.resolver.api.maven.Maven.resolver;
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
public class AccountPersistenceTest {

    @Deployment
    @TargetsContainer("wildfly-managed")
    public static WebArchive createDeployment() {

//        File[] libs = Maven.resolver().loadPomFromFile("pom.xml").importRuntimeAndTestDependencies().resolve().withTransitivity().asFile();
        File[] dependecies = resolver()
                .loadPomFromFile("pom.xml")
                .resolve("org.apache.shiro:shiro-core:1.2.3",
                        "org.apache.shiro:shiro-web:1.2.3",
                        "org.liquibase:liquibase-cdi:3.1.1")
                .withTransitivity().asFile();

        WebArchive war = ShrinkWrap
                .create(WebArchive.class, "credential-manager-test.war")
                .addPackages(true, Filters.exclude(LiquibaseHelper.class), "com.josue.credential.manager")
                .addClass(LiquibaseTestHelper.class)
                .addAsResource("liquibase")
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsLibraries(dependecies);

        return war;
    }

    @PersistenceContext
    EntityManager em;

    @Inject
    JpaRepository repository;

    @Test
    public void testCreateManager() {
        Manager manager = createManager();
        repository.create(manager);

        Manager foundManager = repository.find(Manager.class, manager.getUuid());
        assertEquals(manager, foundManager);
    }

    @Test
    public void testManagerInvitation() {
        ManagerInvitation invitation = new ManagerInvitation();

        Manager authorManager = createManager();
        repository.create(authorManager);

        invitation.setAuthorManager(authorManager);
        invitation.setTargetEmail("eduardo@gmail.com");
        invitation.setStatus(ManagerInvitationStatus.CREATED);
        invitation.setToken(UUID.randomUUID().toString());

        invitation.setValidUntil(mysqlMilliSafeTimestamp());

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

    private Date mysqlMilliSafeTimestamp() {

        //TIP: http://www.coderanch.com/t/530003/java/java/Comparing-Date-Timestamp-unexpected-result
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, 2);
        cal.set(Calendar.MILLISECOND, 0); //reset milliseconds

        return cal.getTime();
    }

    @Test
    public void testApiCredential() {
        Manager manager = createManager();
        repository.create(manager);

        Role role = createRole();
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
        Manager manager = createManager();
        repository.create(manager);

        Role role = createRole();
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

    private Role createRole() {
        Role role = new Role();
        role.setDescription("Role description");
        role.setLevel(new Random().nextInt());
        role.setName("ADMIN");

        return role;
    }

    private Manager createManager() {
        Manager manager = new Manager();
        manager.setEmail("josue@gmail.com");
        manager.setFirstName("josue");
        manager.setLastName("Eduardo");

        return manager;
    }

}
