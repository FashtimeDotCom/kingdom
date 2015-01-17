/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.testutils;

import com.josue.kingdom.util.LiquibaseHelper;
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
                        "org.liquibase:liquibase-cdi:3.1.1",
                        "com.sun.jersey:jersey-client:1.18.3") // ???... Container also needs the client dependencies
                .withTransitivity().asFile();

        WebArchive war = ShrinkWrap
                .create(WebArchive.class, "kingdom-test.war")
                .addPackages(true, Filters.exclude(LiquibaseHelper.class), "com.josue.kingdom")
                .addClass(LiquibaseTestHelper.class)
                .addAsResource("liquibase")
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsResource("liquibase")
                //                .addAsResource("META-INF/services/javax.enterprise.inject.spi.Extension")
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
