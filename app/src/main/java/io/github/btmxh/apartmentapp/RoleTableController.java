package io.github.btmxh.apartmentapp;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.Optional;

public class RoleTableController {
    private static final Logger logger = LogManager.getLogger(RoleTableController.class);
    @FXML
    private TableView<User> table;
    @FXML
    private TableColumn<User, User> numCol;
    @FXML
    private TableColumn<User, String> usernameCol;
    @FXML
    private TableColumn<User, String> fullnameCol;
    @FXML
    private TableColumn<User, String> phoneNumCol;
    @FXML
    private TableColumn<User, DatabaseConnection.Role> roleCol;
    private Runnable updateUsers;

    public void initialize() {
        numCol.setCellValueFactory(feat -> new ReadOnlyObjectWrapper<>(feat.getValue()));
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleCol.setCellFactory(ComboBoxTableCell.forTableColumn(Utils.readOnlyStringConverter(DatabaseConnection.Role::getDisplayName), FXCollections.observableList(DatabaseConnection.Role.getNonAdminRoles())));
        fullnameCol.setCellValueFactory(new PropertyValueFactory<>("fullname"));
        phoneNumCol.setCellValueFactory(new PropertyValueFactory<>("phoneNum"));

        table.setContextMenu(new ContextMenu(
                new MenuItem("Xóa") {{
                    setOnAction(e -> {
                        final var selected = table.getSelectionModel().getSelectedItems();
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Xác nhận xóa");
                        alert.setHeaderText(null);
                        alert.setContentText("Bạn có chắc chắn muốn xóa người dùng " + selected.getFirst().getName() + " không?");

                        Optional<ButtonType> result = alert.showAndWait();
                        if(result.isPresent() && result.get() == ButtonType.OK) {
                            for(final var user : selected) {
                                try {
                                    DatabaseConnection.getInstance().removeUser(user.getId());
                                } catch (SQLException ex) {
                                    logger.warn("Không thể xóa người dùng " + user.getId(), ex);
                                }
                            }
                            updateUsers.run();
                        }
                    });
                }}
        ));
    }

    public void onRoleEdit(TableColumn.CellEditEvent<User, DatabaseConnection.Role> event) {
        User user = event.getRowValue();
        DatabaseConnection dc = DatabaseConnection.getInstance();
        try {
            dc.setRole(user.getName(), event.getNewValue());
            user.setRole(event.getNewValue());
        } catch (SQLException e) {
            logger.warn("Lỗi khi thực hiện câu lệnh SQL", e);
            Announcement.show("Lỗi", "Không thể thiết lập vai trò người dùng", "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
        }
    }

    public void setUserData(int start, ObservableList<User> userList, Runnable updateUsers) {
        Utils.initNoColumn(numCol, start);
        table.setItems(userList);
        this.updateUsers = updateUsers;
    }
}
