package io.github.btmxh.apartmentapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.commons.compiler.CompileException;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class AddServiceFeeController {
    private static final Logger logger = LogManager.getLogger();
    @FXML
    private CheckBox constAmount;
    @FXML
    private DatePicker deadlinePicker;
    @FXML
    private DatePicker startDatePicker;

    @FXML
    private TextField nameField;
    @FXML
    private TextField valueField;

    private ServiceFee fee;
    private Stage stage;

    public void initialize() {
        valueField.disableProperty().bind(constAmount.selectedProperty());
    }

    public void cancel(ActionEvent e) {
        stage.close();
    }

    public void submit(ActionEvent e) {
        try {
            final var name = nameField.getText().trim();
            if (name.isEmpty()) {
                Announcement.show("Thiếu thông tin", "Tên khoản phí không được để trống", "Vui lòng nhập tên khoản phí.");
                return;
            }

            int amount;
            try {
                amount = Integer.parseInt(valueField.getText().trim());
            } catch (NumberFormatException ex) {
                logger.warn("Unable to parse fee amount", ex);
                Announcement.show("Giá trị không hợp lệ", "Số tiền không đúng định dạng", "Vui lòng nhập số tiền hợp lệ (chỉ bao gồm số).");
                return;
            }

            final var startDate = startDatePicker.getValue();
            if (startDate == null) {
                Announcement.show("Thiếu thông tin", "Ngày bắt đầu không được để trống", "Vui lòng chọn ngày bắt đầu thu.");
                return;
            }

            final var deadline = deadlinePicker.getValue();
            if (deadline == null) {
                Announcement.show("Thiếu thông tin", "Hạn cuối không được để trống", "Vui lòng chọn hạn cuối.");
                return;
            }

            if (!deadline.isAfter(startDate)) {
                Announcement.show("Dữ liệu không hợp lệ", "Hạn cuối không hợp lệ", "Hạn cuối phải sau ngày bắt đầu. Vui lòng chọn lại.");
                return;
            }

            fee.setName(nameField.getText());
            long oldAmount = fee.getAmount();
            if(constAmount.isSelected()) {
                fee.setAmount(-1);
            } else {
                fee.setAmount(amount);
            }
            fee.setStartDate(startDate);
            fee.setDeadline(deadline);
            DatabaseConnection.getInstance().updateServiceFee(fee, oldAmount);
        } catch (SQLException | IOException ex) {
            logger.warn("Unable to add service fee", ex);
            Announcement.show("Lỗi", "Không thể thêm phí dịch vụ vào CSDL", ex.getMessage());
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
        nameField.setText(fee.getName());
        if(fee.getAmount() >= 0) {
            valueField.setText(String.valueOf(fee.getAmount()));
        } else {
            valueField.setText("");
        }
        constAmount.setSelected(fee.getAmount() < 0);
        startDatePicker.setValue(fee.getStartDate());
        deadlinePicker.setValue(fee.getDeadline());

    }

    private void setStage(Stage stage) {
        this.stage = stage;
    }
}
