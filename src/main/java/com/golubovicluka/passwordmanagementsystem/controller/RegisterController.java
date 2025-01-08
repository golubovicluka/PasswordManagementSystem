package com.golubovicluka.passwordmanagementsystem.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button registerButton;

    @FXML
    private Button backToLoginButton;

    @FXML
    private void initialize() {
        registerButton.setOnAction(event -> handleRegister());
        backToLoginButton.setOnAction(event -> backToLogin());
    }

    private void handleRegister() {
        // TODO: Add registration logic here
        System.out.println("handleRegister() in register controller");
        backToLogin();
    }

    private void backToLogin() {
        System.out.println("backToLogin()");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/com/golubovicluka/passwordmanagementsystem/view/login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);

            Stage stage = (Stage) backToLoginButton.getScene().getWindow();

            stage.setTitle("Password Management - Login");
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}