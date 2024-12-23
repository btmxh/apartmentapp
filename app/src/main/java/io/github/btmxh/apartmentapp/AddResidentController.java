package io.github.btmxh.apartmentapp;

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
import io.github.btmxh.apartmentapp.DatabaseConnection.Gender;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AddResidentController {
    private static final Logger logger = LogManager.getLogger(AddResidentController.class);

    @FXML
    private DatePicker dateField;

    @FXML
    private TextField fullNameField;

    @FXML
    private ComboBox<Gender> genderComboBox;

    @FXML
    private TextField nationField;

    @FXML
    private TextField passportIdField;

    @FXML
    private Label roomLabel;

    @FXML
    private Button selectRoomButton;

    private Stage stage;
    private Citizen citizen;

    @FXML
    public void initialize() {
        selectRoomButton.setOnAction(e -> selectRoom());
        genderComboBox.getItems().addAll(DatabaseConnection.Gender.values());
        genderComboBox.setCellFactory(param -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(DatabaseConnection.Gender item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDisplayName());
                }
            }
        });
        genderComboBox.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(DatabaseConnection.Gender item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDisplayName());
                }
            }
        });
    }

    private void selectRoom() {
        try {
            Room res = PickRoom.open(stage);
            if (res != null) {
                if (res.getName().equals(roomLabel.getText())) {
                    return;
                }
                roomLabel.setText(res.getName());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void cancelButton(ActionEvent actionEvent) {
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void submitButton(ActionEvent actionEvent) {
        try{
            final String fullName = fullNameField.getText().trim();
            final Gender gender = genderComboBox.getValue();
            final LocalDate dateOfBirth = dateField.getValue();
            final String nationality = nationField.getText().trim();
            final String passportId = passportIdField.getText().trim();
            final String room = roomField.getText().trim();
            if (fullname.isEmpty() || dateofbirth == null  || nationality.isEmpty() || passportId.isEmpty() || room.isEmpty()) {
                showAlert("Lỗi nhập liệu", "Vui lòng nhập đầy đủ thông tin trước khi bấm OK.");
                logger.warn("Một hoặc nhiều trường bị bỏ trống.");
                return;
            }

            if (room.isEmpty()) {
                Announcement.show("Thiếu thông tin", "Căn hộ không được để trống", "Vui lòng chọn căn hộ.");
                return;
            }

            citizen.setFullName(fullName);
            citizen.setGender(gender);
            citizen.setDateOfBirth(dateOfBirth);
            citizen.setNationality(nationality);
            citizen.setPassportId(passportId);
            citizen.setRoom(room);
            DatabaseConnection.getInstance().addCitizenToDB(citizen);

        } catch (SQLException ex) {
            logger.error("Error inserting data into database", ex);
            Announcement.show("Lỗi", "Không thể thêm cư dân vào CSDL", ex.getMessage());
            return;
        }

        stage.close();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setResident(Citizen citizen) {
        this.citizen = citizen;
        fullNameField.setText(citizen.getFullName());
        dateField.setValue(citizen.getDateOfBirth());
        genderComboBox.setValue(citizen.getGender());
        passportIdField.setText(citizen.getPassportId());
        nationField.setText(citizen.getNationality());
        roomLabel.setText(citizen.getRoom());
    }

    public static void open(Window window, Citizen citizen) throws IOException {
        final var loader = Utils.fxmlLoader("/add-resident.fxml");
        final Parent content = loader.load();
        final AddResidentController controller = loader.getController();
        final var stage = new Stage();
        if (citizen == null) {
            citizen = new Citizen(-1, "", LocalDate.now(), null,"", "","", LocalDateTime.now(), LocalDateTime.now());
        }

        stage.initOwner(window);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(new Scene(content));
        controller.setStage(stage);
        controller.setResident(citizen);
        stage.showAndWait();
    }
}
