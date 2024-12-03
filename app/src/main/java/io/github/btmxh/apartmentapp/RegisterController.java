package io.github.btmxh.apartmentapp;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

public class RegisterController {

    private static final Logger logger = LogManager.getLogger();

    @FXML
    private PasswordField passwordRegPasswordField;

    @FXML
    private PasswordField repasswordRegPasswordField;

    @FXML
    private Button returnLogin;

    @FXML
    private TextField usernameRegTextField;

    @FXML
    private TextField nameRegTextField;

    @FXML
    private TextField phoneNumberRegTextField;

    @FXML
    private Button signUpButton;

    @FXML
    private void initialize() {
        signUpButton.setOnAction(_e -> handleSignUp());
        returnLogin.setOnAction(_e -> handleCancel());
    }

    private static boolean validateEmail(String email){
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
    private static boolean validatePhoneNumber(String phoneNumber) {
        if(phoneNumber.length() != 10 && phoneNumber.length() != 11) {
            return false;
        }

        for(int i = 0; i < phoneNumber.length(); i++) {
            char c = phoneNumber.charAt(i);
            if(c < '0' || c > '9') {
                return false;
            }
        }

        return true;
    }

    public static String validateSignUpInfo(String username, String password, String name, String phoneNumber, String reenteredPassword) {
        if (username.isEmpty()) {
            return "Tên người dùng không được để trống!";
        }

        if (password.isEmpty()) {
            return "Mật khẩu không được để trống!";
        }

        if(name.isEmpty()) {
            return "Họ và tên không được để trống!";
        }

        if(phoneNumber.isEmpty()) {
            return "Số điện thoại không được để trống!";
        }

        if(!validatePhoneNumber(phoneNumber)) {
            return "Số điện thoại không hợp lệ: " + phoneNumber;
        }

        if (reenteredPassword.isEmpty()) {
            return "Vui lòng nhập lại mật khẩu!";
        }

        if (!password.equals(reenteredPassword)) {
            return "Mật khẩu không khớp!";
        }

        return null;
    }

    private void handleSignUp() {
        String username = usernameRegTextField.getText().trim();
        String name = nameRegTextField.getText().trim();
        String password = passwordRegPasswordField.getText().trim();
        String phoneNumber = phoneNumberRegTextField.getText().trim();
        String reenteredPassword = repasswordRegPasswordField.getText().trim();

        String failReason = validateSignUpInfo(username, password, name, phoneNumber, reenteredPassword);
        if(failReason != null) {
            Announcement.show("Lỗi","Thông tin đăng ký không hợp lệ!", failReason);
            return;
        }

        processSignUp(username, name, phoneNumber, password);
    }

    private void processSignUp(String username, String name, String phoneNumber, String password) {

        DatabaseConnection dbc = DatabaseConnection.getInstance();
        try {
            if (dbc.signup(username, name, phoneNumber, password)) {
                Announcement.show("Thành công!", "Đăng ký thành công", "Người dùng đã đăng ký thành công " + username);
                if (dbc.getRole(username).equals("admin")) {
                    Announcement.show("Thông tin", "Tài khoản của bạn là quản trị viên.", "Tài khoản quản trị viên có các quyền đặc biệt, chẳng hạn như cấp quyền cho các tài khoản khác.");
                }
                handleCancel();
            } else {
                Announcement.show("Lỗi", "Lỗi đăng ký","Tên người dùng " + username + " đã được sử dụng. Vui lòng chọn tên người dùng khác!");
            }
        }
        catch (SQLException e) {
            logger.warn("Lỗi khi thực hiện câu lệnh SQL", e);
            Announcement.show("Lỗi", "Không thể đăng ký","Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
        }
    }

    private void handleCancel() {
        // Quay lại trang login
        try {
            // Tải file FXML của trang đăng nhập
            Region loginPage = FXMLLoader.load(getClass().getResource("/login-view.fxml"));

            // Lấy Stage hiện tại từ nút Cancel
            Stage stage = (Stage) returnLogin.getScene().getWindow();

            // Đặt Scene mới với trang đăng nhập
            stage.getScene().setRoot(loginPage);

        } catch (Exception e) {
            logger.error("Lỗi khi tải tệp FXML", e);
            Announcement.show("Lỗi","Không thể truy cập vào trang đăng nhập!", "FXML loading error: " + e.getMessage());
        }
    }
}

