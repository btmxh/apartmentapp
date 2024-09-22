package io.github.btmxh.apartmentapp;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.fxml.FXMLLoader;

public class Main extends Application {
    // Initialize logger
    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    @Override
    public void start(Stage stage) {
        try {
            // Log info message
            LOGGER.info("Starting the application");

            // Load FXML
            Parent root;
            root = FXMLLoader.load(getClass().getResource("/login-view.fxml"));

            final var scene = new Scene(root);
            stage.setScene(scene);
            stage.setMinWidth(360);
            stage.setMinHeight(480);

            stage.show();

            // Log success
            LOGGER.info("Application started successfully");
        } catch (Exception e) {
            // Log any exceptions
            LOGGER.error("Error during application startup", e);
        }
    }

    public static void main(String[] args) {
        // Create a table for users
        LOGGER.debug("Connecting to DB");
        DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
        databaseConnection.createUsersTable();

        // Log the application start
        LOGGER.debug("Launching the application");
        launch(args);
        // Log the application end
        LOGGER.debug("Application shutdown");
    }
}

