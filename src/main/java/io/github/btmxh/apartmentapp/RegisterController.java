package io.github.btmxh.apartmentapp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class RegisterController {
    @FXML
    private Label returnLogin;

    @FXML
    public void initialize() {
        // Add a button click handler to navigate to the registration page
        returnLogin.setOnMouseClicked(event -> {
            try {
                Parent registerRoot = FXMLLoader.load(getClass().getResource("/login-view.fxml"));
                Scene registerScene = new Scene(registerRoot);
                Stage stage = (Stage) returnLogin.getScene().getWindow();
                stage.setScene(registerScene);
                stage.setTitle("Login");
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
