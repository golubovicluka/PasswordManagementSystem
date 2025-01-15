package com.golubovicluka.passwordmanagementsystem.controller;

import com.golubovicluka.passwordmanagementsystem.dao.CategoryDAO;
import com.golubovicluka.passwordmanagementsystem.model.Category;
import com.golubovicluka.passwordmanagementsystem.model.PasswordEntry;

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
    @FXML
    private ComboBox<Category> categoryComboBox;
    @FXML
    private Button addCategoryButton;

    @FXML
    private TextField visiblePasswordField;
    private PasswordsController passwordsController;
    private boolean isPasswordVisible = false;
    private static final String VALID_STYLE = "";
    private static final String INVALID_STYLE = "-fx-border-color: red; -fx-border-width: 2px;";
    private static final String SUCCESS_STYLE = "-fx-border-color: green; -fx-border-width: 2px;";
    private CategoryDAO categoryDAO;

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

    private void setupPasswordToggle() {
        visiblePasswordField.setManaged(false);
        visiblePasswordField.setVisible(false);
        visiblePasswordField.promptTextProperty().bind(passwordField.promptTextProperty());
        passwordField.textProperty().bindBidirectional(visiblePasswordField.textProperty());

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

    public void setPasswordsController(PasswordsController passwordsController) {
        this.passwordsController = passwordsController;
        loadCategories();
    }

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

    private void clearFields() {
        websiteField.clear();
        usernameField.clear();
        passwordField.clear();
    }

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

    private void loadCategories() {
        List<Category> categories = categoryDAO.getCategoriesForUser(passwordsController.getCurrentUserId());
        categoryComboBox.setItems(FXCollections.observableArrayList(categories));
    }
}