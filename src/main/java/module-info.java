module com.golubovicluka.passwordmanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires okhttp3;
    requires com.fasterxml.jackson.databind;

    opens com.golubovicluka.passwordmanagementsystem to javafx.fxml;
    opens com.golubovicluka.passwordmanagementsystem.controller to javafx.fxml;
    opens com.golubovicluka.passwordmanagementsystem.model to javafx.base;
    opens com.golubovicluka.passwordmanagementsystem.service to com.fasterxml.jackson.databind;

    exports com.golubovicluka.passwordmanagementsystem;
    exports com.golubovicluka.passwordmanagementsystem.controller;
    exports com.golubovicluka.passwordmanagementsystem.model;
    exports com.golubovicluka.passwordmanagementsystem.service;
}
