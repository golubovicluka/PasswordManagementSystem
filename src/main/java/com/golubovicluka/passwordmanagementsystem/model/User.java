package com.golubovicluka.passwordmanagementsystem.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a user in the password management system.
 * This class corresponds to the 'users' table in the database and maintains
 * a relationship with PasswordEntry entities.
 */
public class User {
    private Integer id;
    private String username;
    private String passwordHash;
    private LocalDateTime createdAt;
    private ObservableList<PasswordEntry> passwordEntries;

    /**
     * Default constructor initializing an empty password entries list
     */
    public User() {
        this.passwordEntries = FXCollections.observableArrayList();
    }

    /**
     * Constructor for creating a new user with basic information
     *
     * @param username     the user's username
     * @param passwordHash the hashed password of the user
     */
    public User(String username, String passwordHash) {
        this();
        this.username = username;
        this.passwordHash = passwordHash;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Full constructor for creating a user with all attributes
     *
     * @param id           the user's unique identifier
     * @param username     the user's username
     * @param passwordHash the hashed password of the user
     * @param createdAt    the timestamp when the user was created
     */
    public User(Integer id, String username, String passwordHash, LocalDateTime createdAt) {
        this();
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ObservableList<PasswordEntry> getPasswordEntries() {
        return passwordEntries;
    }

    public void setPasswordEntries(List<PasswordEntry> passwordEntries) {
        this.passwordEntries.clear();
        this.passwordEntries.addAll(passwordEntries);
    }

    /**
     * Adds a new password entry to the user's list of password entries
     *
     * @param entry the PasswordEntry to add
     */
    public void addPasswordEntry(PasswordEntry entry) {
        this.passwordEntries.add(entry);
    }

    /**
     * Removes a password entry from the user's list of password entries
     *
     * @param entry the PasswordEntry to remove
     * @return true if the entry was removed successfully, false otherwise
     */
    public boolean removePasswordEntry(PasswordEntry entry) {
        return this.passwordEntries.remove(entry);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", createdAt=" + createdAt +
                ", passwordEntriesCount=" + passwordEntries.size() +
                '}';
    }
}