/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager;

import com.josue.credential.manager.liquibase.LiquibaseHelper;
import com.josue.credential.manager.liquibase.LiquibaseTestHelper;
import java.io.File;
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
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Josue
 */
@RunWith(Arquillian.class)
@Transactional(TransactionMode.ROLLBACK)
public class NewClassTest {

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

    @Test
    public void testSomeMethod() {

    }

}
