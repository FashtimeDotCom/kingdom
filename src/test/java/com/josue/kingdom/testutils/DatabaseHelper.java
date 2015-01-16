/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.testutils;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;

/**
 *
 * @author Josue
 */
@ApplicationScoped
public class DatabaseHelper {

    @Resource(lookup = "java:jboss/datasources/kingdom-testDS")
    private DataSource datasource;
    private static final Logger LOG = Logger.getLogger(LiquibaseTestHelper.class.getName());

    private final String REMOVE_MANAGER_DOMAIN_CREDENTIAL = "DELETE FROM MANAGER_DOMAIN_CREDENTIAL";
    private final String REMOVE_API_DOMAIN_CREDENTIAL = "DELETE FROM API_DOMAIN_CREDENTIAL";
    private final String REMOVE_MANAGER_CREDENTIAL = "DELETE FROM MANAGER_CREDENTIAL";
    private final String REMOVE_API_CREDENTIAL = "DELETE FROM API_CREDENTIAL";
    private final String REMOVE_INVITATION = "DELETE FROM INVITATION";
    private final String REMOVE_MANAGER = "DELETE FROM MANAGER";
    private final String REMOVE_DOMAIN_PERMISSION = "DELETE FROM DOMAIN_PERMISSION";
    private final String REMOVE_DOMAIN = "DELETE FROM DOMAIN";

    private final String DISABLE_CONSTRAINTS = "SET FOREIGN_KEY_CHECKS=0;";
    private final String ENABLE_CONSTRAINT = "SET FOREIGN_KEY_CHECKS=1;";

    //TODO check full DB deletion... FK constraint error
//    @PostConstruct
    public void cleanDatabase() {
        try {
            LOG.log(Level.INFO, "### REMOVING DATABASE DATA ###");
            try (Statement statement = datasource.getConnection().createStatement()) {
                statement.addBatch(DISABLE_CONSTRAINTS);

                statement.addBatch(REMOVE_MANAGER_DOMAIN_CREDENTIAL);
                statement.addBatch(REMOVE_API_DOMAIN_CREDENTIAL);
                statement.addBatch(REMOVE_MANAGER_CREDENTIAL);
                statement.addBatch(REMOVE_API_CREDENTIAL);
                statement.addBatch(REMOVE_INVITATION);
                statement.addBatch(REMOVE_MANAGER);
                statement.addBatch(REMOVE_DOMAIN_PERMISSION);
                statement.addBatch(REMOVE_DOMAIN);

                statement.addBatch(ENABLE_CONSTRAINT);

                statement.executeBatch();
            }

        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        LOG.log(Level.INFO, "### DATABASE DATA SUCESSFUL REMOVED ###");
    }
}
