package io.github.btmxh.apartmentapp;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;

public class AddRoomController {
    private static final Logger logger = LogManager.getLogger(AddServiceFeeController.class);

    @FXML private TextField nameField;
    @FXML private TextField ownerField;
    @FXML private TextField areaField;
    @FXML private TextField motorsField;
    @FXML private TextField carsField;
    @FXML private Button submitButton;
    @FXML private Button cancelButton;

    private Stage stage;
    private Room room;

    public void initialize() {
        submitButton.setOnAction(e -> handleSubmit());
        cancelButton.setOnAction(e -> handleCancel());
    }

    public void handleSubmit() {
        try {
            final var name = nameField.getText().trim();
            if (name.isEmpty()) {
                Announcement.show("Thiếu thông tin", "Tên căn hộ không được để trống", "Vui lòng nhập tên căn hộ.");
                return;
            }
            final var owner = ownerField.getText().trim();
            if (owner.isEmpty()) {
                Announcement.show("Thiếu thông tin", "Tên chủ căn hộ không được để trống", "Vui lòng nhập tên chủ căn hộ.");
                return;
            }

            float area;
            try {
                area = Float.parseFloat(areaField.getText().trim());
            }
            catch (NumberFormatException ex) {
                logger.warn("Nhập diện tích không thành công", ex);
                Announcement.show("Giá trị không hợp lệ", "Diện tích không đúng định dạng", "Vui lòng nhập diện tích hợp lệ.");
                return;
            }

            int motors;
            try {
                motors = Integer.parseInt(motorsField.getText().trim());
            }
            catch (NumberFormatException ex) {
                logger.warn("Nhập số lượng xe máy không thành công", ex);
                Announcement.show("Giá trị không hợp lệ", "Số lượng xe máy không đúng định dạng", "Vui lòng nhập số lượng xe máy hợp lệ.");
                return;
            }

            int cars;
            try {
                cars = Integer.parseInt(carsField.getText().trim());
            }
            catch (NumberFormatException ex) {
                logger.warn("Nhập số lượng ô tô không thành công", ex);
                Announcement.show("Giá trị không hợp lệ", "Số lượng ô tô không đúng định dạng", "Vui lòng nhập số lượng ô tô hợp lệ.");
                return;
            }

            room.setName(name);
            room.setOwnerName(owner);
            room.setArea(area);
            room.setNumMotors(motors);
            room.setNumCars(cars);
            DatabaseConnection.getInstance().updateRoom(room);
        }
        catch (SQLException | IOException ex) {
            logger.warn("Thêm căn hộ thất bại", ex);
            Announcement.show("Lỗi", "Không thể thêm căn hộ vào CSDL", ex.getMessage());
            return;
        }

        stage.close();
    }

    public void handleCancel() {
        stage.close();
    }

    public void setRoom(Room room) {
        this.room = room;
        if (room.getName() != null) {
            nameField.setText(room.getName());
        }
        if (room.getOwnerName() != null) {
            ownerField.setText(room.getOwnerName());
        }
        if (room.getArea() != 0) {
            areaField.setText(String.valueOf(room.getArea()));
        }
        if (room.getNumMotors() != -1) {
            motorsField.setText(String.valueOf(room.getNumMotors()));
        }
        if (room.getNumCars() != -1) {
            carsField.setText(String.valueOf(room.getNumCars()));
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public static void open(Window window, Room room) throws IOException {
        final var loader = Utils.fxmlLoader("/add-room.fxml");
        final Parent content = loader.load();
        final AddRoomController controller = loader.getController();
        final var stage = new Stage();
        if (room == null) {
            room = new Room(-1, null, null, 0, -1, -1);
        }

        stage.initOwner(window);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(new Scene(content));
        controller.setStage(stage);
        controller.setRoom(room);
        stage.showAndWait();
    }
}
