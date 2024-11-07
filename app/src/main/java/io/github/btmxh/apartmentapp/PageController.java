package io.github.btmxh.apartmentapp;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class PageController {

    private static final Logger logger = LogManager.getLogger();

    private static final int ROWS_PER_PAGE = 10;

    private enum Section {
        CREATECHARGE,
        CHARGE,
        GRANTPERMISSION,
        DEFAULT
    }

    @FXML
    private Button createChargeButton;

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
    private VBox createChargeVBox;

    @FXML
    private VBox chargeVBox;

    @FXML
    private ComboBox<Integer> monthComboBox;

    @FXML
    private VBox grantPermissionVBox;

    @FXML
    private Pagination usersPagination;

    @FXML
    public void initialize() {

        setNumPages();
        usersPagination.setPageFactory(this::createUserTable);

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

        grantPermissionVBox.visibleProperty().bind(
                Bindings.createBooleanBinding(
                        () -> section.get() == Section.GRANTPERMISSION,
                        section
                )
        );

        createChargeButton.setOnAction(_e -> section.set(Section.CREATECHARGE));

        chargeButton.setOnAction(_e -> section.set(Section.CHARGE));

        residentsButton.setOnAction(_e -> section.set(Section.GRANTPERMISSION));

        ObservableList<Integer> months = FXCollections.observableArrayList(
                1, 2, 3, 4, 5, 6,
                7, 8, 9, 10, 11, 12
        );
        monthComboBox.setItems(months);

        logoutButton.setOnAction(_e -> handleLogout());

        // Sự kiện khi mouse đi qua (hover vào button)
        createChargeButton.setOnMouseEntered(_e -> createChargeButton.setStyle("-fx-text-fill: #333; -fx-background-color: #b8919a; -fx-font-size: 14px; -fx-border-color: #9F6E3F; -fx-border-radius: 10;"));

        // Sự kiện khi mouse rời khỏi button
        createChargeButton.setOnMouseExited(_e -> createChargeButton.setStyle("-fx-text-fill: #333; -fx-background-color: transparent; -fx-font-size: 14px; -fx-border-color: #9F6E3F; -fx-border-radius: 10;"));

        chargeButton.setOnMouseEntered(_e -> chargeButton.setStyle("-fx-text-fill: #333; -fx-background-color: #b8919a; -fx-font-size: 14px; -fx-border-color: #9F6E3F; -fx-border-radius: 10;"));

        chargeButton.setOnMouseExited(_e -> chargeButton.setStyle("-fx-text-fill: #333; -fx-background-color: transparent; -fx-font-size: 14px; -fx-border-color: #9F6E3F; -fx-border-radius: 10;"));

        residentsButton.setOnMouseEntered(_e -> residentsButton.setStyle("-fx-text-fill: #333; -fx-background-color: #b8919a; -fx-font-size: 14px; -fx-border-color: #9F6E3F; -fx-border-radius: 10;"));

        residentsButton.setOnMouseExited(_e -> residentsButton.setStyle("-fx-text-fill: #333; -fx-background-color: transparent; -fx-font-size: 14px; -fx-border-color: #9F6E3F; -fx-border-radius: 10;"));

        residentsButton.setOnMouseEntered(_e -> residentsButton.setStyle("-fx-text-fill: #333; -fx-background-color: #b8919a; -fx-font-size: 14px; -fx-border-color: #9F6E3F; -fx-border-radius: 10;"));

        residentsButton.setOnMouseExited(_e -> residentsButton.setStyle("-fx-text-fill: #333; -fx-background-color: transparent; -fx-font-size: 14px; -fx-border-color: #9F6E3F; -fx-border-radius: 10;"));

        staticButton.setOnMouseEntered(_e -> staticButton.setStyle("-fx-text-fill: #333; -fx-background-color: #b8919a; -fx-font-size: 14px; -fx-border-color: #9F6E3F; -fx-border-radius: 10;"));

        staticButton.setOnMouseExited(_e -> staticButton.setStyle("-fx-text-fill: #333; -fx-background-color: transparent; -fx-font-size: 14px; -fx-border-color: #9F6E3F; -fx-border-radius: 10;"));

        logoutButton.setOnMouseEntered(_e -> logoutButton.setStyle("-fx-background-color: #c79361; -fx-background-radius: 20;"));

        logoutButton.setOnMouseExited(_e -> logoutButton.setStyle("-fx-background-color: #B47C48; -fx-background-radius: 20;"));
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

    public void setUser(CurrentUser user) {
        usernameLabel.textProperty().bind(user.getUsername());
    }

    private TableView<User> createUserTable(int pageIndex) {
        DatabaseConnection dc = DatabaseConnection.getInstance();
        try {
            var loader = new FXMLLoader(Objects.requireNonNull(PageController.class.getResource("/role-table.fxml")));
            int start = pageIndex * ROWS_PER_PAGE;
            var  userList = dc.getUserList(ROWS_PER_PAGE, pageIndex * ROWS_PER_PAGE);
            TableView<User> table = loader.load();
            RoleTableController controller = loader.getController();
            controller.setUserData(start, userList);
            return table;
        } catch (SQLException e) {
            logger.warn("Error during executing SQL statement", e);
            Announcement.show("Error", "Unable to get user list", "Database connection error: " + e.getMessage());
        } catch (IOException e) {
            logger.fatal("Error loading FXML file", e);
            Announcement.show("Error", "Unable to load FXML role table", "Detailed error: " + e.getMessage());
        }
        return null;
    }

    private void setNumPages() {
        DatabaseConnection dc = DatabaseConnection.getInstance();
        try {
            int numUsers = dc.getNumUsers();
            usersPagination.setPageCount(numUsers / ROWS_PER_PAGE + 1);
        }
        catch (SQLException e) {
            logger.warn("Error during executing SQL statement", e);
            Announcement.show("Error", "Unable to get number of users", "Database connection error: " + e.getMessage());
        }
    }
}


