package com.golubovicluka.passwordmanagementsystem.controller;

import com.golubovicluka.passwordmanagementsystem.model.PasswordEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

import java.io.IOException;

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

    private ObservableList<PasswordEntry> masterData;
    private FilteredList<PasswordEntry> filteredData;
    private final PasswordEntryDAO passwordEntryDAO;
    private int currentUserId;

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
    }

    private void setupTableColumns() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        websiteColumn.setCellValueFactory(new PropertyValueFactory<>("website"));
        categoryColumn.setCellFactory(column -> new TableCell<PasswordEntry, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    setTooltip(null);
                    return;
                }

                PasswordEntry entry = getTableRow().getItem();
                Category category = entry.getCategory();

                setText(category != null ? category.getName() : "Uncategorized");

                if (category != null && category.getDescription() != null) {
                    setTooltip(new Tooltip(category.getDescription()));
                }
            }
        });

        categoryColumn.setCellValueFactory(cellData -> {
            Category category = cellData.getValue().getCategory();
            return new SimpleStringProperty(category != null ? category.getName() : "Uncategorized");
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
                    getClass().getResource("/com/golubovicluka/passwordmanagementsystem/view/add-password-view.fxml"));
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
                        passwordEntry.getUsername().toLowerCase().contains(lowerCaseFilter);
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
}
