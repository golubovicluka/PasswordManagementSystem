package com.golubovicluka.passwordmanagementsystem.service;

import com.golubovicluka.passwordmanagementsystem.dao.TestDatabaseConnection;
import com.golubovicluka.passwordmanagementsystem.dao.UserDAO;
import com.golubovicluka.passwordmanagementsystem.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private AuthService authService;
    private static final String VALID_USERNAME = "testUser";
    private static final String VALID_PASSWORD = "TestPassword123!";
    private static final String INVALID_PASSWORD = "wrongpass";
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
        }

        UserDAO userDAO = new UserDAO();
        authService = new AuthService(userDAO);
    }

    @AfterEach
    void tearDown() {
        try (Connection conn = testConnection.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS users");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void validateUser_WithValidCredentials_ShouldReturnUser() throws ExecutionException, InterruptedException {
        authService.registerUser(VALID_USERNAME, VALID_PASSWORD);
        User result = authService.validateUser(VALID_USERNAME, VALID_PASSWORD).get();

        assertNotNull(result);
        assertEquals(VALID_USERNAME, result.getUsername());
    }

    @Test
    void validateUser_WithInvalidPassword_ShouldReturnNull() throws ExecutionException, InterruptedException {
        authService.registerUser(VALID_USERNAME, VALID_PASSWORD);
        User result = authService.validateUser(VALID_USERNAME, INVALID_PASSWORD).get();

        assertNull(result);
    }

    @Test
    void validateUser_WithNonexistentUsername_ShouldReturnNull() throws ExecutionException, InterruptedException {
        User result = authService.validateUser("nonexistent", VALID_PASSWORD).get();
        assertNull(result);
    }

    @Test
    void registerUser_WithNewUsername_ShouldReturnTrue() {
        boolean result = authService.registerUser(VALID_USERNAME, VALID_PASSWORD);
        assertTrue(result);
    }

    @Test
    void registerUser_WithExistingUsername_ShouldReturnFalse() {
        authService.registerUser(VALID_USERNAME, VALID_PASSWORD);
        boolean result = authService.registerUser(VALID_USERNAME, VALID_PASSWORD);
        assertFalse(result);
    }

    @Test
    void hashPassword_ShouldReturnValidBCryptHash() {
        String hash = authService.hashPassword(VALID_PASSWORD);

        assertNotNull(hash);
        assertTrue(hash.startsWith("$2a$"));
        assertTrue(BCrypt.checkpw(VALID_PASSWORD, hash));
    }
}