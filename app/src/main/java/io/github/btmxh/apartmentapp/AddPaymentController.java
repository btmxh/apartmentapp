package io.github.btmxh.apartmentapp;

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
    private final SimpleStringProperty room = new SimpleStringProperty(null);

    public void initialize() {
        selectRoomButton.setOnAction(e -> selectRoom());
        selectFeeButton.setOnAction(e -> selectFee());
        submitButton.setOnAction(e -> handleSubmit());
        cancelButton.setOnAction(e -> handleCancel());

//        roomField.setOnAction(e -> {
//            try {
//                final var owner = DatabaseConnection.getInstance().getRoomOwner(roomField.getText());
//                if (owner != null) {
//                    roomOwner.setText(owner);
//                    room.set(roomField.getText());
//                    return;
//                }
//
//                Announcement.show("Lỗi", "Phòng chưa có nhân khẩu", "Hãy kiểm tra lại số phòng");
//                roomField.setText("");
//            } catch (SQLException | IOException ex) {
//                throw new RuntimeException(ex);
//            }
//        });
    }

    private void selectRoom() {
        logger.debug("Loi o day");
        try {
            this.room.set(PickRoom.open(stage));
            logger.debug("Loi o day 1");
            if (this.room.get() != null) {
                roomLabel.setText(this.room.get());
            }
        } catch (IOException e) {
            logger.debug("Loi o day 2");
            throw new RuntimeException(e);
        }
        logger.debug("Loi o day 3");
    }

    private void selectFee() {

    }

    public void handleSubmit() {

    }

    public void handleCancel() {
        stage.close();
    }

//    public void submit(ActionEvent actionEvent) {
//        try {
//            if (fee.get() == null) {
//                Announcement.show("Thiếu thông tin", "Khoản phí chưa được chọn", "Vui lòng chọn khoản phí.");
//                return;
//            }
//
//            if (room.get() == null) {
//                Announcement.show("Thiếu thông tin", "Chưa chọn phòng thanh toán", "Vui lòng chọn phòng thanh toán.");
//                return;
//            }
//
//            long amount = fee.get().getAmount();
//            if (amount < 0) {
//                try {
//                    amount = Long.parseLong(valueField.getText().trim());
//                } catch (NumberFormatException ex) {
//                    logger.warn("Unable to parse fee amount", ex);
//                    Announcement.show("Giá trị không hợp lệ", "Số tiền không đúng định dạng", "Vui lòng nhập số tiền hợp lệ (chỉ bao gồm số).");
//                    return;
//                }
//            }
//
//            final var owner = DatabaseConnection.getInstance().getRoomOwner(roomField.getText());
//            if (owner == null) {
//                Announcement.show("Lỗi", "Phòng chưa có nhân khẩu", "Hãy kiểm tra lại số phòng");
//                return;
//            }
//
//            if(DatabaseConnection.getInstance().paymentExists(fee.get().getId(), room.get())) {
//                Announcement.show("Lỗi", "Khoản thu này đã được thanh toán", "Hãy kiểm tra lại thông tin");
//                return;
//            }
//
//            payment.setFee(fee.get());
//            payment.setRoomId(room.get());
//            payment.setAmount(fee.get().getAmount() <= 0 ? amount : -1);
//
//            DatabaseConnection.getInstance().updatePayment(payment);
//        } catch (SQLException | IOException ex) {
//            logger.warn("Unable to add service fee", ex);
//            Announcement.show("Lỗi", "Không thể thêm phí dịch vụ vào CSDL", ex.getMessage());
//            return;
//        }
//
//        stage.close();
//    }
//
//    public void selectFee(ActionEvent actionEvent) {
//        try {
//            this.fee.set(PickServiceFee.open(stage));
//            if (this.fee.get() != null) {
//                feeName.setText(this.fee.get().getName());
//                valueField.setDisable(fee.get().getAmount() >= 0);
//                if(this.fee.get().getAmount() >= 0) {
//                    valueField.setText(String.valueOf(this.fee.get().getAmount()));
//                }
//            } else {
//                feeName.setText("Chưa có khoản thu");
//                valueField.setDisable(true);
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public void setPayment(Payment p) {
        payment = p;
        fee.set(p.getFee());
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
            payment = new Payment(-1, null, "", -1, LocalDateTime.now(), "");
        }

        stage.initOwner(window);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(new Scene(content));
        controller.setStage(stage);
        controller.setPayment(payment);
        stage.showAndWait();
    }
}
