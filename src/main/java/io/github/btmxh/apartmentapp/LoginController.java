package io.github.btmxh.apartmentapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

public class LoginController {

    private static final Logger logger = LogManager.getLogger();

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
            DatabaseConnection dbc = DatabaseConnection.getInstance();
            try {
                if (dbc.login(username, password)) {
                    loginMessageLabel.setText("Login successfully!");
                } else {
                    loginMessageLabel.setText("The Username or Password is incorrect. Try again!");
                }
            }
            catch (SQLException e) {
                logger.warn("Error during executing SQL statement", e);
                Announcement.show("Error", "Unable to log in","Database connection error: " + e.getMessage());
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
                logger.fatal("Error loading FXML file", e);
                Announcement.show("Error","Unable to reach sign up page", "FXML loading error: " + e.getMessage());
            }
        });
    }
}
