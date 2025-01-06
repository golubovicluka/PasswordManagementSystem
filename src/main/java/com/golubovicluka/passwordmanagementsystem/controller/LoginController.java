package com.golubovicluka.passwordmanagementsystem.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private void initialize() {
        loginButton.setOnAction(event -> handleLogin());
        registerButton.setOnAction(event -> handleRegister());
    }

    private void handleLogin() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/golubovicluka/passwordmanagementsystem/passwords-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            
            Stage stage = (Stage) loginButton.getScene().getWindow();
            
            stage.setTitle("Password Management - Passwords");
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRegister() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/golubovicluka/passwordmanagementsystem/register-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 400, 300);
            
            Stage stage = (Stage) registerButton.getScene().getWindow();
            
            stage.setTitle("Password Management - Register");
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 