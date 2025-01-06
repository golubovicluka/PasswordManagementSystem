package com.golubovicluka.passwordmanagementsystem.controller;

import com.golubovicluka.passwordmanagementsystem.model.PasswordEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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
    private void initialize() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        websiteColumn.setCellValueFactory(new PropertyValueFactory<>("website"));

        ObservableList<PasswordEntry> mockData = FXCollections.observableArrayList(
            new PasswordEntry("john.doe", "********", "facebook.com"),
            new PasswordEntry("jane.smith", "********", "twitter.com"),
            new PasswordEntry("bob.wilson", "********", "linkedin.com"),
            new PasswordEntry("alice.wonder", "********", "instagram.com"),
            new PasswordEntry("mike.ross", "********", "github.com")
        );

        passwordTable.setItems(mockData);
    }
} 