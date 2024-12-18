package io.github.btmxh.apartmentapp;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.commons.compiler.CompileException;

import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Locale;
import java.util.Objects;

public class PageController {

    private static final Logger logger = LogManager.getLogger(PageController.class);

    private static final int ROWS_PER_PAGE = 10;


    @FXML private TextField serviceFeeFilterField;
    @FXML private Button residentsButton, contributeButton;

    public void addPayment(ActionEvent actionEvent) {
        try {
            AddPaymentController.open(serviceFeeFilterField.getScene().getWindow(), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private enum Section {
        CREATECHARGE,
        CHARGE,
        RESIDENT,
        GRANTPERMISSION,
        UNSUPPORTED,
        HOME
    }

    @FXML
    private Button createChargeButton;

    @FXML
    private Button chargeButton;

    @FXML
    private Button manageButton;

    @FXML
    private Button staticButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button homeButton;

    @FXML
    private Label usernameLabel, greetingLabel, generalLabel;

    @FXML
    private Label numRooms, numCitizens, totalMoneyPending, totalMoneyRecv;

    @FXML
    private VBox createChargeVBox;

    @FXML
    private VBox chargeVBox;

    @FXML
    private VBox residentsVBox;

    @FXML
    private ComboBox<Month> monthComboBox;

    @FXML
    private VBox grantPermissionVBox;

    @FXML
    private VBox unsupportedVBox;

    @FXML
    private VBox homeVBox;

    @FXML
    private Pagination usersPagination;

    @FXML
    private Pagination serviceFeePagination;

    @FXML
    private LineChart<String, Integer> chart;

    @FXML
    private TextField feeSearch;

    @FXML
    private TextField roomSearch;

    @FXML
    private Pagination paymentTable;

    @FXML
    private Button addResident;

    @FXML
    private Pagination residentTable;

    @FXML
    private TextField nameResidentSearch;

    private final ObjectProperty<Section> section = new SimpleObjectProperty<>(Section.HOME);

    private void bindSection(Section sect, Node content, Button button) {
        button.setOnAction(_e -> section.set(sect));
        content.visibleProperty().bind(section.isEqualTo(sect));
    }

    private String formatMoney(long value) {
        return NumberFormat.getCurrencyInstance(Locale.of("vi", "VN")).format(value);
    }

    @FXML
    public void initialize() {
        setNumPages();
        usersPagination.setPageFactory(this::createUserTable);
        serviceFeePagination.setPageFactory(this::createServiceFeeTable);
        paymentTable.setPageFactory(this::createPaymentTable);
        residentTable.setPageFactory(this::createResidentTable);

        bindSection(Section.HOME, homeVBox, homeButton);
        bindSection(Section.CREATECHARGE, createChargeVBox, createChargeButton);
        bindSection(Section.CHARGE, chargeVBox, chargeButton);
        bindSection(Section.RESIDENT, residentsVBox, residentsButton);
        bindSection(Section.GRANTPERMISSION, grantPermissionVBox, manageButton);
        bindSection(Section.UNSUPPORTED, unsupportedVBox, staticButton);
        bindSection(Section.UNSUPPORTED, unsupportedVBox, contributeButton);
        try {
            final var values = DatabaseConnection.getInstance().getDashboardValues();

            totalMoneyRecv.setText(formatMoney(values[0]));
            totalMoneyPending.setText(formatMoney(Math.max(0, values[1] - values[0])));
            numCitizens.setText(values[2] + " người");
            numRooms.setText(values[3] + " hộ");
        } catch (SQLException e) {
            logger.error("Unable to retrieve dashboard stats", e);
            Announcement.show("Lỗi", "Không thể lấy giá trị bảng điều khiển", "Vui lòng kiểm tra kết nối của bạn.");
        }


        logoutButton.setOnAction(_e -> handleLogout());
        serviceFeeFilterField.textProperty().addListener((o, old, ne_) -> updateServiceFees());
        feeSearch.textProperty().addListener((o, old, ne_) -> updatePayments());
        roomSearch.textProperty().addListener((o, old, ne_) -> updatePayments());
        generalLabel.setText("Tổng quan tháng " + LocalDateTime.now().getMonthValue());
        nameResidentSearch.textProperty().addListener((o, old, ne_) -> updateResidents());

        try {
            chart.setData(DatabaseConnection.getInstance().getChartData());
        } catch (SQLException e) {
            logger.error("Unable to retrieve chart stats", e);
            Announcement.show("Lỗi", "Không thể lấy giá trị bảng", "Vui lòng kiểm tra kết nối của bạn.");
        }
    }

    private void handleLogout() {
        try {
            Region loginPage = Utils.fxmlLoader("/login-view.fxml").load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.close();
            stage = new Stage();
            stage.setScene(new Scene(loginPage));
            stage.setWidth(loginPage.getPrefWidth());
            stage.setHeight(loginPage.getPrefHeight());
            stage.show();
        } catch (Exception e) {
            logger.fatal("Lỗi khi tải tệp FXML", e);
            Announcement.show("Lỗi","Không thể truy cập trang đăng ký!", "Lỗi tải FXML:" + e.getMessage());
        }
    }

    private static String getGreeting() {
        LocalTime now = LocalTime.now();

        if (now.isBefore(LocalTime.of(12, 0))) {
            return "Chào buổi sáng";
        } else if (now.isBefore(LocalTime.of(18, 0))) {
            return "Chào buổi chiều";
        } else {
            return "Chào buổi tối";
        }
    }


    public void setUser(User user) {
        usernameLabel.setText(user.getFullname());
        greetingLabel.setText(getGreeting() + ", " + user.getFullname());
        manageButton.setVisible(user.getRole() == DatabaseConnection.Role.ADMIN);
        manageButton.setManaged(user.getRole() == DatabaseConnection.Role.ADMIN);
    }

    private TableView<User> createUserTable(int pageIndex) {
        DatabaseConnection dc = DatabaseConnection.getInstance();
        try {
            var loader = new FXMLLoader(Objects.requireNonNull(PageController.class.getResource("/role-table.fxml")));
            int start = pageIndex * ROWS_PER_PAGE;
            var  userList = dc.getNonAdminUserList(ROWS_PER_PAGE, pageIndex * ROWS_PER_PAGE);
            TableView<User> table = loader.load();
            RoleTableController controller = loader.getController();
            controller.setUserData(start, userList, this::updateUsers);
            return table;
        } catch (SQLException e) {
            logger.warn("Lỗi khi thực hiện câu lệnh SQL", e);
            Announcement.show("Lỗi", "Không thể lấy được danh sách người dùng!", "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
        } catch (IOException e) {
            logger.fatal("Lỗi khi tải tệp FXML", e);
            Announcement.show("Lỗi", "Không thể tải bảng vai trò FXML!", "Lỗi chi tiết: " + e.getMessage());
        }
        return null;
    }

    private TableView<ServiceFee> createServiceFeeTable(int pageIndex) {
        DatabaseConnection dc = DatabaseConnection.getInstance();
        try {
            var loader = new FXMLLoader(Objects.requireNonNull(PageController.class.getResource("/service-fee-table.fxml")));
            int start = pageIndex * ROWS_PER_PAGE;
            var fees = dc.getServiceFees(serviceFeeFilterField.getText(), ROWS_PER_PAGE, pageIndex * ROWS_PER_PAGE);
            TableView<ServiceFee> table = loader.load();
            ServiceFeeTableController controller = loader.getController();
            controller.setUserData(start, FXCollections.observableArrayList(fees), this::updateServiceFees);
            return table;
        } catch (SQLException e) {
            logger.warn("Lỗi khi thực thi lệnh SQL", e);
            Announcement.show("Lỗi", "Không thể lấy được danh sách phí dịch vụ!", "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
        } catch (IOException e) {
            logger.fatal("Lỗi khi tải tệp FXML", e);
            Announcement.show("Lỗi", "Không thể tải bảng phí dịch vụ FXML!", "Lỗi chi tiết: " + e.getMessage());
        }
        return null;
    }

    private TableView<Payment> createPaymentTable(int pageIndex) {
        DatabaseConnection dc = DatabaseConnection.getInstance();
        try {
            var loader = new FXMLLoader(Objects.requireNonNull(PageController.class.getResource("/payment-table.fxml")));
            int start = pageIndex * ROWS_PER_PAGE;
            var fees = dc.getPayments(feeSearch.getText(), roomSearch.getText(), ROWS_PER_PAGE, pageIndex * ROWS_PER_PAGE);
            TableView<Payment> table = loader.load();
            PaymentTableController controller = loader.getController();
            controller.setPage(start, FXCollections.observableArrayList(fees));
            return table;
        } catch (SQLException e) {
            logger.warn("Lỗi khi thực thi lệnh SQL", e);
            Announcement.show("Lỗi", "Không thể lấy được danh sách phí dịch vụ!", "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
        } catch (IOException e) {
            logger.fatal("Lỗi khi tải tệp FXML", e);
            Announcement.show("Lỗi", "Không thể tải bảng phí dịch vụ FXML!", "Lỗi chi tiết: " + e.getMessage());
        }
        return null;
    }

    private TableView<Citizen> createResidentTable(int pageIndex) {
        DatabaseConnection dc = DatabaseConnection.getInstance();
        try {
            var loader = new FXMLLoader(Objects.requireNonNull(PageController.class.getResource("/resident-table.fxml")));
            int start = pageIndex * ROWS_PER_PAGE;
            var ResidentList = DatabaseConnection.getInstance().getResidents(nameResidentSearch.getText(), ROWS_PER_PAGE, pageIndex * ROWS_PER_PAGE) ;
            TableView<Citizen> table = loader.load();
            ResidentTableController controller = loader.getController();
            controller.setResidentPage(start, ResidentList, this::updateResidents);
            return table;
        } catch (SQLException e) {
            logger.warn("Lỗi khi thực thi lệnh SQL", e);
            Announcement.show("Lỗi", "Không thể lấy được danh sách phí dịch vụ!", "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
        } catch (IOException e) {
            logger.fatal("Lỗi khi tải tệp FXML", e);
            Announcement.show("Lỗi", "Không thể tải bảng phí dịch vụ FXML!", "Lỗi chi tiết: " + e.getMessage());
        }
        return null;
    }

    private void setNumPages() {
        try {
            usersPagination.setPageCount(Math.max(1, (DatabaseConnection.getInstance().getNumNonAdminUsers() + ROWS_PER_PAGE - 1) / ROWS_PER_PAGE));
            serviceFeePagination.setPageCount(Math.max((DatabaseConnection.getInstance().getNumServiceFees(serviceFeeFilterField.getText()) + ROWS_PER_PAGE - 1) / ROWS_PER_PAGE, 1));
            paymentTable.setPageCount(Math.max(1, (DatabaseConnection.getInstance().getNumPayment() + ROWS_PER_PAGE - 1) / ROWS_PER_PAGE));
            residentTable.setPageCount(Math.max(1, (DatabaseConnection.getInstance().getNumResident() + ROWS_PER_PAGE - 1) / ROWS_PER_PAGE));
        } catch (SQLException e) {
            logger.warn("Lỗi khi thực hiện câu lệnh SQL", e);
            Announcement.show("Lỗi", "Không thể truy cập cơ sở dữ liệu!", e.getMessage());
        }
    }

    private void updateServiceFees() {
        int page = serviceFeePagination.getCurrentPageIndex();
        setNumPages();
        serviceFeePagination.setPageFactory(this::createServiceFeeTable);
        serviceFeePagination.setCurrentPageIndex(Math.min(page, serviceFeePagination.getPageCount() - 1));
    }

    private void updateUsers() {
        int page = usersPagination.getCurrentPageIndex();
        setNumPages();
        usersPagination.setPageFactory(this::createUserTable);
        usersPagination.setCurrentPageIndex(Math.min(page, usersPagination.getPageCount() - 1));
    }

    private void updatePayments() {
        int page = paymentTable.getCurrentPageIndex();
        setNumPages();
        paymentTable.setPageFactory(this::createPaymentTable);
        paymentTable.setCurrentPageIndex(Math.min(page, usersPagination.getPageCount() - 1));
    }

    private void updateResidents() {
        int page = residentTable.getCurrentPageIndex();
        setNumPages();
        residentTable.setPageFactory(this::createResidentTable);
        residentTable.setCurrentPageIndex(Math.min(page, residentTable.getPageCount() - 1));
    }

    public void addServiceFee(ActionEvent event) {
        try {
            AddServiceFeeController.open(((Node) event.getSource()).getScene().getWindow(), null);
            updateServiceFees();
        } catch (IOException e) {
            logger.fatal("Lỗi khi tải tệp FXML", e);
            Announcement.show("Lỗi", "Không thể tải FXML của hộp thoại phí dịch vụ!", "Lỗi chi tiết: " + e.getMessage());
        }
    }

    public void addResident(ActionEvent event) {
        try {
            AddResidentController.open(((Node) event.getSource()).getScene().getWindow(), null);
            updateResidents();
        } catch (IOException e) {
            logger.fatal("Lỗi khi tải tệp FXML", e);
            Announcement.show("Lỗi", "Không thể tải FXML của nhân khẩu!", "Lỗi chi tiết: " + e.getMessage());
        }
    }
}


