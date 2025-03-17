package com.golubovicluka.passwordmanagementsystem.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Singleton connection pool manager using HikariCP.
 * This class provides efficient database connection management with connection
 * pooling.
 */
public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private static DatabaseConnection instance;
    private HikariDataSource dataSource;

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/passwordmanagement";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";
    private static final int MAX_POOL_SIZE = 10;

    /**
     * Private constructor to prevent instantiation from outside.
     * Initializes the connection pool with the specified configuration.
     */
    private DatabaseConnection() {
        initializeDataSource();
    }

    /**
     * Package-private constructor for testing purposes.
     * This allows tests to create a mock instance without initializing the real
     * data source.
     * 
     * @param forTesting if true, skips initializing the real data source
     */
    DatabaseConnection(boolean forTesting) {
        if (!forTesting) {
            initializeDataSource();
        }
    }

    /**
     * Initializes the HikariCP data source with configured settings.
     */
    private void initializeDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(JDBC_URL);
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);
        config.setMaximumPoolSize(MAX_POOL_SIZE);

        config.setMinimumIdle(5);
        config.setIdleTimeout(30000);
        config.setPoolName("PasswordManagerConnectionPool");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        try {
            dataSource = new HikariDataSource(config);
            logger.info("HikariCP connection pool initialized successfully");
        } catch (Exception e) {
            logger.error("Error initializing connection pool", e);
            throw new RuntimeException("Failed to initialize database connection pool", e);
        }
    }

    /**
     * Gets the singleton instance of the DatabaseConnection.
     *
     * @return The singleton instance
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * For testing purposes only: sets a custom instance
     * 
     * @param customInstance the custom instance to use
     */
    static void setInstance(DatabaseConnection customInstance) {
        instance = customInstance;
    }

    /**
     * Gets a connection from the connection pool.
     *
     * @return A database connection
     * @throws SQLException If a database access error occurs
     */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Closes the connection pool when the application is shutting down.
     * This should be called when the application is terminating.
     */
    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Connection pool closed");
        }
    }
}
