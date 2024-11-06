package io.github.btmxh.apartmentapp;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

public class RoleTableController {
    private static final Logger logger = LogManager.getLogger();
    @FXML
    private TableView<User> table;
    @FXML
    private TableColumn<User, User> numCol;
    @FXML
    private TableColumn<User, String> usernameCol;
    @FXML
    private TableColumn<User, DatabaseConnection.Role> roleCol;

    public void initialize() {
        numCol.setCellValueFactory(feat -> new ReadOnlyObjectWrapper<>(feat.getValue()));
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleCol.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableList(DatabaseConnection.Role.nonAdminRoles())));
    }

    public void onRoleEdit(TableColumn.CellEditEvent<User, DatabaseConnection.Role> event) {
        User user = event.getRowValue();
        DatabaseConnection dc = DatabaseConnection.getInstance();
        try {
            dc.setRole(user.getName(), event.getNewValue());
            user.setRole(event.getNewValue());
        } catch (SQLException e) {
            logger.warn("Error during executing SQL statement", e);
            Announcement.show("Error", "Unable to set user role", "Database connection error: " + e.getMessage());
        }
    }

    public void setUserData(int start, ObservableList<User> userList) {
        numCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(User user, boolean b) {
                super.updateItem(user, b);

                if (getTableRow() != null && user != null) {
                    setText((getTableRow().getIndex() + 1 + start) + "");
                } else {
                    setText("");
                }
            }
        });
        table.setItems(userList);
    }
}
