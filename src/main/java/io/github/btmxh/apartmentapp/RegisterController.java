package io.github.btmxh.apartmentapp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import java.io.IOException;

public class RegisterController {
    @FXML
    private PasswordField passwordRegPasswordField;

    @FXML
    private PasswordField repasswordRegPasswordField;

    @FXML
    private Button returnLogin;

    @FXML
    private TextField usernameRegTextField;

    @FXML
    private Button signUpButton;

    @FXML
    private void initialize() {
        signUpButton.setOnAction(event -> handleSignUp());
        returnLogin.setOnAction(event -> handleCancel());
    }

    private void handleSignUp() {
        String username = usernameRegTextField.getText().trim();
        String password = passwordRegPasswordField.getText().trim();
        String reenteredPassword = repasswordRegPasswordField.getText().trim();

        if (username.isEmpty()) {
            showAlert("Error", "Username không được để trống.");
            return;
        }
        if (password.isEmpty()) {
            showAlert("Error", "Password không được để trống.");
            return;
        }
        if (reenteredPassword.isEmpty()) {
            showAlert("Error", "Bạn phải nhập lại mật khẩu.");
            return;
        }

        if (!password.equals(reenteredPassword)) {
            showAlert("Error", "Mật khẩu nhập lại không khớp.");
            return;
        }

        processSignUp(username, password);
    }

    private void processSignUp(String username, String password) {
        showAlert("Succesful!", "Đăng ký thành công! Username: " + username);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
