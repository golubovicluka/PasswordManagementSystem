package com.golubovicluka.passwordmanagementsystem.dao;

import com.golubovicluka.passwordmanagementsystem.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDAOTest {

    @Mock
    private DatabaseConnection databaseConnection;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private UserDAO userDAO;

    @BeforeEach
    void setUp() throws SQLException {
        userDAO = new UserDAO(databaseConnection);
        when(databaseConnection.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    @Test
    void findByUsername_WhenUserExists_ShouldReturnUser() throws SQLException {
        String username = "testUser";
        String passwordHash = "hashedPassword";

        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getString("username")).thenReturn(username);
        when(resultSet.getString("password_hash")).thenReturn(passwordHash);
        when(resultSet.getTimestamp("created_at")).thenReturn(java.sql.Timestamp.valueOf(LocalDateTime.now()));

        Optional<User> result = userDAO.findByUsername(username);

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        assertEquals(passwordHash, result.get().getPasswordHash());
    }

    @Test
    void findByUsername_WhenUserDoesNotExist_ShouldReturnEmpty() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        Optional<User> result = userDAO.findByUsername("nonexistent");

        assertTrue(result.isEmpty());
    }

    @Test
    void createUser_WhenSuccessful_ShouldReturnTrue() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean result = userDAO.createUser("newUser", "hashedPassword");

        assertTrue(result);
    }

    @Test
    void createUser_WhenDatabaseError_ShouldReturnFalse() throws SQLException {
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Database error"));

        boolean result = userDAO.createUser("newUser", "hashedPassword");

        assertFalse(result);
    }
}