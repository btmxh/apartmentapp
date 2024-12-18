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

    // Tài khoản admin cố định
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";

    public void loginButtonOnActive(ActionEvent event) {
        String username = usernameTextField.getText();
        String password = passwordPasswordField.getText();

        // Kiểm tra tài khoản và mật khẩu không trống
        if (username.isBlank() || password.isBlank()) {
            loginMessageLabel.setText("Tên người dùng và mật khẩu không được để trống!");
            clearInputFields();
        }
        // Đăng nhập bằng tài khoản admin cố định
        else if (username.equals(ADMIN_USERNAME)) {
            if (!password.equals(ADMIN_PASSWORD)) {
                loginMessageLabel.setText("Tên người dùng hoặc mật khẩu không khớp!");
            } else {
                loadHomePage(true); // Chế độ admin
            }
        }
        // Đăng nhập tài khoản từ cơ sở dữ liệu
        else {
            DatabaseConnection dbc = DatabaseConnection.getInstance();
            try {
                User user = dbc.login(username, password);
                if (user != null) {
                    loadHomePage(false); // Chế độ người dùng thông thường
                } else {
                    loginMessageLabel.setText("Tên người dùng hoặc mật khẩu không khớp!");
                }
            } catch (SQLException e) {
                logger.warn("Lỗi khi thực hiện câu lệnh SQL", e);
                Announcement.show("Lỗi", "Không thể đăng nhập!", "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
            }
        }
    }

    /**
     * Phương thức tải màn hình trang chính
     * @param isAdmin true nếu là tài khoản admin, false nếu là tài khoản thường
     */
    private void loadHomePage(boolean isAdmin) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/page-view.fxml"));
            Region homepage = loader.load();

            // Lấy controller của trang chính
            PageController pageController = loader.getController();
            pageController.setAdminMode(isAdmin); // Thiết lập chế độ admin nếu cần

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.close();

            stage = new Stage();
            stage.setScene(new Scene(homepage));
            stage.show();

        } catch (Exception e) {
            logger.fatal("Lỗi khi tải tệp FXML", e);
            Announcement.show("Lỗi", "Không thể truy cập trang chủ!", "Lỗi tải FXML: " + e.getMessage());
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
                Announcement.show("Lỗi", "Không thể truy cập trang đăng ký!", "Lỗi tải FXML: " + e.getMessage());
            }
        });
    }

    public boolean isValidPassword(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
        return password.matches(regex);
    }

    private void clearInputFields() {
        usernameTextField.clear();
        passwordPasswordField.clear();
    }
}
