package com.josue.kingdom.util;

import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.sql.DataSource;
import liquibase.integration.cdi.CDILiquibaseConfig;
import liquibase.integration.cdi.annotations.LiquibaseType;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;

@ApplicationScoped
public class LiquibaseHelper {

    @Resource(lookup = "java:jboss/datasources/kingdomDS")
    private DataSource datasource;
    private static final Logger LOG = Logger.getLogger(LiquibaseHelper.class.getName());

    @Produces
    @LiquibaseType
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
    public ResourceAccessor create() {
        try {
            return new ClassLoaderResourceAccessor(getClass().getClassLoader());
        } catch (Exception e) {
            LOG.severe(e.getMessage());
        }
        return null;

    }
}
