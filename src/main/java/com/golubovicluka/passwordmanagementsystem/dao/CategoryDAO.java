package com.golubovicluka.passwordmanagementsystem.dao;

import com.golubovicluka.passwordmanagementsystem.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Category entities.
 * Handles database operations related to categories including retrieving, creating,
 * and managing categories for users.
 */
public class CategoryDAO {
    private final DatabaseConnection databaseConnection;

    /**
     * Default constructor that initializes a new database connection.
     */
    public CategoryDAO() {
        this.databaseConnection = new DatabaseConnection();
    }

    /**
     * Retrieves all categories associated with a specific user.
     *
     * @param userId The ID of the user whose categories to retrieve
     * @return A list of Category objects belonging to the specified user
     */
    public List<Category> getCategoriesForUser(int userId) {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT * FROM categories WHERE user_id = ?";

        try (Connection conn = databaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                categories.add(new Category(
                        rs.getInt("category_id"),
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("description")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    /**
     * Creates a new category for a user.
     *
     * @param userId The ID of the user who owns this category
     * @param name The name of the category
     * @param description The description of the category
     * @return The newly created Category object, or null if creation failed
     */
    public Category createCategory(int userId, String name, String description) {
        String query = "INSERT INTO categories (user_id, name, description) VALUES (?, ?, ?)";

        try (Connection conn = databaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, userId);
            stmt.setString(2, name);
            stmt.setString(3, description);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return null;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Category(generatedKeys.getInt(1), userId, name, description);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves all categories from the database.
     *
     * @return A list of all Category objects in the database
     */
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT * FROM categories";

        try (Connection conn = databaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categories.add(new Category(
                        rs.getInt("category_id"),
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("description")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }
}