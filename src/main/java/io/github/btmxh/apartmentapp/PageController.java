package io.github.btmxh.apartmentapp;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PageController {

    private static final Logger logger = LogManager.getLogger();

    @FXML
    private Button createchargeButton;

    @FXML
    private Button chargeButton;

    @FXML
    private Button residentsButton;

    @FXML
    private Button staticButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Label usernameLabel;

    @FXML
    private VBox contentVBox;

    @FXML
    public void initialize() {

        ObjectProperty<Boolean> isVisibleContentVBox = new SimpleObjectProperty<>(false);
        contentVBox.visibleProperty().bind(isVisibleContentVBox);

        usernameLabel.setText(User.getInstance().getUsername());

        chargeButton.setOnAction(_ -> {
           isVisibleContentVBox.set(!isVisibleContentVBox.get());
        });

        logoutButton.setOnAction(_ -> handleLogout());

        // Sự kiện khi mouse đi qua (hover vào button)
        createchargeButton.setOnMouseEntered(_ -> createchargeButton.setStyle("-fx-text-fill: #333; -fx-background-color: #b8919a; -fx-font-size: 14px; -fx-border-color: #9F6E3F; -fx-border-radius: 10;"));

        // Sự kiện khi mouse rời khỏi button
        createchargeButton.setOnMouseExited(_ -> createchargeButton.setStyle("-fx-text-fill: #333; -fx-background-color: transparent; -fx-font-size: 14px; -fx-border-color: #9F6E3F; -fx-border-radius: 10;"));

        chargeButton.setOnMouseEntered(_ -> chargeButton.setStyle("-fx-text-fill: #333; -fx-background-color: #b8919a; -fx-font-size: 14px; -fx-border-color: #9F6E3F; -fx-border-radius: 10;"));

        chargeButton.setOnMouseExited(_ -> chargeButton.setStyle("-fx-text-fill: #333; -fx-background-color: transparent; -fx-font-size: 14px; -fx-border-color: #9F6E3F; -fx-border-radius: 10;"));

        residentsButton.setOnMouseEntered(_ -> residentsButton.setStyle("-fx-text-fill: #333; -fx-background-color: #b8919a; -fx-font-size: 14px; -fx-border-color: #9F6E3F; -fx-border-radius: 10;"));

        residentsButton.setOnMouseExited(_ -> residentsButton.setStyle("-fx-text-fill: #333; -fx-background-color: transparent; -fx-font-size: 14px; -fx-border-color: #9F6E3F; -fx-border-radius: 10;"));

        residentsButton.setOnMouseEntered(_ -> residentsButton.setStyle("-fx-text-fill: #333; -fx-background-color: #b8919a; -fx-font-size: 14px; -fx-border-color: #9F6E3F; -fx-border-radius: 10;"));

        residentsButton.setOnMouseExited(_ -> residentsButton.setStyle("-fx-text-fill: #333; -fx-background-color: transparent; -fx-font-size: 14px; -fx-border-color: #9F6E3F; -fx-border-radius: 10;"));

        staticButton.setOnMouseEntered(_ -> staticButton.setStyle("-fx-text-fill: #333; -fx-background-color: #b8919a; -fx-font-size: 14px; -fx-border-color: #9F6E3F; -fx-border-radius: 10;"));

        staticButton.setOnMouseExited(_ -> staticButton.setStyle("-fx-text-fill: #333; -fx-background-color: transparent; -fx-font-size: 14px; -fx-border-color: #9F6E3F; -fx-border-radius: 10;"));

        logoutButton.setOnMouseEntered(_ -> logoutButton.setStyle("-fx-background-color: #c79361; -fx-background-radius: 20;"));

        logoutButton.setOnMouseExited(_ -> logoutButton.setStyle("-fx-background-color: #B47C48; -fx-background-radius: 20;"));
    }

    private void handleLogout() {
        try {
            Region loginPage = FXMLLoader.load(getClass().getResource("/login-view.fxml"));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.getScene().setRoot(loginPage);
        } catch (Exception e) {
            logger.fatal("Error loading FXML file", e);
            Announcement.show("Error","Unable to reach sign up page", "FXML loading error: " + e.getMessage());
        }
    }
}


