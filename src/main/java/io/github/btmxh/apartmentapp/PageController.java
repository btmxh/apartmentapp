package io.github.btmxh.apartmentapp;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PageController {

    private static final Logger logger = LogManager.getLogger();

    private enum Section {
        CREATECHARGE,
        CHARGE,
        DEFAULT
    }

    @FXML
    private Button createChargeButton;

    @FXML
    private Button chargeButton;

//    @FXML
//    private Button residentsButton;
//
//    @FXML
//    private Button staticButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Label usernameLabel;

    @FXML
    private VBox createChargeVBox;

    @FXML
    private VBox chargeVBox;

    @FXML
    private ComboBox<Integer> monthComboBox;

    @FXML
    public void initialize() {

        ObjectProperty<Section> section = new SimpleObjectProperty<>(Section.DEFAULT);

        createChargeVBox.visibleProperty().bind(
                Bindings.createBooleanBinding(
                        () -> section.get() == Section.CREATECHARGE,
                        section
                )
        );

        chargeVBox.visibleProperty().bind(
                Bindings.createBooleanBinding(
                        () -> section.get() == Section.CHARGE,
                        section
                )
        );

        createChargeButton.setOnAction(_ -> section.set(Section.CREATECHARGE));

        chargeButton.setOnAction(_ -> section.set(Section.CHARGE));

        ObservableList<Integer> months = FXCollections.observableArrayList(
                1, 2, 3, 4, 5, 6,
                7, 8, 9, 10, 11, 12
        );
        monthComboBox.setItems(months);


        logoutButton.setOnAction(_ -> handleLogout());

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

    public void setUser(User user) {
        usernameLabel.textProperty().bind(user.getUsername());
    }
}


