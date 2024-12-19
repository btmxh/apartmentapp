package io.github.btmxh.apartmentapp;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.github.btmxh.apartmentapp.DatabaseConnection.Gender;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public class ResidentTableController {
    private static final Logger logger = LogManager.getLogger();

    public TableView<Citizen> table;
    public TableColumn<Citizen, Citizen> idCol;
    public TableColumn<Citizen, String> residentNameCol;
    public TableColumn<Citizen, LocalDate> birthCol;
    public TableColumn<Citizen, String> roomNumberCol;
    public TableColumn<Citizen, String> passportIDCol;
    public TableColumn<Citizen, Gender> genderCol;
    public TableColumn<Citizen, String> nationCol;
    private  Runnable updateResidents;

    public void initialize() {
        idCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue()));
        residentNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        birthCol.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        roomNumberCol.setCellValueFactory(new PropertyValueFactory<>("room"));
        passportIDCol.setCellValueFactory(new PropertyValueFactory<>("passportId"));
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        nationCol.setCellValueFactory(new PropertyValueFactory<>("nationality"));

        table.setContextMenu(new ContextMenu(
                new MenuItem("Xóa") {{
                    setOnAction(e -> {
                        final var selected = table.getSelectionModel().getSelectedItems();
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Xác nhận xóa");
                        alert.setHeaderText(null);
                        alert.setContentText("Bạn có chắc chắn muốn xóa " + selected.getFirst().getFullName() + " không?");

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            for (final var c : selected) {
                                try {
                                    DatabaseConnection.getInstance().removeResident(c.getId());
                                } catch (SQLException ex) {
                                    logger.warn("Không thể xóa người dùng " + c.getId(), ex);
                                }
                            }
                            updateResidents.run();
                        }
                    });
                }},
                new MenuItem("Sửa") {{
                    setOnAction(e -> {
                        final var selected = table.getSelectionModel().getSelectedItem();
                        if (selected == null) return;

                        try {
                            // Mở form sửa thông tin với dữ liệu của nhân khẩu được chọn
                            AddResidentController.open(table.getScene().getWindow(), selected);
                            updateResidents.run(); // Cập nhật lại dữ liệu sau khi sửa
                        } catch (IOException ex) {
                            logger.error("Không thể mở form sửa thông tin.", ex);
                            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                            errorAlert.setTitle("Lỗi");
                            errorAlert.setHeaderText(null);
                            errorAlert.setContentText("Không thể mở form sửa thông tin.");
                            errorAlert.showAndWait();
                        }
                    });
                }}
        ));
    }
    public void setResidentPage(int start, ObservableList<Citizen> citizens, Runnable updateResidents) {
        Utils.initNoColumn(idCol, start);
        table.setItems(citizens);
        this.updateResidents = updateResidents;
    }

}
