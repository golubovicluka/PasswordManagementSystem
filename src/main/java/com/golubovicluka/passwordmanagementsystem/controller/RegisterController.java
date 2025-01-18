package com.golubovicluka.passwordmanagementsystem.controller;

import com.golubovicluka.passwordmanagementsystem.service.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.Random;

public class RegisterController {
    private final AuthService authService = new AuthService();

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
    private Button generatePasswordButton;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField visiblePasswordField;

    @FXML
    private TextField visibleConfirmPasswordField;

    @FXML
    private Button togglePasswordButton;

    @FXML
    private FontIcon togglePasswordIcon;

    @FXML
    private void initialize() {
        registerButton.setOnAction(event -> handleRegister());
        backToLoginButton.setOnAction(event -> backToLogin());
        errorLabel.setVisible(false);
        generatePasswordButton.setOnAction(event -> generateAndSetPassword());

        passwordField.textProperty().bindBidirectional(visiblePasswordField.textProperty());
        confirmPasswordField.textProperty().bindBidirectional(visibleConfirmPasswordField.textProperty());

        togglePasswordButton.setOnAction(event -> {
            boolean passwordIsVisible = visiblePasswordField.isVisible();
            visiblePasswordField.setVisible(!passwordIsVisible);
            visiblePasswordField.setManaged(!passwordIsVisible);
            passwordField.setVisible(passwordIsVisible);
            passwordField.setManaged(passwordIsVisible);
            visibleConfirmPasswordField.setVisible(!passwordIsVisible);
            visibleConfirmPasswordField.setVisible(!passwordIsVisible);
            visibleConfirmPasswordField.setManaged(!passwordIsVisible);
            confirmPasswordField.setVisible(passwordIsVisible);
            confirmPasswordField.setManaged(passwordIsVisible);
            togglePasswordIcon.setIconLiteral(passwordIsVisible ? "fas-eye" : "fas-eye-slash");
        });
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        errorLabel.setVisible(false);
        usernameField.getStyleClass().remove("error-field");
        passwordField.getStyleClass().remove("error-field");
        confirmPasswordField.getStyleClass().remove("error-field");

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill in all fields");
            if (username.isEmpty())
                usernameField.getStyleClass().add("error-field");
            if (password.isEmpty())
                passwordField.getStyleClass().add("error-field");
            if (confirmPassword.isEmpty())
                confirmPasswordField.getStyleClass().add("error-field");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            passwordField.getStyleClass().add("error-field");
            confirmPasswordField.getStyleClass().add("error-field");
            return;
        }

        try {
            if (authService.registerUser(username, password)) {
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Registration Successful");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Account created successfully! You can now log in.");
                successAlert.showAndWait();
                backToLogin();
            } else {
                showError("Username already exists");
                usernameField.getStyleClass().add("error-field");
            }
        } catch (Exception e) {
            showError("An error occurred during registration. Please try again.");
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void backToLogin() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/com/golubovicluka/passwordmanagementsystem/view/login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            Stage stage = (Stage) backToLoginButton.getScene().getWindow();
            stage.setTitle("Password Management - Login");
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error returning to login page");
        }
    }

    private void generateAndSetPassword() {
        String generatedPassword = generateSecurePassword();
        passwordField.setText(generatedPassword);
        confirmPasswordField.setText(generatedPassword);

        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(generatedPassword);
        clipboard.setContent(content);

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Password Generated");
        alert.setHeaderText(null);
        alert.setContentText("A secure password has been generated and copied to your clipboard.");
        alert.showAndWait();
    }

    private String generateSecurePassword() {
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String specialChars = "!@#$%^&*()_+-=[]{}|;:,.<>?";

        StringBuilder password = new StringBuilder();
        Random random = new Random();

        password.append(upperCase.charAt(random.nextInt(upperCase.length())));
        password.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        password.append(numbers.charAt(random.nextInt(numbers.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));

        String allChars = upperCase + lowerCase + numbers + specialChars;
        while (password.length() < 12) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }

        return new String(passwordArray);
    }
}