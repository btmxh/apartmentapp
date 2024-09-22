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

    private boolean login(Connection conn, String username, String password) throws SQLException {

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?;";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();
        return rs.next();

    }

    public void switchScene(ActionEvent event, String URL) throws IOException {

        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource(URL)));
        stage.setScene(scene);
        stage.show();
    }

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
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/apartment", "USERNAME", "PASSWORD");
                if (login(conn, username, password)) {
                    try {
                        switchScene(actionEvent, "/Application.fxml");
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
            switchScene(actionEvent, "/Signup.fxml");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}