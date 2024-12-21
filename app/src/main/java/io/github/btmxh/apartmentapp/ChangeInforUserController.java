package io.github.btmxh.apartmentapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ChangeInforUserController {
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private TextField fullNameUser;

    @FXML
    private PasswordField newPassword;

    @FXML
    private PasswordField oldPassword;

    @FXML
    private TextField phoneField;

    @FXML
    private PasswordField reNewPassword;

    @FXML
    private TextField userNameField;

    private Stage stage;

    private User currentUser;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void cancelButton(ActionEvent e){
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setUser(User user) {
        this.currentUser = user;
        userNameField.setText(user.getName());
        userNameField.setDisable(true);
        fullNameUser.setText(user.getFullname());
        fullNameUser.setDisable(true);
        phoneField.setText(user.getPhoneNum());
        phoneField.setDisable(true);
    }

    public static void open(Window window, User user) throws IOException {
        final var loader = Utils.fxmlLoader("/change-user-form.fxml");
        final Parent content = loader.load();
        final ChangeInforUserController controller = loader.getController();
        final var stage = new Stage();

        stage.initOwner(window);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(new Scene(content));
        controller.setStage(stage);
        controller.setUser(user);
        stage.showAndWait();
    }

    @FXML
    void submitButton(ActionEvent event) {
        try {
            String oldPass = oldPassword.getText();
            String newPass = newPassword.getText();
            String confirmPass = reNewPassword.getText();

            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Announcement.show("Thiếu thông tin", "Mật khẩu cũ, Mật khẩu mới và Nhập lại mật khẩu mới không được để trống", "Vui lòng kiểm tra lại.");
                return;
            }

            if (!oldPass.equals(DatabaseConnection.getInstance().getUserPassword(currentUser.getName()))) {
                Announcement.show("Sai thông tin", "Mật khẩu cũ không chính xác", "Vui lòng kiểm tra lại.");
                return;
            }

            if (!newPass.equals(confirmPass)) {
                Announcement.show("Sai thông tin", "Mật khẩu mới và Nhập lại mật khẩu mới không giống nhau", "Vui lòng kiểm tra lại.");
                return;
            }

            DatabaseConnection.getInstance().setPassword(newPass, currentUser.getName());
        }
        catch (SQLException ex) {
            logger.warn("Thay đổi mật khẩu thất bại", ex);
            Announcement.show("Lỗi", "Không thể thay đổi mật khẩu", ex.getMessage());
            return;
        }

        stage.close();
    }

//    private boolean isOldPasswordCorrect(String oldPass, String name) {
//        DatabaseConnection dc = DatabaseConnection.getInstance();
//        try {
//            return oldPass.equals(dc.getUserPasswordByPhoneNumber(name));
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    public boolean updatePassword(String newPass, String name) throws SQLException {
//        DatabaseConnection dc = DatabaseConnection.getInstance();
//        try {
//            dc.setPassword(newPass, name);
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    public boolean updatePhoneNum(String phone, String name) throws SQLException {
//        DatabaseConnection dc = DatabaseConnection.getInstance();
//        try {
//            dc.setPhoneNum(phone, name);
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
}
