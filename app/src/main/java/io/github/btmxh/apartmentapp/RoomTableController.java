package io.github.btmxh.apartmentapp;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class RoomTableController {
    @FXML private TableView<Room> table;
    @FXML private TableColumn<Room, Room> noCol;
    @FXML private TableColumn<Room, String> nameCol;
    @FXML private TableColumn<Room, String> ownerCol;
    @FXML private TableColumn<Room, Float> areaCol;
    @FXML private TableColumn<Room, Integer> motorsCol;
    @FXML private TableColumn<Room, Integer> carsCol;

    private Runnable updateRooms = () -> {
    };

    public void initialize() {
        noCol.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue()));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        ownerCol.setCellValueFactory(new PropertyValueFactory<>("ownerName"));
        areaCol.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue().getArea()));
        motorsCol.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue().getNumMotors()));
        carsCol.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue().getNumCars()));
        table.setContextMenu(new ContextMenu(
                new MenuItem("Chỉnh sửa") {{
                    setOnAction(e -> {
                        final var room = table.getSelectionModel().getSelectedItem();
                        try {
                            AddRoomController.open(table.getScene().getWindow(), room);
                            updateRooms.run();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                }},
                new MenuItem("Xoá") {{
                    setOnAction(e -> {
                        for (final var room : table.getSelectionModel().getSelectedItems()) {
                            try {
                                DatabaseConnection.getInstance().removeRoom(room);
                            } catch (SQLException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                        updateRooms.run();
                    });
                }}
        ));
    }

    public void setRoomData(int start, ObservableList<Room> roomList, Runnable updateRooms) {
        Utils.initNoColumn(noCol, start);
        table.setItems(roomList);
        this.updateRooms = updateRooms;
    }
}
