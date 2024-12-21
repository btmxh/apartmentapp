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
            DatabaseConnection dc = DatabaseConnection.getInstance();
            String username = userNameField.getText();
            String oldPass = oldPassword.getText();
            String newPass = newPassword.getText();
            String confirmPass = reNewPassword.getText();
            String phone = phoneField.getText();

            // Kiểm tra xem các trường mật khẩu có được điền hay không
            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin các trường mật khẩu!");
                return;
            }

            // Kiểm tra mật khẩu cũ
            if (!isOldPasswordCorrect(oldPass, username)) {
                showAlert("Lỗi", "Mật khẩu cũ không chính xác!");
                return;
            }

            // Kiểm tra mật khẩu mới và xác nhận mật khẩu
            if (!newPass.equals(confirmPass)) {
                showAlert("Lỗi", "Mật khẩu mới và xác nhận mật khẩu không khớp!");
                return;
            }

            if (updatePassword(newPass, username)) {

                showAlert("Thành công", "Mật khẩu đã được thay đổi thành công!");

            } else {
                showAlert("Lỗi", "Có lỗi xảy ra khi thay đổi mật khẩu. Vui lòng thử lại!");
            }

            if(updatePhoneNum(phone, username)){
                showAlert("Thành công", "Số điện thoại đã được thay đổi thành công!");
                String phoneNum = currentUser.getPhoneNum();
                phoneField.setText(phoneNum);
//                phoneField.setText(dc.getPhoneNumberAfterOK(username));
            } else {
                showAlert("Lỗi", "Có lỗi xảy ra khi thay đổi số điện thoại. Vui lòng thử lại!");
            }
            if (updatePassword(newPass, username) && updatePhoneNum(phone, username)) {
                stage.close();
            }

        }
        catch (SQLException e) {
            logger.warn("Thay đổi thông tin thất bại", e);
            showAlert("Lỗi", "Không thể thay đổi thông tin người dùng. Vui lòng thử lại!");
        }
    }

    private boolean isOldPasswordCorrect(String oldPass, String name) {
        DatabaseConnection dc = DatabaseConnection.getInstance();
        try {
            return oldPass.equals(dc.getUserPasswordByPhoneNumber(name));
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean updatePassword(String newPass, String name) throws SQLException {
        DatabaseConnection dc = DatabaseConnection.getInstance();
        try {
            dc.setPassword(newPass, name);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePhoneNum(String phone, String name) throws SQLException {
        DatabaseConnection dc = DatabaseConnection.getInstance();
        try {
            dc.setPhoneNum(phone, name);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
