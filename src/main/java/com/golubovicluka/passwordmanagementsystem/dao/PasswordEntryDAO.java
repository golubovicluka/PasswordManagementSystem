package com.golubovicluka.passwordmanagementsystem.dao;

import com.golubovicluka.passwordmanagementsystem.exception.DatabaseException;
import com.golubovicluka.passwordmanagementsystem.model.PasswordEntry;
import com.golubovicluka.passwordmanagementsystem.model.Category;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PasswordEntryDAO {
    private static final Logger logger = LoggerFactory.getLogger(PasswordEntryDAO.class);
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
                entries.add(mapResultSetToPasswordEntry(rs, userId));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving password entries for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to retrieve password entries", e);
        }
        return entries;
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
                passwords.add(mapResultSetToPasswordEntry(rs, userId));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving passwords for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to retrieve passwords", e);
        }
        return passwords;
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
            logger.error("Error adding password entry for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to add password entry", e);
        }
    }

    public boolean updatePasswordEntry(PasswordEntry entry) {
        String query = "UPDATE password_entries SET website = ?, username = ?, password = ?, category_id = ? WHERE id = ?";

        try (Connection conn = databaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, entry.getWebsite());
            stmt.setString(2, entry.getUsername());
            stmt.setString(3, entry.getPassword());

            if (entry.getCategory() != null) {
                stmt.setInt(4, entry.getCategory().getId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            stmt.setInt(5, entry.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating password entry {}: {}", entry.getId(), e.getMessage());
            throw new DatabaseException("Failed to update password entry", e);
        }
    }

    public boolean deletePasswordEntry(int entryId) {
        String query = "DELETE FROM password_entries WHERE id = ?";

        try (Connection conn = databaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, entryId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting password entry {}: {}", entryId, e.getMessage());
            throw new DatabaseException("Failed to delete password entry", e);
        }
    }

    private PasswordEntry mapResultSetToPasswordEntry(ResultSet rs, int userId) throws SQLException {
        Category category = null;
        if (rs.getObject("category_id") != null) {
            category = new Category(
                    rs.getInt("category_id"),
                    userId,
                    rs.getString("category_name"),
                    rs.getString("category_description"));
        }

        return new PasswordEntry(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getString("website"),
                rs.getString("username"),
                rs.getString("password"),
                category,
                null);
    }
}