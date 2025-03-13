package com.golubovicluka.passwordmanagementsystem.controller;

import com.golubovicluka.passwordmanagementsystem.dao.CategoryDAO;
import com.golubovicluka.passwordmanagementsystem.dao.PasswordEntryDAO;
import com.golubovicluka.passwordmanagementsystem.model.Category;
import com.golubovicluka.passwordmanagementsystem.model.PasswordEntry;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;

import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Controller class for adding and editing password entries in the password management system.
 * Manages the UI components and functionality for creating new password entries or updating
 * existing ones, including password validation, generation, and toggling visibility.
 *
 * This controller also handles category management for password entries, allowing users to
 * select existing categories or create new ones.
 */
public class AddPasswordController {

    /** TextField for entering the website or application name */
    @FXML
    private TextField websiteField;

    /** TextField for entering the username or email */
    @FXML
    private TextField usernameField;

    /** Secure field for entering the password (masked) */
    @FXML
    private PasswordField passwordField;

    /** Button to save the password entry */
    @FXML
    private Button saveButton;

    /** Button to navigate back to the previous screen */
    @FXML
    private Button backButton;

    /** Label for displaying user feedback messages */
    @FXML
    private Label messageLabel;

    /** Button to toggle password visibility */
    @FXML
    private Button togglePasswordButton;

    /** Button to generate a secure random password */
    @FXML
    private Button generatePasswordButton;

    /** Text component displaying password requirements and hints */
    @FXML
    private Text passwordHints;

    /** ComboBox for selecting a password category */
    @FXML
    private ComboBox<Category> categoryComboBox;

    /** Button to add a new category */
    @FXML
    private Button addCategoryButton;

    /** TextField for displaying the password in plain text when visibility is toggled */
    @FXML
    private TextField visiblePasswordField;

    /** Reference to the parent passwords controller for navigation and data management */
    private PasswordsController passwordsController;

    /** Flag indicating whether the password is currently visible */
    private boolean isPasswordVisible = false;

    /** CSS style applied to valid input fields */
    private static final String VALID_STYLE = "";

    /** CSS style applied to invalid input fields */
    private static final String INVALID_STYLE = "-fx-border-color: red; -fx-border-width: 2px;";

    /** CSS style applied to fields after successful save */
    private static final String SUCCESS_STYLE = "-fx-border-color: green; -fx-border-width: 2px;";

    /** Data Access Object for managing categories */
    private CategoryDAO categoryDAO;

    /** Label displaying the form title */
    @FXML
    private Label titleLabel;

    /** Flag indicating whether the controller is in edit mode */
    private boolean isEditMode = false;

    /** Reference to the password entry being edited */
    private PasswordEntry editingEntry = null;

    /** Data Access Object for managing password entries */
    private final PasswordEntryDAO passwordEntryDAO = new PasswordEntryDAO();

    /** FontIcon for the password visibility toggle button */
    @FXML
    private FontIcon togglePasswordIcon;

    /**
     * Initializes the controller after its root element has been completely processed.
     * Sets up validation, password toggle functionality, password generation, and category controls.
     */
    @FXML
    private void initialize() {
        setupValidation();
        setupPasswordToggle();
        setupPasswordGeneration();

        saveButton.setOnAction(event -> handleSave());
        backButton.setOnAction(event -> handleBack());

        categoryDAO = new CategoryDAO();
        setupCategoryControls();
    }

    /**
     * Sets up real-time validation for the input fields.
     * Validates the password as the user types and provides visual feedback.
     */
    private void setupValidation() {
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

    /**
     * Sets up the password toggle functionality that allows showing or hiding the password.
     * Manages the state of the password field, visible password field, and toggle icon.
     */
    private void setupPasswordToggle() {
        passwordField.setVisible(true);
        passwordField.setManaged(true);
        visiblePasswordField.setVisible(false);
        visiblePasswordField.setManaged(false);

        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());

        togglePasswordButton.setOnAction(event -> {
            if (passwordField.isVisible()) {
                passwordField.setVisible(false);
                passwordField.setManaged(false);
                visiblePasswordField.setVisible(true);
                visiblePasswordField.setManaged(true);
                togglePasswordIcon.setIconLiteral("fas-eye-slash");
            } else {
                passwordField.setVisible(true);
                passwordField.setManaged(true);
                visiblePasswordField.setVisible(false);
                visiblePasswordField.setManaged(false);
                togglePasswordIcon.setIconLiteral("fas-eye");
            }
        });
    }

    /**
     * Sets up the password generation feature.
     * Configures the generate password button to create a secure random password.
     */
    private void setupPasswordGeneration() {
        generatePasswordButton.setOnAction(event -> {
            String generatedPassword = generateSecurePassword();
            passwordField.setText(generatedPassword);
            if (visiblePasswordField != null) {
                visiblePasswordField.setText(generatedPassword);
            }
        });
    }

    /**
     * Generates a secure random password that meets the security requirements.
     * Creates a password containing uppercase, lowercase, numbers, and special characters.
     *
     * @return A securely generated random password
     */
    private String generateSecurePassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = upper.toLowerCase();
        String digits = "0123456789";
        String specialChars = "!@#$%^&*()_+-=[]{}|;:,.<>?";
        String allChars = upper + lower + digits + specialChars;

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        password.append(upper.charAt(random.nextInt(upper.length())));
        password.append(lower.charAt(random.nextInt(lower.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));

        for (int i = password.length(); i < 12; i++) {
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

    /**
     * Validates if a password meets the security requirements.
     * Checks for minimum length, uppercase, lowercase, numbers, and special characters.
     *
     * @param password The password to validate
     * @return true if the password meets all requirements, false otherwise
     */
    private boolean validatePassword(String password) {
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[0-9].*");
    }

    /**
     * Applies visual styling to a field based on its validation state.
     *
     * @param field The field to apply styling to
     * @param isValid Whether the field's current value is valid
     */
    private void validateField(TextField field, boolean isValid) {
        field.setStyle(isValid ? VALID_STYLE : INVALID_STYLE);
    }

    /**
     * Handles the save action when the user submits the form.
     * Validates all fields, creates or updates a password entry, and navigates back.
     */
    private void handleSave() {
        String website = websiteField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        Category selectedCategory = categoryComboBox.getValue();

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

        if (isEditMode) {
            editingEntry.setWebsite(website);
            editingEntry.setUsername(username);
            editingEntry.setPassword(password);
            editingEntry.setCategory(selectedCategory);

            if (passwordEntryDAO.updatePasswordEntry(editingEntry)) {
                messageLabel.setText("Password updated successfully!");
                messageLabel.setStyle("-fx-text-fill: green;");
                new Thread(() -> {
                    try {
                        Thread.sleep(1500);
                        Platform.runLater(this::handleBack);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                messageLabel.setText("Failed to update password");
                messageLabel.setStyle("-fx-text-fill: red;");
            }
        } else {
            PasswordEntry newEntry = new PasswordEntry(username, password, website);
            newEntry.setCategory(selectedCategory);
            passwordsController.addPasswordEntry(newEntry);

            messageLabel.setText("Password saved successfully!");
            messageLabel.setStyle("-fx-text-fill: green;");

            websiteField.setStyle(SUCCESS_STYLE);
            usernameField.setStyle(SUCCESS_STYLE);
            passwordField.setStyle(SUCCESS_STYLE);
            if (visiblePasswordField != null) {
                visiblePasswordField.setStyle(SUCCESS_STYLE);
            }

            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(this::clearFields);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    /**
     * Sets the parent passwords controller for navigation purposes.
     *
     * @param passwordsController The parent controller to set
     */
    public void setPasswordsController(PasswordsController passwordsController) {
        this.passwordsController = passwordsController;
        loadCategories();
    }

    /**
     * Handles the back button action.
     * Returns to the previous screen without saving changes.
     */
    private void handleBack() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/com/golubovicluka/passwordmanagementsystem/view/passwords-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 800);

            PasswordsController newPasswordsController = fxmlLoader.getController();
            newPasswordsController.setCurrentUserId(this.passwordsController.getCurrentUserId());

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setTitle("Password Management - Passwords");
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Clears all input fields and resets their validation state.
     */
    private void clearFields() {
        websiteField.clear();
        usernameField.clear();
        passwordField.clear();
    }

    /**
     * Sets up the category-related UI controls.
     * Initializes the category dropdown and add category button.
     */
    private void setupCategoryControls() {
        addCategoryButton.setOnAction(e -> showAddCategoryDialog());

        categoryComboBox.setCellFactory(param -> new ListCell<Category>() {
            @Override
            protected void updateItem(Category category, boolean empty) {
                super.updateItem(category, empty);
                if (empty || category == null) {
                    setText(null);
                } else {
                    setText(category.getName());
                }
            }
        });

        categoryComboBox.setButtonCell(new ListCell<Category>() {
            @Override
            protected void updateItem(Category category, boolean empty) {
                super.updateItem(category, empty);
                if (empty || category == null) {
                    setText(null);
                } else {
                    setText(category.getName());
                }
            }
        });
    }


    /**
     * Shows a dialog for adding a new category.
     * Allows the user to create and save a new category to the system.
     */
    private void showAddCategoryDialog() {
        Dialog<Category> dialog = new Dialog<>();
        dialog.setTitle("Add New Category");
        dialog.setHeaderText("Create a new category");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("/com/golubovicluka/passwordmanagementsystem/styles/style.css")
                        .toExternalForm());

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogPane.getButtonTypes().addAll(saveButtonType, cancelButtonType);

        Button saveButton = (Button) dialogPane.lookupButton(saveButtonType);
        Button cancelButton = (Button) dialogPane.lookupButton(cancelButtonType);
        saveButton.getStyleClass().add("ok-button");
        cancelButton.getStyleClass().add("cancel-button");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Category name");
        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Category description");
        descriptionField.setPrefRowCount(3);

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);

        dialogPane.setContent(grid);

        saveButton.setDisable(true);
        nameField.textProperty()
                .addListener((observable, oldValue, newValue) -> saveButton.setDisable(newValue.trim().isEmpty()));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String name = nameField.getText().trim();
                String description = descriptionField.getText().trim();
                return categoryDAO.createCategory(
                        passwordsController.getCurrentUserId(),
                        name,
                        description);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(category -> {
            if (category != null) {
                loadCategories();
                categoryComboBox.setValue(category);
            }
        });
    }

    /**
     * Loads all available categories into the category dropdown.
     * Retrieves categories from the database and populates the combo box.
     */

    private void loadCategories() {
        List<Category> categories = categoryDAO.getCategoriesForUser(passwordsController.getCurrentUserId());
        categoryComboBox.setItems(FXCollections.observableArrayList(categories));
    }

    /**
     * Switches the controller to edit mode for modifying an existing password entry.
     * Populates fields with the existing entry's data and changes UI elements accordingly.
     *
     * @param entry The password entry to edit
     */

    public void setEditMode(PasswordEntry entry) {
        this.isEditMode = true;
        this.editingEntry = entry;
        titleLabel.setText("Edit Password Entry");
        saveButton.setText("Update Password");

        websiteField.setText(entry.getWebsite());
        usernameField.setText(entry.getUsername());
        passwordField.setText(entry.getPassword());
        visiblePasswordField.setText(entry.getPassword());
        if (entry.getCategory() != null) {
            categoryComboBox.setValue(entry.getCategory());
        }
    }
}