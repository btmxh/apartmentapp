package io.github.btmxh.apartmentapp;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;

public class ServiceFeeTableController {
    private static final Logger logger = LogManager.getLogger();
    @FXML
    private TableView<ServiceFee> table;
    @FXML
    private TableColumn<ServiceFee, ServiceFee> noCol;
    @FXML
    private TableColumn<ServiceFee, String> nameCol;
    @FXML
    private TableColumn<ServiceFee, String> progressCol;
    @FXML
    private TableColumn<ServiceFee, Void> viewCol;

    private Runnable updateFees = () -> {};

    public void initialize() {
        noCol.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue()));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        progressCol.setCellValueFactory(f -> new SimpleStringProperty("heheheh"));
        viewCol.setCellValueFactory(f -> null);
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        table.setContextMenu(new ContextMenu(
                new MenuItem("Edit"){{
                    setOnAction(e -> {
                        final var fee = table.getSelectionModel().getSelectedItem();
                        try {
                            AddServiceFeeController.open(table.getScene().getWindow(), fee);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                }},
                new MenuItem("Remove"){{
                    setOnAction(e -> {
                        for(final var fee : table.getSelectionModel().getSelectedItems()) {
                            try {
                                DatabaseConnection.getInstance().removeServiceFee(fee.getId());
                            } catch (SQLException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                        updateFees.run();
                    });
                }}
        ));
    }

    public void setUserData(int start, ObservableList<ServiceFee> userList, Runnable updateFees) {
        Utils.initNoColumn(noCol, start);
        table.setItems(userList);
        this.updateFees = updateFees;
    }

    public void updateFeeName(TableColumn.CellEditEvent<ServiceFee, String> e) {
        try {
            DatabaseConnection.getInstance().updateServiceFee(e.getRowValue(), e.getNewValue(), null);
        } catch (SQLException | IOException ex) {
            logger.warn("Lỗi khi thực hiện câu lệnh SQL", ex);
            Announcement.show("Lỗi", "Không thể cập nhật tên phí dịch vụ!", "Lỗi kết nối cơ sở dữ liệu: " + ex.getMessage());
        }
    }
}
