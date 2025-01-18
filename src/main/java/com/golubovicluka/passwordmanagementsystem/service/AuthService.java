package com.golubovicluka.passwordmanagementsystem.service;

import com.golubovicluka.passwordmanagementsystem.dao.UserDAO;
import com.golubovicluka.passwordmanagementsystem.model.User;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.concurrent.CompletableFuture;

/**
 * Service class responsible for handling user authentication and registration
 * operations.
 * This class provides methods for user validation, password hashing, and user
 * registration.
 * It uses BCrypt for secure password hashing and implements asynchronous
 * operations for
 * better performance.
 */
public class AuthService {
    private final UserDAO userDAO;
    private User authenticatedUser;

    /**
     * Constructs a new AuthService instance.
     * Initializes the UserDAO for database operations.
     */
    public AuthService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Validates user credentials asynchronously.
     * This method checks if the provided username exists and if the password
     * matches
     * the stored hash.
     *
     * @param username The username to validate
     * @param password The plain text password to verify
     * @return A CompletableFuture containing the User object if authentication is
     *         successful,
     *         or null if authentication fails
     */
    public CompletableFuture<User> validateUser(String username, String password) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                User user = userDAO.findByUsername(username)
                        .filter(u -> BCrypt.checkpw(password, u.getPasswordHash()))
                        .orElse(null);

                if (user != null) {
                    this.authenticatedUser = user;
                    return user;
                }
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    /**
     * Retrieves the currently authenticated user.
     *
     * @return The authenticated User object, or null if no user is authenticated
     */
    public User getAuthenticatedUser() {
        return authenticatedUser;
    }

    /**
     * Hashes a plain text password using BCrypt.
     * This method generates a salt and creates a secure hash of the password.
     *
     * @param plainTextPassword The password to hash
     * @return The BCrypt hash of the password
     */
    public String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    /**
     * Registers a new user in the system.
     * This method hashes the provided password and creates a new user record
     * in the database.
     *
     * @param username The username for the new user
     * @param password The plain text password for the new user
     * @return true if registration is successful, false otherwise
     */
    public boolean registerUser(String username, String password) {
        try {
            // Check if username exists first
            if (userDAO.findByUsername(username).isPresent()) {
                return false;
            }

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            return userDAO.createUser(username, hashedPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}