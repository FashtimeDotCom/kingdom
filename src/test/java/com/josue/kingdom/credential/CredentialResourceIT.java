/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.credential;

import com.josue.kingdom.testutils.ArquillianTestBase;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Josue
 */
@RunAsClient
@RunWith(Arquillian.class)
public class CredentialResourceIT {

    @Deployment
    @TargetsContainer("wildfly-managed")
    public static WebArchive createDeployment() {
        return ArquillianTestBase.createDefaultDeployment();
    }

    @Test
    public void testGetCurrentCredential() {
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetAccount() {
        fail("The test case is a prototype.");
    }

    @Test
    public void testPasswordReset() {
        fail("The test case is a prototype.");
    }

    @Test
    public void testLoginRecover() {
        fail("The test case is a prototype.");
    }

    @Test
    public void testCreateAccount() {
        fail("The test case is a prototype.");
    }

}
