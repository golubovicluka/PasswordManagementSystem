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
import javafx.scene.text.Text;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.kordamp.ikonli.javafx.FontIcon;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Random;

/**
 * Controller class for the user registration view of the Password Management
 * System.
 * Handles user registration, password generation, and navigation between
 * registration
 * and login views. This controller manages the registration form's UI elements
 * and
 * their associated behaviors.
 */
public class RegisterController {
    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    /** Service responsible for handling user authentication and registration */
    private final AuthService authService = new AuthService();

    /** Text field for entering username */
    @FXML
    private TextField usernameField;

    /** Password field for entering password */
    @FXML
    private PasswordField passwordField;

    /** Text field for displaying password hints */
    @FXML
    private Text passwordHints;

    /** Password field for confirming password */
    @FXML
    private PasswordField confirmPasswordField;

    /** Button to submit registration */
    @FXML
    private Button registerButton;

    /** Button to return to login view */
    @FXML
    private Button backToLoginButton;

    /** Button to generate a secure password */
    @FXML
    private Button generatePasswordButton;

    /** Label for displaying error messages */
    @FXML
    private Label errorLabel;

    /** Text field for visible password entry */
    @FXML
    private TextField visiblePasswordField;

    /** Text field for visible password confirmation */
    @FXML
    private TextField visibleConfirmPasswordField;

    /** Button to toggle password visibility */
    @FXML
    private Button togglePasswordButton;

    /** Icon for password visibility toggle */
    @FXML
    private FontIcon togglePasswordIcon;

    /**
     * Initializes the registration form and sets up event handlers for UI elements.
     * Configures button actions, password visibility toggle, and password field
     * bindings.
     */
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

        loadPasswordTips();
    }

    /**
     * Loads password tips from Microsoft's website to display password creation
     * guidelines.
     * If the website is not accessible, falls back to local password tips.
     * The tips are displayed in the passwordHints text area.
     */
    private void loadPasswordTips() {
        passwordHints.setText("Loading password tips...");
        String url = "https://support.microsoft.com/en-us/account-billing/how-to-create-a-strong-password-for-your-microsoft-account-f67e4ddd-0dbe-cd75-cebe-0cfda3cf7386";
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent(
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                    .get();

            String intro = doc.select("section.ocpIntroduction > p:not(:empty)").first().text();

            Elements tips = doc.select("section.ocpIntroduction > ul > li > p");

            StringBuilder tipsText = new StringBuilder(intro);
            tipsText.append("\n\n");

            for (Element tip : tips) {
                tipsText.append("• ").append(tip.text()).append("\n");
            }

            passwordHints.setText(tipsText.toString());

        } catch (IOException e) {
            passwordHints.setText(getFallbackPasswordTips());
            System.err.println("Error fetching data: " + e.getMessage());
        }
    }

    /**
     * Provides a set of default password tips when online tips cannot be loaded.
     * These tips cover essential aspects of creating strong passwords.
     *
     * @return A string containing basic password creation guidelines
     */
    private String getFallbackPasswordTips() {
        return "Strong Password Essentials:\n"
                + "• Minimum 12 characters\n"
                + "• Mix uppercase & lowercase\n"
                + "• Include numbers & symbols\n"
                + "• Avoid personal information\n"
                + "• Use unique passwords per account\n"
                + "• Consider passphrases";
    }

    /**
     * Handles the registration process when triggered by the register button.
     * Validates input fields, checks password matching, and attempts to register
     * the user.
     * Shows appropriate success or error messages based on the registration result.
     */
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
     * Handles navigation back to the login view when the back button is clicked.
     * Loads and displays the login form in a new scene.
     */
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

    /**
     * Generates a secure password and sets it in both password fields.
     * Copies the generated password to the system clipboard and shows a
     * notification.
     */
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

    /**
     * Generates a secure password with the following characteristics:
     * - At least 12 characters long
     * - Contains at least one uppercase letter
     * - Contains at least one lowercase letter
     * - Contains at least one number
     * - Contains at least one special character
     * - Characters are randomly shuffled
     *
     * @return A secure password string meeting the specified criteria
     */
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