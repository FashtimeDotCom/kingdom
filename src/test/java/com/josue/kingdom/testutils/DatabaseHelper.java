/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.testutils;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;

/**
 *
 * @author Josue
 */
@ApplicationScoped
public class DatabaseHelper {

    @Resource(lookup = "java:jboss/datasources/kingdom-testDS")
    private DataSource datasource;
    private static final Logger LOG = Logger.getLogger(DatabaseHelper.class.getName());

    public void cleanDatabase() {

        LOG.log(Level.INFO, "### REMOVING DATABASE DATA ###");
        try {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(datasource.getConnection()));
            Liquibase liquibase = new Liquibase("initial-test-data.xml", new FileSystemResourceAccessor(), database);
            liquibase.update(new Contexts());
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DatabaseException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LiquibaseException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        LOG.log(Level.INFO, "### DATABASE DATA SUCESSFUL REMOVED ###");
    }
}
