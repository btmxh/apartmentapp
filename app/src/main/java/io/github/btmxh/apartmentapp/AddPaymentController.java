package io.github.btmxh.apartmentapp;

import io.github.btmxh.apartmentapp.DatabaseConnection.FeeType;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

public class AddPaymentController {
    private static final Logger logger = LogManager.getLogger(AddPaymentController.class);

    public Label roomLabel;
    public Button selectRoomButton;
    public Label feeLabel;
    public Button selectFeeButton;
    public TextField valueTextField;
    public Button submitButton;
    public Button cancelButton;

    private Stage stage;
    private Payment payment;
    private final SimpleObjectProperty<ServiceFee> fee = new SimpleObjectProperty<>(null);
    private final SimpleObjectProperty<Room> room = new SimpleObjectProperty<>(null);
    private static final SimpleObjectProperty<User> user = new SimpleObjectProperty<>(null);

    public void initialize() {
        selectRoomButton.setOnAction(e -> selectRoom());
        selectFeeButton.setOnAction(e -> selectFee());
        submitButton.setOnAction(e -> handleSubmit());
        cancelButton.setOnAction(e -> handleCancel());

        valueTextField.setText("");
        valueTextField.setDisable(true);
    }

    private void selectRoom() {
        try {
            Room res = PickRoom.open(stage);
            if (res != null) {
                if (room.get() != null && res.getId() == room.get().getId()) {
                    return;
                }
                fee.set(null);
                feeLabel.setText("Chưa chọn khoản thu");
                valueTextField.setDisable(true);
                valueTextField.setText("");
                this.room.set(res);
                roomLabel.setText(res.getName());
                PickServiceFee.setRoom(room.get());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void selectFee() {
        if (room.get() == null) {
            Announcement.show("Lỗi", "Căn hộ chưa được chọn", "Vui lòng chọn căn hộ.");
            return;
        }
        try {
            ServiceFee res = PickServiceFee.open(stage);
            if (res != null) {
                if (fee.get() != null && res.getId() == fee.get().getId()) {
                    return;
                }
                valueTextField.setDisable(true);
                valueTextField.setText("");
                fee.set(res);
                feeLabel.setText(this.fee.get().getName());

                switch(fee.get().getType()) {
                    case FeeType.MANAGEMENT, FeeType.SERVICE:
                        valueTextField.setText(String.valueOf((long) (fee.get().getValue1() * room.get().getArea())));
                        valueTextField.setDisable(true);
                        break;

                    case FeeType.PARKING:
                        valueTextField.setText(String.valueOf(fee.get().getValue1() * room.get().getNumMotors() + fee.get().getValue2() * room.get().getNumCars()));
                        valueTextField.setDisable(true);
                        break;

                    case FeeType.DONATION:
                        valueTextField.setText("");
                        valueTextField.setDisable(false);
                        break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleSubmit() {
        try {
            if (room.get() == null || fee.get() == null) {
                Announcement.show("Thiếu thông tin", "Chưa chọn căn hộ", "Vui lòng chọn căn hộ và khoản thu trước khi xác nhận.");
                return;
            } else if (fee.get() == null) {
                Announcement.show("Thiếu thông tin", "Chưa chọn khoản thu", "Vui lòng chọn khoản thu trước khi xác nhận.");
            } else {
                long value;
                try {
                    value = Long.parseLong(valueTextField.getText().trim());
                } catch (NumberFormatException ex) {
                    logger.warn("Nhập số tiền không thành công", ex);
                    Announcement.show("Giá trị không hợp lệ", "Số tiền không đúng định dạng", "Vui lòng nhập số tiền hợp lệ (chỉ bao gồm số).");
                    return;
                }
                payment.setValue(value);
                payment.setRoom(room.get());
                payment.setFee(fee.get());
                payment.setUser(user.get());
                DatabaseConnection.getInstance().updatePayment(payment);
            }
        }
        catch (SQLException | IOException ex) {
            logger.warn("Thêm thanh toán thất bại", ex);
            Announcement.show("Lỗi", "Không thể thêm thanh toán vào CSDL", ex.getMessage());
            return;
        }
        stage.close();
    }

    public void handleCancel() {
        stage.close();
    }

    public static void setUser(User user) {
        AddPaymentController.user.set(user);
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
        if (payment.getRoom() != null) {
            roomLabel.setText(payment.getRoom().getName());
            room.set(payment.getRoom());
        }
        if (payment.getFee() != null) {
            fee.set(payment.getFee());
            feeLabel.setText(payment.getFee().getName());
            if (payment.getFee().getType() == FeeType.DONATION) {
                valueTextField.setDisable(false);
            }
        }
        if (payment.getValue() != 0) {
            valueTextField.setText(String.valueOf(payment.getValue()));
        }
        user.set(payment.getUser());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public static void open(Window window, Payment payment) throws IOException {
        final var loader = Utils.fxmlLoader("/add-payment.fxml");
        final Parent content = loader.load();
        final AddPaymentController controller = loader.getController();
        final var stage = new Stage();
        if (payment == null) {
            payment = new Payment(-1, null, user.get(), null, 0, LocalDateTime.now());
        }

        stage.initOwner(window);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(new Scene(content));
        controller.setStage(stage);
        controller.setPayment(payment);
        stage.showAndWait();
    }
}
