package com.josue.kingdom.testutils;

import com.josue.kingdom.util.LiquibaseHelper;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;
import javax.sql.DataSource;
import liquibase.integration.cdi.CDILiquibaseConfig;
import liquibase.integration.cdi.annotations.LiquibaseType;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;

@Specializes
public class LiquibaseTestHelper extends LiquibaseHelper {

    @Inject
    DatabaseHelper dbHelper;

    @Resource(lookup = "java:jboss/datasources/kingdom-testDS")
    private DataSource datasource;

    private static final Logger LOG = Logger.getLogger(LiquibaseTestHelper.class.getName());

    @Produces
    @LiquibaseType
    @Override
    public CDILiquibaseConfig createConfig() {
        try {
            CDILiquibaseConfig config = new CDILiquibaseConfig();
            config.setChangeLog("liquibase/changelog.xml");
            return config;
        } catch (Exception e) {
            LOG.severe(e.getMessage());
        }
        return null;
    }

    @Override
    @Produces
    @LiquibaseType
    public DataSource createDataSource() {
        try {
            return datasource;
        } catch (Exception e) {
            LOG.severe(e.getMessage());
        }
        return null;
    }

    @Produces
    @LiquibaseType
    @Override
    public ResourceAccessor create() {
        try {
            return new ClassLoaderResourceAccessor(LiquibaseTestHelper.class.getClassLoader());
        } catch (Exception e) {
            LOG.severe(e.getMessage());
        }
        return null;

    }

}
