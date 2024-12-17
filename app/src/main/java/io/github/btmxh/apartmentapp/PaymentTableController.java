package io.github.btmxh.apartmentapp;

import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class PaymentTableController {
    public TableView<Payment> table;
    public TableColumn<Payment, Payment> noCol;
    public TableColumn<Payment, String> nameCol;
    public TableColumn<Payment, String> roomCol;
    public TableColumn<Payment, Long> amountCol;
    public TableColumn<Payment, String> roomOwnerCol;

    public void initialize() {
        noCol.setCellValueFactory(feat -> new ReadOnlyObjectWrapper<>(feat.getValue()));
        nameCol.setCellValueFactory(f -> f.getValue().getFee().name());
        roomCol.setCellValueFactory(f -> f.getValue().roomIdProperty());
        roomOwnerCol.setCellValueFactory(f -> f.getValue().roomOwnerProperty());
    }

    public void setPage(int start, ObservableList<Payment> payments) {
        Utils.initNoColumn(noCol, start);
        table.setItems(payments);
    }
}
