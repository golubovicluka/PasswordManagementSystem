package com.golubovicluka.passwordmanagementsystem.model;

/**
 * Represents a password category in the system.
 * Categories are used to organize password entries and are associated with
 * specific users.
 * Each category has a unique identifier, name, description, and is linked to a
 * user.
 */
public class Category {
    /** The unique identifier for the category */
    private int id;

    /** The ID of the user who owns this category */
    private int userId;

    /** The name of the category */
    private String name;

    /** The description of the category */
    private String description;

    /**
     * Constructs a new Category with specified parameters.
     *
     * @param categoryId  the unique identifier for the category
     * @param userId      the ID of the user who owns this category
     * @param name        the name of the category
     * @param description the description of the category
     */
    public Category(int categoryId, int userId, String name, String description) {
        this.id = categoryId;
        this.userId = userId;
        this.name = name;
        this.description = description;
    }

    /**
     * Constructs a new empty Category.
     * Used when creating a category before setting its properties.
     */
    public Category() {
    }

    /**
     * Gets the unique identifier of the category.
     *
     * @return the category's ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the category.
     *
     * @param id the ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the ID of the user who owns this category.
     *
     * @return the user's ID
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the ID of the user who owns this category.
     *
     * @param userId the user ID to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Gets the name of the category.
     *
     * @return the category name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the category.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of the category.
     *
     * @return the category description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the category.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns a string representation of the category.
     * This method returns the category name for use in UI components.
     *
     * @return the name of the category
     */
    @Override
    public String toString() {
        return name;
    }
}