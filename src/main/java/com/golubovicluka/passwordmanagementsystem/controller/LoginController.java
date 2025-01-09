package com.golubovicluka.passwordmanagementsystem.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;

import java.io.IOException;
import com.golubovicluka.passwordmanagementsystem.service.AuthService;
import javafx.application.Platform;

public class LoginController {
    private final AuthService authService = new AuthService();

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private Label errorLabel;

    @FXML
    private void initialize() {
        loginButton.setOnAction(event -> handleLogin());
        registerButton.setOnAction(event -> handleRegister());

        usernameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (passwordField.getText().isEmpty()) {
                    passwordField.requestFocus();
                } else {
                    handleLogin();
                }
            }
        });

        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleLogin();
            }
        });

        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            usernameField.getStyleClass().remove("error-field");
            errorLabel.setVisible(false);
        });

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            passwordField.getStyleClass().remove("error-field");
            errorLabel.setVisible(false);
        });
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        usernameField.getStyleClass().remove("error-field");
        passwordField.getStyleClass().remove("error-field");
        errorLabel.setVisible(false);

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields");
            errorLabel.setVisible(true);
            if (username.isEmpty())
                usernameField.getStyleClass().add("error-field");
            if (password.isEmpty())
                passwordField.getStyleClass().add("error-field");
            return;
        }

        loginButton.setDisable(true);
        errorLabel.setText("Validating...");
        errorLabel.setVisible(true);

        authService.validateUser(username, password)
                .thenAccept(isValid -> Platform.runLater(() -> {
                    if (isValid) {
                        try {
                            FXMLLoader fxmlLoader = new FXMLLoader(
                                    getClass().getResource(
                                            "/com/golubovicluka/passwordmanagementsystem/view/passwords-view.fxml"));
                            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
                            Stage stage = (Stage) loginButton.getScene().getWindow();
                            stage.setTitle("Password Management - Passwords");
                            stage.setScene(scene);
                        } catch (IOException e) {
                            e.printStackTrace();
                            showError("An error occurred while loading the application");
                        }
                    } else {
                        showError("Invalid username or password");
                        usernameField.getStyleClass().add("error-field");
                        passwordField.getStyleClass().add("error-field");
                    }
                    loginButton.setDisable(false);
                }));
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void handleRegister() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/com/golubovicluka/passwordmanagementsystem/view/register-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);

            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setTitle("Password Management - Register");
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showError("An error occurred while loading the registration page");
        }
    }
}