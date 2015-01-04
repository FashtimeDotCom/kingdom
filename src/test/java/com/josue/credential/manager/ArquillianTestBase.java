/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager;

import com.josue.credential.manager.liquibase.LiquibaseHelper;
import com.josue.credential.manager.liquibase.LiquibaseTestHelper;
import java.io.File;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.jboss.shrinkwrap.resolver.api.maven.Maven.resolver;

/**
 *
 * @author Josue
 */
public abstract class ArquillianTestBase {

    public static WebArchive createDefaultDeployment() {

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
                //                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource("test-ds.xml") //generic test DS
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/beans.xml"))
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/shiro.ini"))
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/web.xml"))
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/jboss-deployment-structure.xml"))
                .addAsLibraries(dependecies);

        return war;
    }
}
