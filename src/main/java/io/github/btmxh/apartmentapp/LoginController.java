package io.github.btmxh.apartmentapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class LoginController {
    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordPasswordField;

    @FXML
    private Label loginMessageLabel;

    @FXML
    private Hyperlink clicktoRegister;

    public void loginButtonOnActive(ActionEvent e) {
        if (!usernameTextField.getText().isBlank() && !passwordPasswordField.getText().isBlank()) {
            loginMessageLabel.setText("You try login again!");
        } else {
            loginMessageLabel.setText("Please enter username and password.");
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
