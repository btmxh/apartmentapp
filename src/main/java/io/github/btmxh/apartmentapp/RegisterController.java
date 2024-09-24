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
import java.sql.SQLException;

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
    private TextField emailRegTextField;

    @FXML
    private TextField phoneNumberRegTextField;

    @FXML
    private Button signUpButton;

    @FXML
    private void initialize() {
        signUpButton.setOnAction(event -> handleSignUp());
        returnLogin.setOnAction(event -> handleCancel());
    }

    private boolean validatePhoneNumber(String phoneNumber) {
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

    private void handleSignUp() {
        String username = usernameRegTextField.getText().trim();
        String email = emailRegTextField.getText().trim();
        String password = passwordRegPasswordField.getText().trim();
        String phoneNumber = phoneNumberRegTextField.getText().trim();
        String reenteredPassword = repasswordRegPasswordField.getText().trim();

        if (username.isEmpty()) {
            showAlert("Error", "Username must not be empty");
            return;
        }

        if (password.isEmpty()) {
            showAlert("Error", "Password must not be empty");
            return;
        }

        if(email.isEmpty()) {
            showAlert("Error", "Email must not be empty");
            return;
        }

        if(phoneNumber.isEmpty()) {
            showAlert("Error", "Phone number must not be empty");
            return;
        }

        if(!validatePhoneNumber(phoneNumber)) {
            showAlert("Error", "Invalid phone number: " + phoneNumber);
            return;
        }

        if (reenteredPassword.isEmpty()) {
            showAlert("Error", "Please reenter password");
            return;
        }

        if (!password.equals(reenteredPassword)) {
            showAlert("Error", "Password does not match");
            return;
        }

        processSignUp(username, email, phoneNumber, password);
    }

    private void processSignUp(String username, String email, String phoneNumber, String password) {
        DatabaseConnection dbc = DatabaseConnection.getInstance();
        try {
            if(dbc.signup(username, email, phoneNumber, password)) {
                showAlert("Successful!", "Successful registered user " + username);
            } else {
                showAlert("Error", "Username " + username + " has already been taken. Please choose another username");
            }
        } catch (SQLException e) {
            showAlert("Error", "Unable to sign up");
            e.printStackTrace();
        }
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

