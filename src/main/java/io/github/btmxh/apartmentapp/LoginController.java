package io.github.btmxh.apartmentapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.*;

public class LoginController {

    private static final Logger LOGGER = LogManager.getLogger();

    public TextField usernameTextField;
    public PasswordField passwordField;
    public Label statusLabel;

    public void login(ActionEvent actionEvent) {
        String username = usernameTextField.getText();
        String password = passwordField.getText();

        if (username.isEmpty()) {
            statusLabel.setText("Username cannot be empty!");
            passwordField.setText("");
        }
        else if (password.isEmpty()) {
            statusLabel.setText("Password cannot be empty!");
            usernameTextField.setText("");
        }
        else {
            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/apartment", "root", "200416");
                Statement st = conn.createStatement();
                String sql = "SELECT password FROM users WHERE username = \"" + username + "\";";
                ResultSet rs = st.executeQuery(sql);
                if (rs.next() && rs.getString("password").equals(password)) {
                    try {
                        Stage stage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
                        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/Application.fxml")));
                        stage.setScene(scene);
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    statusLabel.setText("The Username or Password is Incorrect. Try again!");
                    usernameTextField.setText("");
                    passwordField.setText("");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void switchToSignupScene(ActionEvent actionEvent) {
        try {
            Stage stage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/Signup.fxml")));
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}