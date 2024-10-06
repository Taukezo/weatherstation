package org.aulich.utilities;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aulich.model.Configuration;
import org.aulich.model.ConfigurationModel;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Provides a DB-Connectionpool for the application
 * to a RDBMS.
 */
public class AppDataBaseConnectionPool {
    private static final Logger LOG = LogManager.getLogger(AppDataBaseConnectionPool.class);
    private static AppDataBaseConnectionPool instance;
    private static BasicDataSource ds;

    private AppDataBaseConnectionPool() {
    }

    public static AppDataBaseConnectionPool getInstance() {
        if (instance == null) {
            instance = new AppDataBaseConnectionPool();
        }
        return instance;
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public void connectToDataBase() throws SQLException {
        if (ds == null || ds.isClosed()) {
            ds = new BasicDataSource();
            ConfigurationModel cfgM = Configuration.getConfiguration().getConfigurationModel();
            ds.setDriverClassName(cfgM.getDataSourceModel().getDriverClassName());
            ds.setUrl(cfgM.getDataSourceModel().getUrl());
            ds.setUsername(cfgM.getDataSourceModel().getUserName());
            ds.setPassword(cfgM.getDataSourceModel().getPassword());
            ds.start();
        }
    }
}
