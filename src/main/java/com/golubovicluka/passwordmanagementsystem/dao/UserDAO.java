package com.golubovicluka.passwordmanagementsystem.dao;

import com.golubovicluka.passwordmanagementsystem.model.User;
import java.sql.*;
import java.util.Optional;

/**
 * Data Access Object for User entities.
 * Handles database operations related to users including finding and creating users.
 */
public class UserDAO {
    private final DatabaseConnection databaseConnection;

    /**
     * Default constructor that initializes a new database connection.
     */
    public UserDAO() {
        this.databaseConnection = new DatabaseConnection();
    }

    /**
     * Constructor with dependency injection for database connection.
     * 
     * @param databaseConnection The database connection to use
     */
    public UserDAO(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    /**
     * Finds a user by their username.
     * 
     * @param username The username to search for
     * @return An Optional containing the User if found, or empty if not found
     */
    public Optional<User> findByUsername(String username) {
        String query = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = databaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getTimestamp("created_at").toLocalDateTime());
                return Optional.of(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Creates a new user in the database.
     * 
     * @param username The username for the new user
     * @param hashedPassword The hashed password for the new user
     * @return true if user was successfully created, false otherwise
     */
    public boolean createUser(String username, String hashedPassword) {
        String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?)";

        try (Connection conn = databaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);

            return pstmt.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}