package com.golubovicluka.passwordmanagementsystem.controller;

import com.golubovicluka.passwordmanagementsystem.model.PasswordEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import com.golubovicluka.passwordmanagementsystem.dao.PasswordEntryDAO;
import com.golubovicluka.passwordmanagementsystem.model.Category;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import java.util.List;
import com.golubovicluka.passwordmanagementsystem.dao.CategoryDAO;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import com.golubovicluka.passwordmanagementsystem.exception.DatabaseException;

public class PasswordsController {
    private final Image DEFAULT_FAVICON = new Image(
            getClass().getResourceAsStream("/com/golubovicluka/passwordmanagementsystem/images/default-favicon.png"));

    @FXML
    private TableView<PasswordEntry> passwordTable;

    @FXML
    private TableColumn<PasswordEntry, String> usernameColumn;

    @FXML
    private TableColumn<PasswordEntry, String> passwordColumn;

    @FXML
    private TableColumn<PasswordEntry, String> websiteColumn;

    @FXML
    private TableColumn<PasswordEntry, String> categoryColumn;

    @FXML
    private Button logoutButton;

    @FXML
    private TextField searchField;

    @FXML
    private Button addPasswordButton;

    @FXML
    private FlowPane categoryFilterPane;

    @FXML
    private TableColumn<PasswordEntry, Void> actionsColumn;

    private ObservableList<PasswordEntry> masterData;
    private FilteredList<PasswordEntry> filteredData;
    private final PasswordEntryDAO passwordEntryDAO;
    private int currentUserId;
    private FilteredList<PasswordEntry> filteredEntries;
    private String currentSearchText = "";
    private Category selectedCategory = null;

    public PasswordsController() {
        this.passwordEntryDAO = new PasswordEntryDAO();
    }

    @FXML
    private void initialize() {
        setupTableColumns();
        setupButtonHandlers();
        masterData = FXCollections.observableArrayList();
        filteredData = new FilteredList<>(masterData, p -> true);
        setupSearch();
        SortedList<PasswordEntry> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(passwordTable.comparatorProperty());
        passwordTable.setItems(sortedData);

        categoryColumn.setCellValueFactory(cellData -> {
            Category category = cellData.getValue().getCategory();
            return new SimpleStringProperty(category != null ? category.getName() : "");
        });

        filteredEntries = new FilteredList<>(masterData);
        passwordTable.setItems(filteredEntries);

        loadCategoryFilters();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            currentSearchText = newValue;
            updateFilters();
        });
    }

    private void setupTableColumns() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        passwordColumn.setCellFactory(column -> new TableCell<PasswordEntry, String>() {
            private boolean isRevealed = false;
            private final Tooltip hiddenTooltip = new Tooltip("Click to reveal password");
            private final Tooltip revealedTooltip = new Tooltip("Click to hide password • Click with CTRL to copy");

            {
                setTooltip(hiddenTooltip);

                setOnMouseClicked(event -> {
                    if (getItem() == null)
                        return;

                    if (event.isControlDown()) {

                        final Clipboard clipboard = Clipboard.getSystemClipboard();
                        final ClipboardContent content = new ClipboardContent();
                        content.putString(getItem());
                        clipboard.setContent(content);

                        Tooltip copiedTooltip = new Tooltip("Password copied!");
                        setTooltip(copiedTooltip);
                        new Thread(() -> {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            javafx.application.Platform
                                    .runLater(() -> setTooltip(isRevealed ? revealedTooltip : hiddenTooltip));
                        }).start();
                    } else {
                        isRevealed = !isRevealed;
                        updateItem(getItem(), false);
                    }
                });
            }

            @Override
            protected void updateItem(String password, boolean empty) {
                super.updateItem(password, empty);

                if (empty || password == null) {
                    setText(null);
                    setTooltip(null);
                    return;
                }

                if (isRevealed) {
                    setText(password);
                    setTooltip(revealedTooltip);
                } else {
                    setText("•".repeat(8));
                    setTooltip(hiddenTooltip);
                }
            }
        });
        websiteColumn.setCellValueFactory(new PropertyValueFactory<>("website"));
        categoryColumn.setCellFactory(column -> new TableCell<PasswordEntry, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setText(null);
                    setTooltip(null);
                    return;
                }

                PasswordEntry entry = getTableRow().getItem();
                if (entry == null) {
                    setText(null);
                    return;
                }

                Category category = entry.getCategory();
                setText(category != null ? category.getName() : "Uncategorized");

                if (category != null && category.getDescription() != null) {
                    setTooltip(new Tooltip(category.getDescription()));
                }
            }
        });

        websiteColumn.setCellFactory(column -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            private final HBox hbox = new HBox(5);
            private final Label label = new Label();

            {
                imageView.setFitHeight(20);
                imageView.setFitWidth(20);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                hbox.getChildren().addAll(imageView, label);
            }

            @Override
            protected void updateItem(String website, boolean empty) {
                super.updateItem(website, empty);
                if (empty || website == null) {
                    setGraphic(null);
                    return;
                }
                label.setText(website);
                setGraphic(hbox);

                loadFavicon(website, imageView);
            }
        });

        websiteColumn.setPrefWidth(300);

        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button editButton = new Button();
            private final Button deleteButton = new Button();
            private final HBox buttonsBox = new HBox(5);

            {
                editButton.getStyleClass().add("icon-button");
                editButton.setTooltip(new Tooltip("Edit Password"));

                FontIcon editIcon = new FontIcon(FontAwesomeSolid.PENCIL_ALT);
                editIcon.setIconSize(16);
                editButton.setGraphic(editIcon);

                deleteButton.getStyleClass().add("icon-button");
                deleteButton.setTooltip(new Tooltip("Delete Password"));

                FontIcon deleteIcon = new FontIcon(FontAwesomeSolid.TRASH_ALT);
                deleteIcon.setIconSize(16);
                deleteIcon.getStyleClass().add("delete-icon");
                deleteButton.setGraphic(deleteIcon);

                buttonsBox.getChildren().addAll(editButton, deleteButton);
                buttonsBox.setAlignment(Pos.CENTER);

                editButton.setOnAction(event -> {
                    PasswordEntry entry = getTableView().getItems().get(getIndex());
                    handleEditPassword(entry);
                });

                deleteButton.setOnAction(event -> {
                    PasswordEntry entry = getTableView().getItems().get(getIndex());
                    handleDeletePassword(entry);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonsBox);
                }
            }
        });
    }

    private void loadFavicon(String website, ImageView imageView) {
        new Thread(() -> {
            try {
                String faviconUrl = "https://www.google.com/s2/favicons?sz=64&domain_url=" + website;
                Image favicon = new Image(faviconUrl, true);
                favicon.progressProperty().addListener((obs, oldProgress, newProgress) -> {
                    if (newProgress.doubleValue() == 1.0) {
                        imageView.setImage(favicon);
                    }
                });
                favicon.errorProperty().addListener((obs, oldError, newError) -> {
                    if (newError) {
                        imageView.setImage(DEFAULT_FAVICON);
                    }
                });
            } catch (Exception e) {
                imageView.setImage(DEFAULT_FAVICON);
            }
        }).start();
    }

    private void setupButtonHandlers() {
        logoutButton.setOnAction(event -> handleLogout());
        addPasswordButton.setOnAction(event -> handleAddPassword());
    }

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
        loadPasswordEntries();
    }

    private void loadPasswordEntries() {
        masterData.clear();

        ObservableList<PasswordEntry> entries = passwordEntryDAO.getAllPasswordEntriesForUser(currentUserId);
        masterData.addAll(entries);
    }

    private void handleLogout() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/com/golubovicluka/passwordmanagementsystem/view/login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setTitle("Password Management - Login");
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleAddPassword() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource(
                            "/com/golubovicluka/passwordmanagementsystem/view/add-edit-password-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);

            AddPasswordController controller = fxmlLoader.getController();
            controller.setPasswordsController(this);

            Stage stage = (Stage) addPasswordButton.getScene().getWindow();
            stage.setTitle("Password Management - Add Password");
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(passwordEntry -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                return passwordEntry.getWebsite().toLowerCase().contains(lowerCaseFilter) ||
                        passwordEntry.getUsername().toLowerCase().contains(lowerCaseFilter) ||
                        (passwordEntry.getCategory() != null &&
                                passwordEntry.getCategory().getName().toLowerCase().contains(lowerCaseFilter));
            });
        });
    }

    public void addPasswordEntry(PasswordEntry entry) {
        if (passwordEntryDAO.addPasswordEntry(entry, currentUserId)) {
            masterData.add(entry);
        }
    }

    public int getCurrentUserId() {
        return currentUserId;
    }

    private void loadCategoryFilters() {
        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> categories = categoryDAO.getAllCategories();

        categoryFilterPane.getChildren().clear();

        Button allButton = createCategoryButton(null);
        categoryFilterPane.getChildren().add(allButton);

        for (Category category : categories) {
            Button categoryButton = createCategoryButton(category);
            categoryFilterPane.getChildren().add(categoryButton);
        }
    }

    private Button createCategoryButton(Category category) {
        Button button = new Button(category == null ? "All" : category.getName());
        button.getStyleClass().add("category-filter-button");

        button.setOnAction(e -> {
            selectedCategory = category;
            updateFilters();

            categoryFilterPane.getChildren()
                    .forEach(node -> node.getStyleClass().remove("category-filter-button-selected"));

            button.getStyleClass().add("category-filter-button-selected");
        });

        return button;
    }

    private void updateFilters() {
        filteredEntries.setPredicate(entry -> {
            boolean matchesSearch = currentSearchText.isEmpty() ||
                    entry.getWebsite().toLowerCase().contains(currentSearchText.toLowerCase()) ||
                    entry.getUsername().toLowerCase().contains(currentSearchText.toLowerCase()) ||
                    (entry.getTitle() != null
                            && entry.getTitle().toLowerCase().contains(currentSearchText.toLowerCase()));

            boolean matchesCategory = selectedCategory == null ||
                    (entry.getCategory() != null && entry.getCategory().getId() == selectedCategory.getId());

            return matchesSearch && matchesCategory;
        });
    }

    private void handleEditPassword(PasswordEntry entry) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource(
                            "/com/golubovicluka/passwordmanagementsystem/view/add-edit-password-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);

            AddPasswordController controller = fxmlLoader.getController();
            controller.setPasswordsController(this);
            controller.setEditMode(entry);

            Stage stage = (Stage) addPasswordButton.getScene().getWindow();
            stage.setTitle("Password Management - Edit Password");
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDeletePassword(PasswordEntry entry) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Delete");
        confirmDialog.setHeaderText("Delete Password Entry");
        confirmDialog.setContentText("Are you sure you want to delete this password entry?");

        Stage stage = (Stage) confirmDialog.getDialogPane().getScene().getWindow();
        stage.getIcons().clear();

        DialogPane dialogPane = confirmDialog.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("/com/golubovicluka/passwordmanagementsystem/styles/style.css")
                        .toExternalForm());

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    passwordEntryDAO.deletePasswordEntry(entry.getId());
                    masterData.remove(entry);
                } catch (DatabaseException e) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText("Database Error");
                    errorAlert.setContentText("Failed to delete password entry: " + e.getMessage());
                    errorAlert.showAndWait();
                }
            }
        });
    }
}
