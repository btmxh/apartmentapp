package io.github.btmxh.apartmentapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import java.sql.SQLException;

public class LoginController {
    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordPasswordField;

    @FXML
    private Label loginMessageLabel;

    @FXML
    private Hyperlink clicktoRegister;

    public void loginButtonOnActive(ActionEvent event) {

        String username = usernameTextField.getText();
        String password = passwordPasswordField.getText();

        if (username.isBlank() || password.isBlank()) {
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
                    loginMessageLabel.setText("The Username or Password is incorrect. Try again!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    public void initialize() {
        // Add a button click handler to navigate to the registration page
        clicktoRegister.setOnAction(event -> {
            try {
                Region registerRoot = FXMLLoader.load(getClass().getResource("/register-view.fxml"));
                Stage stage = (Stage) clicktoRegister.getScene().getWindow();
                stage.getScene().setRoot(registerRoot);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
