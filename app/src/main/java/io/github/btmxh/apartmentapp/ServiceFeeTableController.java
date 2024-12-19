package io.github.btmxh.apartmentapp;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class ServiceFeeTableController {
    private static final Logger logger = LogManager.getLogger(ServiceFeeTableController.class);
    @FXML
    private TableView<ServiceFee> table;
    @FXML
    private TableColumn<ServiceFee, ServiceFee> noCol;
    @FXML
    private TableColumn<ServiceFee, String> nameCol;
    @FXML
    private TableColumn<ServiceFee, String> typeCol;
    @FXML
    private TableColumn<ServiceFee, LocalDate> startDateCol;
    @FXML
    private TableColumn<ServiceFee, LocalDate> deadlineCol;
    @FXML
    private TableColumn<ServiceFee, Void> viewCol;

    private Runnable updateFees = () -> {
    };

    public void initialize() {
        noCol.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue()));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeCol.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue().getType().getDisplayName()));
        startDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        deadlineCol.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        table.setContextMenu(new ContextMenu(
                new MenuItem("Chỉnh sửa") {{
                    setOnAction(e -> {
                        final var fee = table.getSelectionModel().getSelectedItem();
                        try {
                            AddServiceFeeController.open(table.getScene().getWindow(), fee);
                            updateFees.run();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                }},
                new MenuItem("Xoá") {{
                    setOnAction(e -> {
                        for (final var fee : table.getSelectionModel().getSelectedItems()) {
                            try {
                                DatabaseConnection.getInstance().removeServiceFee(fee);
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
}
