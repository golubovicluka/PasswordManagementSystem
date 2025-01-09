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
    private Button logoutButton;

    @FXML
    private void initialize() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        websiteColumn.setCellValueFactory(new PropertyValueFactory<>("website"));

        websiteColumn.setCellFactory(column -> new TableCell<>() {
            private final ImageView imageView = new ImageView(DEFAULT_FAVICON);
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
            }
        });

        websiteColumn.setPrefWidth(300);

        ObservableList<PasswordEntry> mockData = FXCollections.observableArrayList(
                new PasswordEntry("john.doe", "********", "facebook.com"),
                new PasswordEntry("jane.smith", "********", "twitter.com"),
                new PasswordEntry("bob.wilson", "********", "linkedin.com"),
                new PasswordEntry("alice.wonder", "********", "github.com"),
                new PasswordEntry("mike.ross", "********", "google.com"),
                new PasswordEntry("emma.davis", "********", "amazon.com"),
                new PasswordEntry("david.miller", "********", "microsoft.com"),
                new PasswordEntry("sarah.jones", "********", "apple.com"),
                new PasswordEntry("james.wilson", "********", "netflix.com"),
                new PasswordEntry("olivia.brown", "********", "spotify.com"),
                new PasswordEntry("william.taylor", "********", "reddit.com"),
                new PasswordEntry("sophia.anderson", "********", "instagram.com"),
                new PasswordEntry("lucas.martin", "********", "youtube.com"),
                new PasswordEntry("ava.thompson", "********", "discord.com"),
                new PasswordEntry("noah.garcia", "********", "twitch.tv"));

        passwordTable.setItems(mockData);
        logoutButton.setOnAction(event -> handleLogout());
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
}
