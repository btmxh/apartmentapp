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
    private TextField emailRegTextField;

    @FXML
    private TextField phoneNumberRegTextField;

    @FXML
    private Button signUpButton;

    @FXML
    private void initialize() {
        signUpButton.setOnAction(_e -> handleSignUp());
        returnLogin.setOnAction(_e -> handleCancel());
        returnLogin.setOnMouseEntered(_e -> {
            returnLogin.setStyle("-fx-background-color: #b8919a; -fx-text-fill: #303D4F; -fx-border-radius: 15; -fx-background-radius: 15;");
        });
        signUpButton.setOnMouseEntered(_e -> {
            signUpButton.setStyle("-fx-background-color: #b8919a; -fx-text-fill: #303D4F; -fx-border-radius: 15; -fx-background-radius: 15;");
        });
        signUpButton.setOnMouseExited(_e -> {
            signUpButton.setStyle("-fx-background-color: #DBBCC3; -fx-text-fill: #303D4F; -fx-border-radius: 15; -fx-background-radius: 15;");
        });
        returnLogin.setOnMouseExited(_e -> {
            returnLogin.setStyle("-fx-background-color: #DBBCC3; -fx-text-fill: #303D4F; -fx-border-radius: 15; -fx-background-radius: 15;");
        });
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

    public static String validateSignUpInfo(String username, String password, String email, String phoneNumber, String reenteredPassword) {
        if (username.isEmpty()) {
            return "Tên người dùng không được để trống!";
        }

        if (password.isEmpty()) {
            return "Mật khẩu không được để trống!";
        }

        if(email.isEmpty()) {
            return "Email không được để trống!";
        }

        if(phoneNumber.isEmpty()) {
            return "Số điện thoại không được để trống!";
        }

        if(!validateEmail(email)) {
            return "Email không hợp lệ: " + email;
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
        String email = emailRegTextField.getText().trim();
        String password = passwordRegPasswordField.getText().trim();
        String phoneNumber = phoneNumberRegTextField.getText().trim();
        String reenteredPassword = repasswordRegPasswordField.getText().trim();

        String failReason = validateSignUpInfo(username, password, email, phoneNumber, reenteredPassword);
        if(failReason != null) {
            Announcement.show("Lỗi","Thông tin đăng ký không hợp lệ!", failReason);
            return;
        }

        processSignUp(username, email, phoneNumber, password);
    }

    private void processSignUp(String username, String email, String phoneNumber, String password) {

        DatabaseConnection dbc = DatabaseConnection.getInstance();
        try {
            if (dbc.signup(username, email, phoneNumber, password)) {
                Announcement.show("Thành công!", "Đăng ký thành công", "Người dùng đã đăng ký thành công " + username);
                if (dbc.getRole(username).equals("admin")) {
                    Announcement.show("Thông tin", "Tài khoản của bạn là quản trị viên.", "Tài khoản quản trị viên có các quyền đặc biệt, chẳng hạn như cấp quyền cho các tài khoản khác.");
                }
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

