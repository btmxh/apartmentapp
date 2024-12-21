package io.github.btmxh.apartmentapp;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.sql.SQLException;

public class PaymentTableController {
    public TableView<Payment> table;
    public TableColumn<Payment, Payment> noCol;
    public TableColumn<Payment, String> nameCol;
    public TableColumn<Payment, String> roomCol;
    public TableColumn<Payment, Long> valueCol;
    public TableColumn<Payment, String> userCol;

    private Runnable updatePayments = () -> {
    };

    public void initialize() {
        noCol.setCellValueFactory(feat -> new ReadOnlyObjectWrapper<>(feat.getValue()));
        nameCol.setCellValueFactory(f -> f.getValue().getFee().name());
        roomCol.setCellValueFactory(f -> f.getValue().getRoom().name());
        valueCol.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue().getValue()));
        userCol.setCellValueFactory(f -> f.getValue().getUser().fullname());

        table.setContextMenu(new ContextMenu(
                new MenuItem("Chỉnh sửa") {{
                    setOnAction(e -> {
                        final var payment = table.getSelectionModel().getSelectedItem();
                        try {
                            AddPaymentController.open(table.getScene().getWindow(), payment);
                            updatePayments.run();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                }},
                new MenuItem("Xoá") {{
                    setOnAction(e -> {
                        for (final var payment : table.getSelectionModel().getSelectedItems()) {
                            try {
                                DatabaseConnection.getInstance().removePayment(payment);
                            } catch (SQLException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                        updatePayments.run();
                    });
                }}
        ));
    }

    public void setPaymentData(int start, ObservableList<Payment> paymentList, Runnable updatePayments) {
        Utils.initNoColumn(noCol, start);
        table.setItems(paymentList);
        this.updatePayments = updatePayments;
    }
}
