package com.golubovicluka.passwordmanagementsystem.model;

/**
 * Represents a password category in the system.
 * Categories are used to organize password entries and are associated with
 * specific users.
 */
public class Category {
    private int id;
    private int userId;
    private String name;
    private String description;

    public Category(int categoryId, int userId, String name, String description) {
        this.id = categoryId;
        this.userId = userId;
        this.name = name;
        this.description = description;
    }

    public Category() {
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return name;
    }
}