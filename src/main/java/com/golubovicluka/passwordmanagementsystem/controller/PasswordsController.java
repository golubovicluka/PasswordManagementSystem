package com.golubovicluka.passwordmanagementsystem.controller;

import com.golubovicluka.passwordmanagementsystem.model.PasswordEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class PasswordsController {
    @FXML
    private TableView<PasswordEntry> passwordTable;

    @FXML
    private TableColumn<PasswordEntry, String> usernameColumn;

    @FXML
    private TableColumn<PasswordEntry, String> passwordColumn;

    @FXML
    private TableColumn<PasswordEntry, String> websiteColumn;

    @FXML
    private Button logoutButton;

    @FXML
    private void initialize() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        websiteColumn.setCellValueFactory(new PropertyValueFactory<>("website"));

        ObservableList<PasswordEntry> mockData = FXCollections.observableArrayList(
                new PasswordEntry("john.doe", "********", "facebook.com"),
                new PasswordEntry("jane.smith", "********", "x.com"),
                new PasswordEntry("bob.wilson", "********", "linkedin.com"),
                new PasswordEntry("alice.wonder", "********", "neetcode.com"),
                new PasswordEntry("mike.ross", "********", "udemy.com"),
                new PasswordEntry("pera.peric", "********", "leetcode.com"),
                new PasswordEntry("luka.golubovic", "********", "github.com"));

        passwordTable.setItems(mockData);

        logoutButton.setOnAction(event -> handleLogout());
    }

    private void handleLogout() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/com/golubovicluka/passwordmanagementsystem/view/login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setTitle("Password Management - Login");
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}