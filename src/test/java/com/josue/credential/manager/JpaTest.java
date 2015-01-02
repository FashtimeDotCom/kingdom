///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.josue.credential.manager;
//
//import com.josue.credential.manager.business.account.AccountRepository;
//import com.josue.credential.manager.auth.manager.Manager;
//import com.josue.credential.manager.auth.credential.ManagerCredential;
//import com.josue.credential.manager.liquibase.LiquibaseHelper;
//import java.io.File;
//import javax.inject.Inject;
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import org.jboss.arquillian.container.test.api.Deployment;
//import org.jboss.arquillian.container.test.api.TargetsContainer;
//import org.jboss.arquillian.junit.Arquillian;
//import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
//import org.jboss.arquillian.transaction.api.annotation.Transactional;
//import org.jboss.shrinkwrap.api.Filters;
//import org.jboss.shrinkwrap.api.ShrinkWrap;
//import org.jboss.shrinkwrap.api.asset.EmptyAsset;
//import org.jboss.shrinkwrap.api.spec.WebArchive;
//import static org.jboss.shrinkwrap.resolver.api.maven.Maven.resolver;
//import static org.junit.Assert.assertEquals;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
///**
// *
// * @author Josue
// */
//@RunWith(Arquillian.class)
//@Transactional(TransactionMode.COMMIT)
//public class JpaTest {
//
//    @Deployment
//    @TargetsContainer("wildfly-managed")
//    public static WebArchive createDeployment() {
//
//        File[] dependecies = resolver()
//                .loadPomFromFile("pom.xml")
//                .resolve("org.apache.shiro:shiro-core:1.2.3",
//                        "org.apache.shiro:shiro-web:1.2.3")
//                .withTransitivity().asFile();
//
//        WebArchive war = ShrinkWrap
//                .create(WebArchive.class, "credential-manager-test.war")
//                .addPackages(true, Filters.exclude(LiquibaseHelper.class), "com.josue.credential.manager")
//                .addAsResource("liquibase")
//                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
//                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
//                .addAsLibraries(dependecies);
//
//        return war;
//    }
//
//    @PersistenceContext
//    EntityManager em;
//
//    @Inject
//    AccountRepository repository;
//
//    @Test
//    public void testCreateManager() {
//        Manager manager = InstanceHelper.createManager();
//        ManagerCredential credential = InstanceHelper.createManagerCredential(manager);
//        repository.create(credential);
//
//        Manager foundManager = repository.find(Manager.class, manager.getUuid());
//        assertEquals(manager, foundManager);
//    }
//
//}
