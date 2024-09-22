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

public class LoginController {
    public Label clicktoRegister;
    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordPasswordField;

    @FXML
    private Button cancelButton;

    @FXML
    private Label loginMessageLabel;

    public void loginButtonOnActive(ActionEvent event) {

        String username = usernameTextField.getText();
        String password = passwordPasswordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            loginMessageLabel.setText("Username and password cannot be empty!");
            usernameTextField.setText("");
            passwordPasswordField.setText("");
        }
        else {
            try {
                DatabaseConnection dbc = DatabaseConnection.getInstance();
                if (dbc.login(username, password)) {
                    loginMessageLabel.setText("Login successfully!");
                }
                else {
                    loginMessageLabel.setText("The Username or Password is Incorrect. Try again!");
                    usernameTextField.setText("");
                    passwordPasswordField.setText("");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void cancelButtonOnActive(ActionEvent e) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void setMouseClicked(MouseEvent mouseEvent) {
        try {
            Stage stage = (Stage) clicktoRegister.getScene().getWindow();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/register-view.fxml")));
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
