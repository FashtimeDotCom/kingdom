/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.application;

import com.josue.kingdom.application.entity.Application;
import com.josue.kingdom.application.entity.ApplicationConfig;
import com.josue.kingdom.testutils.ArquillianTestBase;
import com.josue.kingdom.testutils.InstanceHelper;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Josue
 */
@RunWith(Arquillian.class)
@Transactional(TransactionMode.ROLLBACK)
public class ApplicationRepositoryIT {

    @Inject
    ApplicationRepository repository;

    private static final Integer DEFAULT_LIMIT = 100;
    private static final Integer DEFAULT_OFFSET = 0;

    @Deployment
    @TargetsContainer("wildfly-managed")
    public static WebArchive createDeployment() {
        return ArquillianTestBase.createDefaultDeployment();
    }

    @Test
    public void testGetApplication() {
        Application application = InstanceHelper.createApplication();
        repository.create(application);
        assertNotNull(application.getUuid());
        Application foundApplication = repository.getApplication(application.getUuid());
        assertEquals(application, foundApplication);
    }

    @Test
    public void testGetApplicationConfig() {
        Application application = InstanceHelper.createApplication();
        repository.create(application);

        ApplicationConfig config = InstanceHelper.createApplicationConfig(application);
        repository.create(config);
        ApplicationConfig foundConfig = repository.getApplicationConfig(config.getApplication().getUuid());
        assertEquals(config, foundConfig);
    }

}
