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

/**
 * Data Access Object for PasswordEntry entities.
 * Handles database operations related to password entries including retrieving,
 * creating,
 * updating, and deleting password entries for users.
 */
public class PasswordEntryDAO {
    private static final Logger logger = LoggerFactory.getLogger(PasswordEntryDAO.class);

    /**
     * Retrieves all password entries for a specific user as an ObservableList.
     * This method is specifically designed for JavaFX UI binding.
     *
     * @param userId The ID of the user whose password entries to retrieve
     * @return An ObservableList of PasswordEntry objects belonging to the specified
     *         user
     * @throws DatabaseException If there is an error retrieving the password
     *                           entries
     */
    public ObservableList<PasswordEntry> getAllPasswordEntriesForUser(int userId) {
        ObservableList<PasswordEntry> entries = FXCollections.observableArrayList();
        String query = "SELECT pe.*, c.category_id, c.name AS category_name, c.description AS category_description " +
                "FROM password_entries pe " +
                "LEFT JOIN categories c ON pe.category_id = c.category_id " +
                "WHERE pe.user_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
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

    /**
     * Retrieves all password entries for a specific user as a standard List.
     *
     * @param userId The ID of the user whose password entries to retrieve
     * @return A List of PasswordEntry objects belonging to the specified user
     * @throws DatabaseException If there is an error retrieving the password
     *                           entries
     */
    public List<PasswordEntry> getPasswordsForUser(int userId) {
        List<PasswordEntry> passwords = new ArrayList<>();
        String query = "SELECT p.*, c.name as category_name, " +
                "c.category_id, c.description as category_description " +
                "FROM password_entries p " +
                "LEFT JOIN categories c ON p.category_id = c.category_id " +
                "WHERE p.user_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
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

    /**
     * Adds a new password entry to the database for a specific user.
     *
     * @param entry  The PasswordEntry object to add
     * @param userId The ID of the user who owns this password entry
     * @return true if the password entry was successfully added, false otherwise
     * @throws DatabaseException If there is an error adding the password entry
     */
    public boolean addPasswordEntry(PasswordEntry entry, int userId) {
        String query = "INSERT INTO password_entries (user_id, website, username, password, category_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
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

    /**
     * Updates an existing password entry in the database.
     *
     * @param entry The PasswordEntry object with updated information
     * @return true if the password entry was successfully updated, false otherwise
     * @throws DatabaseException If there is an error updating the password entry
     */
    public boolean updatePasswordEntry(PasswordEntry entry) {
        String query = "UPDATE password_entries SET website = ?, username = ?, password = ?, category_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
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

    /**
     * Deletes a password entry from the database.
     *
     * @param entryId The ID of the password entry to delete
     * @return true if the password entry was successfully deleted, false otherwise
     * @throws DatabaseException If there is an error deleting the password entry
     */
    public boolean deletePasswordEntry(int entryId) {
        String query = "DELETE FROM password_entries WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, entryId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting password entry {}: {}", entryId, e.getMessage());
            throw new DatabaseException("Failed to delete password entry", e);
        }
    }

    /**
     * Maps a database result set to a PasswordEntry object.
     *
     * @param rs     The ResultSet containing password entry data
     * @param userId The ID of the user who owns this password entry
     * @return A PasswordEntry object populated with data from the ResultSet
     * @throws SQLException If there is an error accessing the ResultSet
     */
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