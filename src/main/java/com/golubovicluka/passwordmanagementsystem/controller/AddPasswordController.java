package com.golubovicluka.passwordmanagementsystem.controller;

import com.golubovicluka.passwordmanagementsystem.model.PasswordEntry;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;

public class AddPasswordController {
    @FXML
    private TextField websiteField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button saveButton;
    @FXML
    private Button backButton;
    @FXML
    private Label messageLabel;
    @FXML
    private Button togglePasswordButton;
    @FXML
    private Button generatePasswordButton;
    @FXML
    private Text passwordHints;

    private TextField visiblePasswordField;
    private PasswordsController passwordsController;
    private boolean isPasswordVisible = false;
    private static final String VALID_STYLE = "";
    private static final String INVALID_STYLE = "-fx-border-color: red; -fx-border-width: 2px;";
    private static final String SUCCESS_STYLE = "-fx-border-color: green; -fx-border-width: 2px;";

    @FXML
    private void initialize() {
        setupValidation();
        setupPasswordToggle();
        setupPasswordGeneration();

        saveButton.setOnAction(event -> handleSave());
        backButton.setOnAction(event -> handleBack());
    }

    private void setupValidation() {
        // Add listeners for real-time validation
        websiteField.textProperty().addListener(
                (observable, oldValue, newValue) -> validateField(websiteField, !newValue.trim().isEmpty()));

        usernameField.textProperty().addListener(
                (observable, oldValue, newValue) -> validateField(usernameField, !newValue.trim().isEmpty()));

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean isValid = validatePassword(newValue);
            validateField(passwordField, isValid);
            if (visiblePasswordField != null) {
                validateField(visiblePasswordField, isValid);
            }
        });
    }

    private void setupPasswordToggle() {
        // Create visible password field (initially hidden)
        visiblePasswordField = new TextField();
        visiblePasswordField.setManaged(false);
        visiblePasswordField.setVisible(false);
        visiblePasswordField.promptTextProperty().bind(passwordField.promptTextProperty());

        // Bind the visible field to the password field
        passwordField.textProperty().bindBidirectional(visiblePasswordField.textProperty());

        // Add visible field to the GridPane
        GridPane gridPane = (GridPane) passwordField.getParent();
        gridPane.add(visiblePasswordField, 1, 2);

        togglePasswordButton.setOnAction(event -> {
            isPasswordVisible = !isPasswordVisible;
            passwordField.setManaged(!isPasswordVisible);
            passwordField.setVisible(!isPasswordVisible);
            visiblePasswordField.setManaged(isPasswordVisible);
            visiblePasswordField.setVisible(isPasswordVisible);
            togglePasswordButton.setText(isPasswordVisible ? "🔒" : "👁");
        });
    }

    private void setupPasswordGeneration() {
        generatePasswordButton.setOnAction(event -> {
            String generatedPassword = generateSecurePassword();
            passwordField.setText(generatedPassword);
            if (visiblePasswordField != null) {
                visiblePasswordField.setText(generatedPassword);
            }
        });
    }

    private String generateSecurePassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = upper.toLowerCase();
        String digits = "0123456789";
        String specialChars = "!@#$%^&*()_+-=[]{}|;:,.<>?";
        String allChars = upper + lower + digits + specialChars;

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        // Ensure at least one of each required character type
        password.append(upper.charAt(random.nextInt(upper.length())));
        password.append(lower.charAt(random.nextInt(lower.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));

        // Fill the rest with random characters
        for (int i = password.length(); i < 12; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Shuffle the password
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }

        return new String(passwordArray);
    }

    private boolean validatePassword(String password) {
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[0-9].*");
    }

    private void validateField(TextField field, boolean isValid) {
        field.setStyle(isValid ? VALID_STYLE : INVALID_STYLE);
    }

    private void handleSave() {
        String website = websiteField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (website.isEmpty() || username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please fill in all fields");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!validatePassword(password)) {
            messageLabel.setText("Password does not meet requirements");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Create new password entry
        PasswordEntry newEntry = new PasswordEntry(username, password, website);

        // Add to the main controller's data
        passwordsController.addPasswordEntry(newEntry);

        // Show success message
        messageLabel.setText("Password saved successfully!");
        messageLabel.setStyle("-fx-text-fill: green;");

        // Apply success style
        websiteField.setStyle(SUCCESS_STYLE);
        usernameField.setStyle(SUCCESS_STYLE);
        passwordField.setStyle(SUCCESS_STYLE);
        if (visiblePasswordField != null) {
            visiblePasswordField.setStyle(SUCCESS_STYLE);
        }

        // Clear fields after a delay
        new Thread(() -> {
            try {
                Thread.sleep(1500);
                javafx.application.Platform.runLater(this::clearFields);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void setPasswordsController(PasswordsController passwordsController) {
        this.passwordsController = passwordsController;
    }

    private void handleBack() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/com/golubovicluka/passwordmanagementsystem/view/passwords-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setTitle("Password Management - Passwords");
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        websiteField.clear();
        usernameField.clear();
        passwordField.clear();
    }
}