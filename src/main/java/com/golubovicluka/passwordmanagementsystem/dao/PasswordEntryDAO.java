package com.golubovicluka.passwordmanagementsystem.dao;

import com.golubovicluka.passwordmanagementsystem.model.PasswordEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PasswordEntryDAO {
    public ObservableList<PasswordEntry> getAllPasswordEntriesForUser(int userId) {
        List<PasswordEntry> entries = new ArrayList<>();
        String query = "SELECT * FROM password_entries WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                PasswordEntry entry = new PasswordEntry(
                        rs.getString("username"),
                        rs.getString("encrypted_password"),
                        rs.getString("website_name"));
                entry.setId(rs.getInt("id"));
                entry.setWebsiteUrl(rs.getString("website_url"));
                entries.add(entry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return FXCollections.observableArrayList(entries);
    }

    public boolean addPasswordEntry(PasswordEntry entry, int userId) {
        String query = "INSERT INTO password_entries (user_id, website_name, website_url, username, encrypted_password) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, userId);
            stmt.setString(2, entry.getWebsite());
            stmt.setString(3, entry.getWebsiteUrl());
            stmt.setString(4, entry.getUsername());
            stmt.setString(5, entry.getPassword()); // Note: This should be encrypted in production

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    entry.setId(generatedKeys.getInt(1));
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}