package com.golubovicluka.passwordmanagementsystem.service;

import com.golubovicluka.passwordmanagementsystem.dao.UserDAO;
import com.golubovicluka.passwordmanagementsystem.model.User;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.concurrent.CompletableFuture;

public class AuthService {
    private final UserDAO userDAO;
    private User authenticatedUser;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

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

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }

    public String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    public boolean registerUser(String username, String password) {
        try {
            String hashedPassword = hashPassword(password);
            return userDAO.createUser(username, hashedPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}