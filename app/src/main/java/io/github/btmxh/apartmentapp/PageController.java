package io.github.btmxh.apartmentapp;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.commons.compiler.CompileException;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Month;
import java.util.Objects;

public class PageController {

    private static final Logger logger = LogManager.getLogger();

    private static final int ROWS_PER_PAGE = 10;

    private enum Section {
        CREATECHARGE,
        CHARGE,
        GRANTPERMISSION,
        STATISTICS,
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
    private ComboBox<Month> monthComboBox;

    @FXML
    private VBox grantPermissionVBox;

    @FXML
    private VBox statisticsVBox;

    @FXML
    private Pagination usersPagination;

    @FXML
    private Pagination serviceFeePagination;

    private final ObjectProperty<Section> section = new SimpleObjectProperty<>(Section.DEFAULT);

    private void bindSection(Section sect, Node content, Button button) {
        button.setOnAction(_e -> section.set(sect));
        content.visibleProperty().bind(section.isEqualTo(sect));
    }

    @FXML
    public void initialize() {
        setNumPages();
        usersPagination.setPageFactory(this::createUserTable);
        serviceFeePagination.setPageFactory(this::createServiceFeeTable);

        bindSection(Section.CREATECHARGE, createChargeVBox, createChargeButton);
        bindSection(Section.CHARGE, chargeVBox, chargeButton);
        bindSection(Section.GRANTPERMISSION, grantPermissionVBox, residentsButton);
        bindSection(Section.STATISTICS, statisticsVBox, staticButton);
        Utils.initMonthComboBox(monthComboBox);

        logoutButton.setOnAction(_e -> handleLogout());
    }

    private void handleLogout() {
        try {
            Region loginPage = Utils.fxmlLoader("/login-view.fxml").load();
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
            var  userList = dc.getNonAdminUserList(ROWS_PER_PAGE, pageIndex * ROWS_PER_PAGE);
            TableView<User> table = loader.load();
            RoleTableController controller = loader.getController();
            controller.setUserData(start, userList, this::updateUsers);
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

    private TableView<ServiceFee> createServiceFeeTable(int pageIndex) {
        DatabaseConnection dc = DatabaseConnection.getInstance();
        try {
            var loader = new FXMLLoader(Objects.requireNonNull(PageController.class.getResource("/service-fee-table.fxml")));
            int start = pageIndex * ROWS_PER_PAGE;
            var fees = dc.getServiceFees(ROWS_PER_PAGE, pageIndex * ROWS_PER_PAGE);
            TableView<ServiceFee> table = loader.load();
            ServiceFeeTableController controller = loader.getController();
            controller.setUserData(start, FXCollections.observableArrayList(fees), this::updateServiceFees);
            return table;
        } catch (SQLException e) {
            logger.warn("Error during executing SQL statement", e);
            Announcement.show("Error", "Unable to get service fee list", "Database connection error: " + e.getMessage());
        } catch (IOException e) {
            logger.fatal("Error loading FXML file", e);
            Announcement.show("Error", "Unable to load FXML service fee table", "Detailed error: " + e.getMessage());
        } catch (CompileException e) {
            logger.fatal("Error recompiling service fee formula", e);
            Announcement.show("Error", "Unable to load FXML service fee table", "Detailed error: " + e.getMessage());

        }
        return null;
    }

    private void setNumPages() {
        try {
            usersPagination.setPageCount((DatabaseConnection.getInstance().getNumNonAdminUsers() + ROWS_PER_PAGE - 1) / ROWS_PER_PAGE);
            serviceFeePagination.setPageCount((DatabaseConnection.getInstance().getNumServiceFees() + ROWS_PER_PAGE - 1) / ROWS_PER_PAGE);
        } catch (SQLException e) {
            logger.warn("Error during executing SQL statement", e);
            Announcement.show("Error", "Unable to get number of users", "Database connection error: " + e.getMessage());
        }
    }

    private void updateServiceFees() {
        int page = serviceFeePagination.getCurrentPageIndex();
        setNumPages();
        serviceFeePagination.setPageFactory(this::createServiceFeeTable);
        serviceFeePagination.setCurrentPageIndex(Math.min(page, serviceFeePagination.getPageCount() - 1));
    }

    private void updateUsers() {
        int page = usersPagination.getCurrentPageIndex();
        setNumPages();
        usersPagination.setPageFactory(this::createUserTable);
        usersPagination.setCurrentPageIndex(Math.min(page, usersPagination.getPageCount() - 1));
    }

    public void addServiceFee(ActionEvent event) {
        try {
            AddServiceFeeController.open(((Node) event.getSource()).getScene().getWindow(), null);
            updateServiceFees();
        } catch (IOException e) {
            logger.fatal("Error loading FXML file", e);
            Announcement.show("Error", "Unable to load FXML of service fee dialog", "Detailed error: " + e.getMessage());
        }
    }
}


