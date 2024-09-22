package io.github.btmxh.apartmentapp;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.SQLException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

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
            stage.show();

            // Log success
            LOGGER.info("Application started successfully");
        } catch (Exception e) {
            // Log any exceptions
            LOGGER.error("Error during application startup", e);
        }
    }

    public static void main(String[] args) {
        // Log the application start
        LOGGER.debug("Launching the application");
        launch(args);
        // Log the application end
        LOGGER.debug("Application shutdown");
    }
}

