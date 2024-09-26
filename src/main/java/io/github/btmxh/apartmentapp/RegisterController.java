package io.github.btmxh.apartmentapp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Matcher;

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
        signUpButton.setOnAction(event -> handleSignUp());
        returnLogin.setOnAction(event -> handleCancel());
    }

    private boolean validateEmail(String email){
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
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
            Announcement.show("Error", "Username must not be empty");
            return;
        }

        if (password.isEmpty()) {
            Announcement.show("Error", "Password must not be empty");
            return;
        }

        if(email.isEmpty()) {
            Announcement.show("Error", "Email must not be empty");
            return;
        }

        if(phoneNumber.isEmpty()) {
            Announcement.show("Error", "Phone number must not be empty");
            return;
        }

        if(!validateEmail(email)) {
            Announcement.show("Error", "Invalid email");
            return;
        }

        if(!validatePhoneNumber(phoneNumber)) {
            Announcement.show("Error", "Invalid phone number: " + phoneNumber);
            return;
        }

        if (reenteredPassword.isEmpty()) {
            Announcement.show("Error", "Please reenter password");
            return;
        }

        if (!password.equals(reenteredPassword)) {
            Announcement.show("Error", "Password does not match");
            return;
        }

        processSignUp(username, email, phoneNumber, password);
    }

    private void processSignUp(String username, String email, String phoneNumber, String password) {

        DatabaseConnection dbc = DatabaseConnection.getInstance();
        if(dbc.signup(username, email, phoneNumber, password)) {
            Announcement.show("Successful!", "Successful registered user " + username);
        } else {
            Announcement.show("Error", "Username " + username + " has already been taken. Please choose another username");
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
            logger.error("Error during loading FXML file", e);
            Announcement.show("Error", "Unable to reach log in page");
        }
    }
}

