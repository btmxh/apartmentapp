package io.github.btmxh.apartmentapp;

import io.github.btmxh.apartmentapp.DatabaseConnection.FeeType;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.beans.value.ObservableValue;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class AddServiceFeeController {
    private static final Logger logger = LogManager.getLogger(AddServiceFeeController.class);
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private ComboBox<FeeType> typeComboBox;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField value1TextField;
    @FXML
    private TextField value2TextField;
    @FXML
    private Label motorLabel;
    @FXML
    private Label carLabel;
    @FXML
    private DatePicker startDatePicker;

    private ServiceFee fee;
    private Stage stage;

    public void initialize() {
        value1TextField.setDisable(true);
        value2TextField.setVisible(false);
        motorLabel.setVisible(false);
        carLabel.setVisible(false);
        typeComboBox.getItems().addAll(FeeType.values());
        typeComboBox.setCellFactory(param -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(FeeType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDisplayName());
                }
            }
        });
        typeComboBox.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(FeeType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDisplayName());
                }
            }
        });
        typeComboBox.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<FeeType>() {
                    @Override
                    public void changed(ObservableValue<? extends FeeType> observable, FeeType oldValue, FeeType newValue) {
                        if (newValue == FeeType.PARKING) {
                            value2TextField.setVisible(true);
                            motorLabel.setVisible(true);
                            carLabel.setVisible(true);
                        }
                        else {
                            value2TextField.setVisible(false);
                            motorLabel.setVisible(false);
                            carLabel.setVisible(false);
                        }
                        value1TextField.setDisable(newValue == FeeType.DONATION);
                    }
                }
        );
    }

    public void cancel(ActionEvent e) {
        stage.close();
    }

    public void submit(ActionEvent e) {
        try {
            final var type = typeComboBox.getValue();
            if (type == null) {
                Announcement.show("Thiếu thông tin", "Loại khoản thu chưa được chọn", "Vui lòng chọn loại khoản thu.");
                return;
            }
            final var name = nameTextField.getText().trim();
            if (name.isEmpty()) {
                Announcement.show("Thiếu thông tin", "Tên khoản thu không được để trống", "Vui lòng nhập tên khoản thu.");
                return;
            }

            final var startDate = startDatePicker.getValue();
            if (startDate == null) {
                Announcement.show("Thiếu thông tin", "Ngày bắt đầu không được để trống", "Vui lòng chọn ngày bắt đầu thu.");
                return;
            }

            final var deadline = endDatePicker.getValue();
            if (deadline == null) {
                Announcement.show("Thiếu thông tin", "Hạn cuối không được để trống", "Vui lòng chọn hạn cuối.");
                return;
            }

            if (!deadline.isAfter(startDate)) {
                Announcement.show("Dữ liệu không hợp lệ", "Hạn cuối không hợp lệ", "Hạn cuối phải sau ngày bắt đầu. Vui lòng chọn lại.");
                return;
            }

            long value1 = 0, value2 = 0;
            try {
                value1 = Long.parseLong(value1TextField.getText().trim());
            }
            catch (NumberFormatException ex) {
                logger.warn("Nhập số tiền không thành công", ex);
                Announcement.show("Giá trị không hợp lệ", "Số tiền không đúng định dạng", "Vui lòng nhập số tiền hợp lệ (chỉ bao gồm số).");
                return;
            }

            if (type == FeeType.PARKING) {
                try {
                    value2 = Long.parseLong(value2TextField.getText().trim());
                }
                catch (NumberFormatException ex) {
                    logger.warn("Nhập số tiền không thành công", ex);
                    Announcement.show("Giá trị không hợp lệ", "Số tiền không đúng định dạng", "Vui lòng nhập số tiền hợp lệ (chỉ bao gồm số).");
                    return;
                }
            }

            fee.setType(type);
            fee.setName(name);
            fee.setStartDate(startDate);
            fee.setDeadline(deadline);
            fee.setValue1(value1);
            fee.setValue2(value2);
            logger.info(type.getDisplayName());
            logger.info(name);
            logger.info(startDate);
            logger.info(deadline);
            logger.info(value1);
            logger.info(value2);
            DatabaseConnection.getInstance().updateServiceFee1(fee);
        } catch (SQLException | IOException ex) {
            logger.warn("Thêm khoản thu thất bại", ex);
            Announcement.show("Lỗi", "Không thể thêm khoản thu vào CSDL", ex.getMessage());
            return;
        }

        stage.close();
    }

    public static void open(Window window, ServiceFee fee) throws IOException {
        final var loader = Utils.fxmlLoader("/add-service-fee.fxml");
        final Parent content = loader.load();
        final AddServiceFeeController controller = loader.getController();
        final var stage = new Stage();
        if (fee == null) {
            fee = new ServiceFee(ServiceFee.NULL_ID, "", -1, LocalDate.now(), LocalDate.now());
        }

        stage.initOwner(window);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(new Scene(content));
        controller.setStage(stage);
        controller.setServiceFee(fee);
        stage.showAndWait();
    }

    public void setServiceFee(ServiceFee fee) {
        this.fee = fee;
//        nameField.setText(fee.getName());
//        if(fee.getAmount() >= 0) {
//            valueField.setText(String.valueOf(fee.getAmount()));
//        } else {
//            valueField.setText("");
//        }
//        constAmount.setSelected(fee.getAmount() < 0);
//        startDatePicker.setValue(fee.getStartDate());
//        deadlinePicker.setValue(fee.getDeadline());

    }

    private void setStage(Stage stage) {
        this.stage = stage;
    }
}
