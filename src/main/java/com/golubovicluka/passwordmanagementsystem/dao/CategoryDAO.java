package com.golubovicluka.passwordmanagementsystem.dao;

import com.golubovicluka.passwordmanagementsystem.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    private final DatabaseConnection databaseConnection;

    public CategoryDAO() {
        this.databaseConnection = new DatabaseConnection();
    }

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