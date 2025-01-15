package com.golubovicluka.passwordmanagementsystem.dao;

import com.golubovicluka.passwordmanagementsystem.model.PasswordEntry;
import com.golubovicluka.passwordmanagementsystem.model.Category;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PasswordEntryDAO {
    private final DatabaseConnection databaseConnection = new DatabaseConnection();

    public ObservableList<PasswordEntry> getAllPasswordEntriesForUser(int userId) {
        ObservableList<PasswordEntry> entries = FXCollections.observableArrayList();
        String query = "SELECT pe.*, c.category_id, c.name AS category_name, c.description AS category_description " +
                "FROM password_entries pe " +
                "LEFT JOIN categories c ON pe.category_id = c.category_id " +
                "WHERE pe.user_id = ?";

        try (Connection conn = databaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Category category = null;
                if (rs.getObject("category_id") != null) {
                    category = new Category(
                            rs.getInt("category_id"),
                            userId,
                            rs.getString("category_name"),
                            rs.getString("category_description"));
                }

                PasswordEntry entry = new PasswordEntry(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("website"),
                        rs.getString("username"),
                        rs.getString("password"),
                        category,
                        null);
                entries.add(entry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entries;
    }

    public boolean addPasswordEntry(PasswordEntry entry, int userId) {
        String query = "INSERT INTO password_entries (user_id, website, username, password, category_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = databaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setString(2, entry.getWebsite());
            stmt.setString(3, entry.getUsername());
            stmt.setString(4, entry.getPassword());

            if (entry.getCategory() != null) {
                stmt.setInt(5, entry.getCategory().getId());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<PasswordEntry> getPasswordsForUser(int userId) {
        List<PasswordEntry> passwords = new ArrayList<>();
        String query = "SELECT p.*, c.name as category_name, " +
                "c.category_id, c.description as category_description " +
                "FROM password_entries p " +
                "LEFT JOIN categories c ON p.category_id = c.category_id " +
                "WHERE p.user_id = ?";

        try (Connection conn = databaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Category category = null;
                if (rs.getObject("category_id") != null) {
                    category = new Category(
                            rs.getInt("category_id"),
                            userId,
                            rs.getString("category_name"),
                            rs.getString("category_description"));
                }

                passwords.add(new PasswordEntry(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("website"),
                        rs.getString("username"),
                        rs.getString("password"),
                        category,
                        null));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return passwords;
    }

    public boolean createPasswordEntry(int userId, String website, String username, String password,
            Integer categoryId) {
        String query = "INSERT INTO password_entries (user_id, website, username, password, category_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = databaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setString(2, website);
            stmt.setString(3, username);
            stmt.setString(4, password);
            if (categoryId != null) {
                stmt.setInt(5, categoryId);
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}