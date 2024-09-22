package io.github.btmxh.apartmentapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class RegisterController {
    public Label loginMessageLabel;
    @FXML
    private TextField usernameRegTextField;

    @FXML
    public PasswordField passwordPasswordField;

    @FXML
    public PasswordField repasswordPasswordField;

    @FXML
    public Button signupButton;

    @FXML
    private Label returnLogin;

    public void setOnMouseClicked(MouseEvent mouseEvent) {
        try {
            Stage stage = (Stage) returnLogin.getScene().getWindow();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/login-view.fxml")));
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void signup(ActionEvent actionEvent) {

        String username = usernameRegTextField.getText();
        String password = passwordPasswordField.getText();
        String repassword = repasswordPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || repassword.isEmpty()) {
            loginMessageLabel.setText("Username and password cannot be empty!");
            usernameRegTextField.setText("");
            passwordPasswordField.setText("");
            repasswordPasswordField.setText("");
        }
        else if (!password.equals(repassword)) {
            loginMessageLabel.setText("Passwords are not matching. Try again!");
        }
        else {
            try {
                DatabaseConnection dbc = DatabaseConnection.getInstance();
                if (dbc.signup(username, password)) {
                    loginMessageLabel.setText("Register successfully!");
                }
                else {
                    loginMessageLabel.setText("Username has already been taken. Please choose another username!");
                    usernameRegTextField.setText("");
                    passwordPasswordField.setText("");
                    repasswordPasswordField.setText("");
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

