package com.golubovicluka.passwordmanagementsystem.dao;

import com.golubovicluka.passwordmanagementsystem.model.PasswordEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class PasswordEntryDAO {
    public ObservableList<PasswordEntry> getAllPasswordEntriesForUser(int userId) {
        ObservableList<PasswordEntry> entries = FXCollections.observableArrayList();
        String sql = "SELECT * FROM password_entries WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                PasswordEntry entry = new PasswordEntry(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("website"));
                entries.add(entry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entries;
    }

    public boolean addPasswordEntry(PasswordEntry entry, int userId) {
        String sql = "INSERT INTO password_entries (user_id, username, password, website) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, entry.getUsername());
            pstmt.setString(3, entry.getPassword());
            pstmt.setString(4, entry.getWebsite());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}