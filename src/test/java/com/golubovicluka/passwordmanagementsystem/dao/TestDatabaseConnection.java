package com.golubovicluka.passwordmanagementsystem.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A special database connection implementation for tests.
 * This class provides an H2 in-memory database connection for unit tests.
 */
public class TestDatabaseConnection extends DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(TestDatabaseConnection.class);
    private static TestDatabaseConnection instance;

    /**
     * Private constructor for singleton pattern
     */
    private TestDatabaseConnection() {
        super(true);
    }

    /**
     * Gets the singleton instance of the TestDatabaseConnection.
     *
     * @return The singleton instance
     */
    public static synchronized TestDatabaseConnection getInstance() {
        if (instance == null) {
            instance = new TestDatabaseConnection();
        }
        return instance;
    }

    /**
     * Setup the TestDatabaseConnection as the main database connection
     * for the current test run.
     */
    public static void setupForTesting() {
        TestDatabaseConnection testInstance = getInstance();
        DatabaseConnection.setInstance(testInstance);
    }

    /**
     * Gets a connection to the in-memory H2 test database
     * 
     * @return A connection to the test database
     * @throws SQLException If a database access error occurs
     */
    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
    }
}