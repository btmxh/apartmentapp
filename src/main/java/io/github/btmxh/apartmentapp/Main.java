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

public class Main extends Application {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void start(Stage stage) {
        final var content = new VBox();
        final var label = new Label("Hello, World!");
        final var button = new Button("Query");
        final var result = new Label();

        content.setAlignment(Pos.CENTER);
        content.getChildren().addAll(label, button, result);

        button.setOnAction((e) -> {
            try {
                LOGGER.debug("Button pressed");
                result.setText(Database.queryDataFromDatabase());
            } catch (SQLException ex) {
                result.setText("ERROR");
                LOGGER.error("Error querying from DB", ex);
            }
        });

        final var scene = new Scene(content);
        stage.setScene(scene);
        stage.setWidth(480);
        stage.setHeight(360);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
