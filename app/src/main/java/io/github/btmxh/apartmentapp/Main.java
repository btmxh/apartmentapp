package io.github.btmxh.apartmentapp;

import atlantafx.base.theme.*;
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
        Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());

        try {
            // Log info message
            LOGGER.info("Khởi chạy ứng dụng");

            // Load FXML
            Parent root;
            root = FXMLLoader.load(getClass().getResource("/login-view.fxml"));

            final var scene = new Scene(root);
            stage.setScene(scene);
            stage.setMinWidth(1120);
            stage.setMinHeight(645);

            stage.show();

            // Log success
            LOGGER.info("Ứng dụng đã khởi chạy thành công");
        } catch (Exception e) {
            // Log any exceptions
            LOGGER.error("Lỗi trong quá trình khởi chạy ứng dụng", e);
        }
    }

    public static void main(String[] args) {
        // Create a table for users
        LOGGER.debug("Kết nối với Cơ sở dữ liệu");
        DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
        databaseConnection.createUsersTable();
        databaseConnection.createServiceFeeTable();
        databaseConnection.createPaymentsTable();
        databaseConnection.createCitizensTable();

        // Log the application start
        LOGGER.debug("Khởi chạy ứng dụng");
        launch(args);
        // Log the application end
        databaseConnection.disconnect();
        LOGGER.debug("Đã ngắt kết nối khỏi Cơ sở dữ liệu");
        LOGGER.debug("Tắt ứng dụng");

    }
}

