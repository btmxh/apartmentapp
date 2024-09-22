package io.github.btmxh.apartmentapp;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.*;

public class SignupController {

    private static final Logger LOGGER = LogManager.getLogger();

    public TextField usernameTextField;
    public PasswordField passwordField;
    public Label statusLabel;

    private boolean signup(Connection conn, String username, String password) throws SQLException {
        String sql = "SELECT username FROM users WHERE username = ?;";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
            String sql2 = "INSERT INTO users (username, password) VALUES (?, ?);";
            ps = conn.prepareStatement(sql2);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            return true;
        }
        else return false;
    }


    public void signup(ActionEvent actionEvent) {
        String username = usernameTextField.getText();
        String password = passwordField.getText();

        if (username.isEmpty()) {
            statusLabel.setText("Username cannot be empty!");
            passwordField.setText("");
        } else if (password.isEmpty()) {
            statusLabel.setText("Password cannot be empty!");
            usernameTextField.setText("");
        }
        else {
            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/apartment", "USERNAME", "PASSWORD");
                if (signup(conn, username, password)) {
                    statusLabel.setText("Signup successfully!");
                    usernameTextField.setText("");
                    passwordField.setText("");
                }
                else {
                    statusLabel.setText("Username has already been taken. Please choose another username!");
                    usernameTextField.setText("");
                    passwordField.setText("");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void switchToLoginScene(ActionEvent actionEvent) {
        try {
            LoginController loginController = new LoginController();
            loginController.switchScene(actionEvent, "/Login.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
