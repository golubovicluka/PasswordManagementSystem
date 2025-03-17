package com.golubovicluka.passwordmanagementsystem.dao;

import com.golubovicluka.passwordmanagementsystem.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {

    private UserDAO userDAO;
    private TestDatabaseConnection testConnection;

    @BeforeEach
    void setUp() throws SQLException {
        testConnection = TestDatabaseConnection.getInstance();
        TestDatabaseConnection.setupForTesting();

        try (Connection conn = testConnection.getConnection();
                Statement stmt = conn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS users");
            stmt.execute("CREATE TABLE users ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "username VARCHAR(50) UNIQUE NOT NULL,"
                    + "password_hash VARCHAR(255) NOT NULL,"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ")");

            stmt.execute("INSERT INTO users (username, password_hash, created_at) VALUES "
                    + "('testUser', 'hashedPassword', CURRENT_TIMESTAMP)");
        }

        userDAO = new UserDAO();
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (Connection conn = testConnection.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS users");
        }
    }

    @Test
    void findByUsername_WhenUserExists_ShouldReturnUser() {
        Optional<User> result = userDAO.findByUsername("testUser");

        assertTrue(result.isPresent());
        assertEquals("testUser", result.get().getUsername());
        assertEquals("hashedPassword", result.get().getPasswordHash());
    }

    @Test
    void findByUsername_WhenUserDoesNotExist_ShouldReturnEmpty() {
        Optional<User> result = userDAO.findByUsername("nonexistent");

        assertTrue(result.isEmpty());
    }

    @Test
    void createUser_WhenSuccessful_ShouldReturnTrue() {
        boolean result = userDAO.createUser("newUser", "newHashedPassword");

        assertTrue(result);

        Optional<User> createdUser = userDAO.findByUsername("newUser");
        assertTrue(createdUser.isPresent());
        assertEquals("newHashedPassword", createdUser.get().getPasswordHash());
    }

    @Test
    void createUser_WhenDuplicateUsername_ShouldReturnFalse() {
        boolean result = userDAO.createUser("testUser", "anotherPassword");

        assertFalse(result);
    }
}