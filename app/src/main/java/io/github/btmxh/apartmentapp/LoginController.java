package io.github.btmxh.apartmentapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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

    @FXML
    private Button loginButton;

    public void loginButtonOnActive(ActionEvent event) {
        String username = usernameTextField.getText();
        String password = passwordPasswordField.getText();

        if (username.isBlank() || password.isBlank()) {
            loginMessageLabel.setText("Tên người dùng và mật khẩu không được để trống!");
            usernameTextField.setText("");
            passwordPasswordField.setText("");
        }
        else {
            DatabaseConnection dbc = DatabaseConnection.getInstance();
            try {
                User user = dbc.login(username, password);
                if (user != null) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/page-view.fxml"));
                        Region homepage = loader.load();
                        PageController pageController = loader.getController();
                        pageController.setUser(user);
                        Stage stage = (Stage) loginButton.getScene().getWindow();
                        stage.close();

                        stage = new Stage();
                        stage.setScene(new Scene(homepage));
                        stage.show();

                    } catch (Exception e) {
                        logger.fatal("Lỗi khi tải tệp FXML", e);
                        Announcement.show("Lỗi","Không thể truy cập trang chủ!", "Lỗi tải FXML: " + e.getMessage());
                    }
                } else {
                    loginMessageLabel.setText("Tên người dùng hoặc mật khẩu không đúng. Hãy thử lại!");
                }
            }
            catch (SQLException e) {
                logger.warn("Lỗi khi thực hiện câu lệnh SQL", e);
                Announcement.show("Lỗi", "Không thể đăng nhập!","Lỗi kết nối cơ sở dữ liệu:" + e.getMessage());
            }
        }
    }
    @FXML
    public void initialize() {
        // Add a button click handler to navigate to the registration page
        clicktoRegister.setOnAction(_e -> {
            try {
                Region registerRoot = Utils.fxmlLoader("/register-view.fxml").load();
                Stage stage = (Stage) clicktoRegister.getScene().getWindow();
                stage.getScene().setRoot(registerRoot);
            } catch (Exception e) {
                logger.fatal("Error loading FXML file", e);
                Announcement.show("Lỗi","Không thể truy cập trang đăng ký!", "Lỗi tải FXML: " + e.getMessage());
            }
        });
    }
}
