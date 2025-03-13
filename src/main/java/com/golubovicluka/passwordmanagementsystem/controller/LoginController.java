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
import com.golubovicluka.passwordmanagementsystem.model.User;

/**
 * Controller class for the login view of the Password Management System.
 * Handles user authentication, login form interactions, and navigation to other views.
 * This controller manages the login form's UI elements and their associated behaviors.
 */
public class LoginController {
    /** Service responsible for handling user authentication */
    private final AuthService authService = new AuthService();
    
    /** Currently logged-in user instance */
    private User loggedInUser;

    /** Text field for entering username */
    @FXML
    private TextField usernameField;

    /** Password field for entering user password */
    @FXML
    private PasswordField passwordField;

    /** Button to trigger login process */
    @FXML
    private Button loginButton;

    /** Button to navigate to registration view */
    @FXML
    private Button registerButton;

    /** Label for displaying error messages */
    @FXML
    private Label errorLabel;

    /**
     * Initializes the login form and sets up event handlers for UI elements.
     * Configures button actions, keyboard shortcuts, and input field listeners.
     */
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

    /**
     * Handles the login process when triggered by button click or Enter key.
     * Validates input fields, authenticates user credentials, and navigates to the passwords view
     * upon successful authentication.
     */
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
        errorLabel.setText("Authenticating...");
        errorLabel.setVisible(true);

        authService.validateUser(username, password)
                .thenAccept(user -> Platform.runLater(() -> {
                    if (user != null) {
                        loggedInUser = user;
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                                    "/com/golubovicluka/passwordmanagementsystem/view/passwords-view.fxml"));
                            Scene scene = new Scene(loader.load(), 1200, 800);

                            PasswordsController passwordsController = loader.getController();
                            if (passwordsController == null) {
                                throw new IOException(
                                        "Failed to load the passwords controller. Please check FXML configuration.");
                            }

                            passwordsController.setCurrentUserId(loggedInUser.getId());

                            Stage stage = (Stage) loginButton.getScene().getWindow();
                            stage.setTitle("Password Management - Passwords");
                            stage.setScene(scene);
                        } catch (IOException e) {
                            e.printStackTrace();
                            showError("An error occurred while loading the application: " + e.getMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
                            showError("An unexpected error occurred: " + e.getMessage());
                        }
                    } else {
                        showError("Invalid username or password");
                        usernameField.getStyleClass().add("error-field");
                        passwordField.getStyleClass().add("error-field");
                    }
                    loginButton.setDisable(false);
                }))
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        showError("Connection error. Please try again.");
                        loginButton.setDisable(false);
                    });
                    return null;
                });
    }

    /**
     * Displays an error message in the error label.
     * 
     * @param message The error message to display
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    /**
     * Handles navigation to the registration view when the register button is clicked.
     * Loads and displays the registration form in a new scene.
     */
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