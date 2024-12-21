package io.github.btmxh.apartmentapp;

import java.io.*;
import java.sql.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.commons.compiler.CompileException;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private static final Logger logger = LogManager.getLogger(DatabaseConnection.class);

    public enum Role {
        ADMIN("Quản trị viên"),
        MANAGER( "Quản lý"),
        OFFICER( "Cảnh sát");

        private final String displayName;

        Role(String displayName) {
            this.displayName = displayName;
        }

        public String getSQLName() {
            return name();
        }

        public String getDisplayName() {
            return displayName;
        }

        public static String getRoleEnum() {
            String roleEnum = Arrays.stream(Role.values())
                    .map(Role::getSQLName)
                    .map(role -> "'" + role + "'")
                    .collect(Collectors.joining(", "));
            return "Enum(" + roleEnum + ")";
        }

        public static Role getRole(String sqlName) {
            for (Role role : Role.values()) {
                if (role.name().equals(sqlName)) {
                    return role;
                }
            }
            throw new IllegalArgumentException("Giá trị không xác định: " + sqlName);
        }

        public static List<Role> getNonAdminRoles() {
            return Arrays.stream(Role.values()).filter(r -> r != Role.ADMIN).toList();
        }
    }

    public enum FeeType {
        MANAGEMENT("Phí quản lý"),
        SERVICE("Phí dịch vụ"),
        PARKING("Phí gửi xe"),
        DONATION("Phí đóng góp");

        private final String displayName;

        FeeType(String displayName) {
            this.displayName = displayName;
        }

        public String getSQLName() {
            return name();
        }

        public String getDisplayName() {
            return displayName;
        }

        public static String getFeeTypeEnum() {
            String feeTypeEnum = Arrays.stream(FeeType.values())
                    .map(FeeType::getSQLName)
                    .map(type -> "'" + type + "'")
                    .collect(Collectors.joining(", "));
            return "Enum(" + feeTypeEnum + ")";
        }

        public static FeeType getFeeType(String sqlName) {
            for (FeeType type : FeeType.values()) {
                if (type.name().equals(sqlName)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Giá trị không xác định: " + sqlName);
        }
    }

    public enum Gender {
        MALE("Nam"),
        FEMALE("Nữ"),
        OTHER("Khác");

        private final String displayName;

        Gender(String displayName) {
            this.displayName = displayName;
        }

        public String getSQLName() {
            return name();
        }

        public String getDisplayName() {
            return displayName;
        }

        public static String getGenderEnum() {
            String genderEnum = Arrays.stream(Gender.values())
                    .map(Gender::getSQLName)
                    .map(type -> "'" + type + "'")
                    .collect(Collectors.joining(", "));
            return "Enum(" + genderEnum + ")";
        }

        public static Gender getGender(String sqlName) {
            for (Gender gender : Gender.values()) {
                if (gender.name().equals(sqlName)) {
                    return gender;
                }
            }
            throw new IllegalArgumentException("Giá trị không xác định: " + sqlName);
        }
    }

    private DatabaseConnection() {
        try {
            Dotenv dotenv = Dotenv.load();
            String url = dotenv.get("DB_URL");
            String username = dotenv.get("DB_USERNAME");
            String password = dotenv.get("DB_PASSWORD");
            if (url == null) {
                url = "jdbc:mysql://localhost:3306/apartment";
            }
            connection = DriverManager.getConnection(url, username, password);
            logger.info("Kết nối thành công tới Cơ sở dữ liệu");
        } catch (SQLException e) {
            throw new RuntimeException("Không thể kết nối với Cơ sở dữ liệu", e);
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException("Không thể ngắt kết nối khỏi Cơ sở dữ liệu", e);
        }
    }

    public void createUsersTable() {
        String roleEnum = Role.getRoleEnum();
        String sql_createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                user_id INT PRIMARY KEY AUTO_INCREMENT,
                user_name VARCHAR(50) NOT NULL,
                user_fullname VARCHAR(50) NOT NULL,
                user_password VARCHAR(50) NOT NULL,
                user_phone_number VARCHAR(15) NOT NULL,
                """ +
                "user_role " + roleEnum + " NOT NULL);";
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql_createUsersTable);
            logger.info("Đã tạo thành công bảng cho người dùng!");
        } catch (SQLException e) {
            throw new RuntimeException("Không thể tạo bảng người dùng trên cơ sở dữ liệu!", e);
        }
    }

    public void createServiceFeeTable() {
        String typeEnum = FeeType.getFeeTypeEnum();
        String sql = """
                CREATE TABLE IF NOT EXISTS service_fees (
                id INT PRIMARY KEY AUTO_INCREMENT,
                name VARCHAR(50) NOT NULL,
                value1 INT NOT NULL,
                value2 INT NOT NULL,
                start_date DATE NOT NULL,
                end_date DATE NOT NULL,
                """
                +
                "type " + typeEnum + " NOT NULL);";
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
            logger.info("Đã tạo thành công bảng phí dịch vụ!");
        } catch (SQLException e) {
            throw new RuntimeException("Không thể tạo bảng phí dịch vụ trên cơ sở dữ liệu", e);
        }
    }

    public void createPaymentsTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS payments (
                id INT PRIMARY KEY AUTO_INCREMENT,
                fee_id INT NOT NULL,
                room_id INT NOT NULL,
                value INT NOT NULL,
                commit_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                user_id INT NOT NULL);
                """;
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
            logger.info("Đã tạo thành công bảng thanh toán!");
        } catch (SQLException e) {
            throw new RuntimeException("Không thể tạo bảng thanh toán trên cơ sở dữ liệu", e);
        }
    }

    public void createCitizensTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS citizens (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    full_name VARCHAR(255) NOT NULL,
                    date_of_birth DATE NOT NULL,
                    gender ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL,
                    passport_id VARCHAR(12) NOT NULL UNIQUE,
                    nationality VARCHAR(100) NOT NULL,
                    room VARCHAR(10) NOT NULL,
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                );
                """;
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
            logger.info("Đã tạo thành công bảng nhân khẩu!");
        } catch (SQLException e) {
            throw new RuntimeException("Không thể tạo bảng nhân khẩu trên cơ sở dữ liệu", e);
        }
    }

    public void createRoomsTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS rooms (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(10) NOT NULL,
                    owner_name VARCHAR(255) NOT NULL,
                    area FLOAT(1) NOT NULL,
                    number_of_motors INT NOT NULL DEFAULT 0,
                    number_of_cars INT NOT NULL DEFAULT 0
                );
                """;
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
            logger.info("Đã tạo thành công bảng căn hộ!");
        } catch (SQLException e) {
            throw new RuntimeException("Không thể tạo bảng căn hộ trên cơ sở dữ liệu", e);
        }
    }

    public void addCitizenToDB(Citizen citizen) throws SQLException {
        String insertQuery = "INSERT INTO citizens (full_name, date_of_birth, gender, passport_id, nationality, room) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        String updateQuery = "UPDATE citizens SET full_name = ?, date_of_birth = ?, gender = ?, passport_id = ?, "
                + "nationality = ?, room = ? WHERE id = ?";

        // Nếu ID là -1 thì thực hiện thêm mới
        if (citizen.getId() == -1) {
            try (PreparedStatement st = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                st.setString(1, citizen.getFullName());
                st.setDate(2, Date.valueOf(citizen.getDateOfBirth()));
                st.setString(3, citizen.getGender().toString());
                st.setString(4, citizen.getPassportId());
                st.setString(5, citizen.getNationality());
                st.setString(6, citizen.getRoom());

                if (st.executeUpdate() == 0) {
                    throw new RuntimeException("Error while inserting citizen into DB");
                }

                // Lấy ID tự động tạo và gán lại cho đối tượng Citizen
                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (rs.next()) {
                        citizen.setId(rs.getInt(1));
                    }
                }
            }
        }
        // Nếu ID khác -1 thì thực hiện cập nhật
        else {
            try (PreparedStatement st = connection.prepareStatement(updateQuery)) {
                st.setString(1, citizen.getFullName());
                st.setDate(2, Date.valueOf(citizen.getDateOfBirth()));
                st.setString(3, citizen.getGender().toString());
                st.setString(4, citizen.getPassportId());
                st.setString(5, citizen.getNationality());
                st.setString(6, citizen.getRoom());
                st.setInt(7, citizen.getId());

                if (st.executeUpdate() == 0) {
                    throw new RuntimeException("Error while updating citizen in DB");
                }
            }
        }
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    private static JsonNode getJson(ResultSet rs, String key) throws IOException, SQLException {
        return mapper.readTree(rs.getBinaryStream(key));
    }

    public List<ServiceFee> getServiceFees(String query, int limit, int offset) throws IOException, SQLException {
        final var fees = new ArrayList<ServiceFee>();
        try(var st = connection.prepareStatement("SELECT id, type, name, value1, value2, start_date, end_date FROM service_fees WHERE INSTR(name, ?) != 0 LIMIT ? OFFSET ?")) {
            st.setInt(2, limit);
            st.setInt(3, offset);
            st.setString(1, query);
            final var rs = st.executeQuery();
            while(rs.next()) {
                final var id = rs.getInt("id");
                final var type = FeeType.valueOf(rs.getString("type"));
                final var name = rs.getString("name");
                final var value1 = rs.getLong("value1");
                final var value2 = rs.getLong("value2");
                final var startDate = rs.getDate("start_date").toLocalDate();
                final var deadline = rs.getDate("end_date").toLocalDate();
                final var fee = new ServiceFee(id, type, name, value1, value2, startDate, deadline);
                fees.add(fee);
            }
        }
        return fees;
    }

    public List<Room> getRooms(String query) throws SQLException {
        final var rooms = new ArrayList<Room>();
        try(var st = connection.prepareStatement("SELECT id, name, owner_name, area, number_of_motors, number_of_cars FROM rooms WHERE INSTR(name, ?) != 0")) {
            st.setString(1, query);
            final var rs = st.executeQuery();
            while(rs.next()) {
                final var id = rs.getInt("id");
                final var name = rs.getString("name");
                final var ownerName = rs.getString("owner_name");
                final var area = rs.getFloat("area");
                final var numMotors = rs.getInt("number_of_motors");
                final var numCars = rs.getInt("number_of_cars");
                final var room = new Room(id, name, ownerName, area, numMotors, numCars);
                rooms.add(room);
            }
        }
        return rooms;
    }

    public List<Room> getRooms(String query, int limit, int offset) throws SQLException {
        final var rooms = new ArrayList<Room>();
        try(var st = connection.prepareStatement("SELECT id, name, owner_name, area, number_of_motors, number_of_cars FROM rooms WHERE INSTR(name, ?) != 0 LIMIT ? OFFSET ?")) {
            st.setString(1, query);
            st.setInt(2, limit);
            st.setInt(3, offset);
            final var rs = st.executeQuery();
            while(rs.next()) {
                final var id = rs.getInt("id");
                final var name = rs.getString("name");
                final var ownerName = rs.getString("owner_name");
                final var area = rs.getFloat("area");
                final var numMotors = rs.getInt("number_of_motors");
                final var numCars = rs.getInt("number_of_cars");
                final var room = new Room(id, name, ownerName, area, numMotors, numCars);
                rooms.add(room);
            }
        }
        return rooms;
    }

    public List<ServiceFee> getUnchargedFees(String query, Room room) throws IOException, SQLException {
        final var fees = new ArrayList<ServiceFee>();
        try(var st = connection.prepareStatement("SELECT id, name, value1, type, value2 FROM service_fees WHERE INSTR(name, ?) != 0 AND id NOT IN (SELECT fee_id FROM payments WHERE room_id = ?)")) {
            st.setString(1, query);
            st.setInt(2, room.getId());
            final var rs = st.executeQuery();
            while(rs.next()) {
                final var id = rs.getInt("id");
                final var type = FeeType.valueOf(rs.getString("type"));
                final var name = rs.getString("name");
                final var value1 = rs.getLong("value1");
                final var value2 = rs.getLong("value2");
                final var fee = new ServiceFee(id, type, name, value1, value2);
                fees.add(fee);
            }
        }
        return fees;
    }

    public void updatePayment(Payment payment) throws SQLException, IOException {
        if (payment.getId() == Payment.NULL_ID)
            try(var st = connection.prepareStatement("INSERT INTO payments (fee_id, user_id, room_id, value) VALUES (?, ?, ?, ?)")) {
                st.setInt(1, payment.getFee().getId());
                st.setInt(2, payment.getUser().getId());
                st.setInt(3, payment.getRoom().getId());
                st.setLong(4, payment.getValue());
                st.executeUpdate();
        }
        else {
            try (var st = connection.prepareStatement("UPDATE payments SET fee_id = ?, user_id = ?, room_id = ?, value = ?, commit_timestamp = ? WHERE id = ?")) {
                st.setInt(1, payment.getFee().getId());
                st.setInt(2, payment.getUser().getId());
                st.setInt(3, payment.getRoom().getId());
                st.setLong(4, payment.getValue());
                st.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                st.setInt(6, payment.getId());
                st.executeUpdate();
            }
        }
    }

    public void updateServiceFee(ServiceFee fee) throws SQLException, IOException {
        if(fee.getId() == ServiceFee.NULL_ID) {
            try (var st = connection.prepareStatement("INSERT INTO service_fees (type, name, value1, value2, start_date, end_date) VALUES (?, ?, ?, ?, ?, ?)")) {
                st.setString(1, fee.getType().getSQLName());
                st.setString(2, fee.getName());
                st.setLong(3, fee.getValue1());
                st.setLong(4, fee.getValue2());
                st.setDate(5, Date.valueOf(fee.getStartDate()));
                st.setDate(6, Date.valueOf(fee.getDeadline()));
                st.executeUpdate();
            }
        }
        else {
            try (var st = connection.prepareStatement("UPDATE service_fees SET type = ?, name = ?, value1 = ?, value2 = ?, start_date = ?, end_date = ? WHERE id = ?")) {
                st.setString(1, fee.getType().getSQLName());
                st.setString(2, fee.getName());
                st.setLong(3, fee.getValue1());
                st.setLong(4, fee.getValue2());
                st.setDate(5, Date.valueOf(fee.getStartDate()));
                st.setDate(6, Date.valueOf(fee.getDeadline()));
                st.setInt(7, fee.getId());
                st.executeUpdate();
            }
        }
    }

    public void updateRoom(Room room) throws SQLException, IOException {
        if(room.getId() == Room.NULL_ID) {
            try (var st = connection.prepareStatement("INSERT INTO rooms (name, owner_name, area, number_of_motors, number_of_cars) VALUES (?, ?, ?, ?, ?)")) {
                st.setString(1, room.getName());
                st.setString(2, room.getOwnerName());
                st.setFloat(3, room.getArea());
                st.setInt(4, room.getNumMotors());
                st.setInt(5, room.getNumCars());
                st.executeUpdate();
            }
        }
        else {
            try (var st = connection.prepareStatement("UPDATE rooms SET name = ?, owner_name = ?, area = ?, number_of_motors = ?, number_of_cars = ? WHERE id = ?")) {
                st.setString(1, room.getName());
                st.setString(2, room.getOwnerName());
                st.setFloat(3, room.getArea());
                st.setInt(4, room.getNumMotors());
                st.setInt(5, room.getNumCars());
                st.setInt(6, room.getId());
                st.executeUpdate();
            }
        }
    }

    public User login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_name = ? AND user_password = ?;";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if(!rs.next()) {
                    return null;
                }
                return new User(rs.getInt("user_id"), rs.getString("user_name"), rs.getString("user_fullname"), rs.getString("user_phone_number"), Role.valueOf(rs.getString("user_role")));
            }
        }
    }

    public boolean signup(String username, String name, String phoneNumber, String password) throws SQLException {
        String sql = "SELECT user_name FROM users WHERE user_name = ?;";
        try (PreparedStatement ps1 = connection.prepareStatement(sql)) {
            ps1.setString(1, username);
            try (ResultSet rs = ps1.executeQuery()) {
                if (rs.next()) {
                    return false;
                } else {
                    String role = isFirstAccount() ? Role.ADMIN.getSQLName() : Role.MANAGER.getSQLName();
                    String sql2 = "INSERT INTO users (user_name, user_fullname, user_phone_number, user_password, user_role) VALUES (?, ?, ?, ?, ?);";
                    try (PreparedStatement ps2 = connection.prepareStatement(sql2)) {
                        ps2.setString(1, username);
                        ps2.setString(2, name);
                        ps2.setString(3, phoneNumber);
                        ps2.setString(4, password);
                        ps2.setString(5, role);
                        ps2.executeUpdate();
                    }
                    return true;
                }
            }
        }
    }

    public long calc(String sql) throws SQLException {
        try(Statement st = connection.createStatement()) {
            final var rs = st.executeQuery(sql);
            rs.next();
            return rs.getLong(1);
        }
    }

    public long[] getDashboardValues() throws SQLException {
        return new long[]{
            calc("SELECT SUM(IF(value1 > value, value1, value)) FROM payments INNER JOIN service_fees on payments.fee_id = service_fees.id"),
            calc("SELECT SUM(IF(f.value1 > 0, f.value1, 0) * (SELECT COUNT(*) FROM citizens c WHERE f.start_date >= c.created_at)) FROM service_fees f"),
            calc("SELECT COUNT(1) FROM citizens"),
            calc("SELECT COUNT(DISTINCT room) FROM citizens"),
        };
    }

    public ObservableList<XYChart.Series<String, Integer>> getChartData() throws SQLException {
        String sql = """
                SELECT
                    YEAR(f.end_date) AS year,
                    MONTH(f.end_date) AS month,
                    SUM(f.value1 * (SELECT COUNT(*) FROM citizens c WHERE f.start_date >= c.created_at )) AS pending
                FROM
                    service_fees f
                WHERE
                    f.value1 > 0
                GROUP BY
                    YEAR(f.end_date),
                    MONTH(f.end_date)
                ORDER BY
                    year, month
                LIMIT 10;
                """;


        var list = FXCollections.<XYChart.Series<String, Integer>>observableArrayList();
        try(final var st = connection.createStatement()) {
            final var rs = st.executeQuery(sql);
            final ObservableList<XYChart.Data<String, Integer>> data = FXCollections.observableArrayList();
            while(rs.next()) {
                var ym = YearMonth.of(rs.getInt("year"), rs.getInt("month")).toString();
                int pending = (int) rs.getLong("pending");
                data.add(new XYChart.Data<>(ym, pending));
            }
            final var series = new XYChart.Series<String, Integer>();
            series.setData(data);
            series.setName("Tổng số tiền cần thu");
            list.add(series);
        }

        sql = """
                SELECT
                    YEAR(f.end_date) AS year,
                    MONTH(f.end_date) AS month,
                    SUM(IF(f.value1 > p.value, f.value1, p.value)) AS received
                FROM
                    payments p
                INNER JOIN
                    service_fees f ON p.fee_id = f.id
                GROUP BY
                    YEAR(f.end_date),
                    MONTH(f.end_date)
                ORDER BY
                    year, month
                LIMIT 10;
                """;
        try(final var st = connection.createStatement()) {
            final var rs = st.executeQuery(sql);
            final ObservableList<XYChart.Data<String, Integer>> data = FXCollections.observableArrayList();
            while(rs.next()) {
                var ym = YearMonth.of(rs.getInt("year"), rs.getInt("month")).toString();
                int received = (int) rs.getLong("received");
                data.add(new XYChart.Data<>(ym, received));
            }
            final var series = new XYChart.Series<String, Integer>();
            series.setData(data);
            series.setName("Tổng số tiền đã thu");
            list.add(series);
        }

        return list;
    }

    public String getRole(String username) throws SQLException {
        String sql = "SELECT user_role FROM users WHERE user_name = ?;";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("user_role");
                } else {
                    return "";
                }
            }
        }
    }

    public boolean isFirstAccount() throws SQLException {
        String sql = "SELECT * FROM users;";
        try (Statement s = connection.createStatement();
             ResultSet rs = s.executeQuery(sql)) {

            return !rs.next();
        }
    }

    public void resetDatabase() throws SQLException {
        try (final var st = connection.createStatement()) {
            st.execute("DROP TABLE IF EXISTS users");
            st.execute("DROP TABLE IF EXISTS service_fees");
            st.execute("DROP TABLE IF EXISTS payments");
            st.execute("DROP TABLE IF EXISTS citizens");
            logger.info("Successfully reset database");
        }
    }

    public ObservableList<User> getNonAdminUserList(int limit, int offset) throws SQLException {
        ObservableList<User> userList = FXCollections.observableArrayList();
        String query = "SELECT user_id, user_name, user_fullname, user_phone_number, user_role FROM users WHERE user_role != '" + Role.ADMIN.getSQLName() + "' LIMIT " + limit + " OFFSET " + offset;
        try (Statement s = connection.createStatement();
             ResultSet rs = s.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("user_id");
                String name = rs.getString("user_name");
                String fullname = rs.getString("user_fullname");
                String phoneNum = rs.getString("user_phone_number");
                String role = rs.getString("user_role");
                userList.add(new User(id, name, fullname, phoneNum, Role.getRole(role)));
            }
        }
        return userList;
    }

    public String getUserPassword(String name) throws SQLException {
        String query = "SELECT user_password FROM users WHERE user_name = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("user_password");
                }
            }
        }
        return null;
    }

    public void setPassword (String pass, String name) throws SQLException {
        String query = "UPDATE users SET user_password = ? WHERE user_name = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, pass);
            ps.setString(2, name);
            ps.executeUpdate();
        }
    }

    public void setPhoneNum (String phone, String name) throws SQLException {
        String query = "UPDATE users SET user_phone_number = ? WHERE user_name = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, phone);
            ps.setString(2, name);
            ps.executeUpdate();
        }
    }


    public void setRole(String username, Role role) throws SQLException {
        String query = "UPDATE users SET user_role = ? WHERE user_name = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, role.getSQLName());
            ps.setString(2, username);
            ps.executeUpdate();
        }
    }

    public int getNumNonAdminUsers() throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE user_role != '" + Role.ADMIN.getSQLName() + "'";
        try (Statement s = connection.createStatement();
             ResultSet rs = s.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }
        }
    }

    public int getNumServiceFees(String search) throws SQLException {
        String query = "SELECT COUNT(*) FROM service_fees WHERE INSTR(name, ?) != 0";
        try (PreparedStatement s = connection.prepareStatement(query)) {
            s.setString(1, search);
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }
        }
    }

    public int getNumPayments(String feeS, String roomS) throws SQLException {
        String query = "SELECT COUNT(*) FROM payments, service_fees, rooms WHERE payments.fee_id = service_fees.id AND payments.room_id = rooms.id AND INSTR(service_fees.name, ?) != 0 AND INSTR(rooms.name, ?) != 0";
        try (PreparedStatement s = connection.prepareStatement(query)) {
            s.setString(1, feeS);
            s.setString(2, roomS);
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }
        }
    }

    public int getNumResident() throws SQLException {
        String query = "SELECT COUNT(*) FROM citizens";
        try (PreparedStatement s = connection.prepareStatement(query)) {
//            s.setString(1, search);
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }
        }
    }

    public int getNumRooms(String search) throws SQLException {
        String query = "SELECT COUNT(*) FROM rooms WHERE INSTR(name, ?) != 0";
        try (PreparedStatement s = connection.prepareStatement(query)) {
            s.setString(1, search);
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }
        }
    }

    public void removeUser(int id) throws SQLException {
        try(var st = connection.prepareStatement("DELETE FROM users WHERE user_id = ?")) {
            st.setInt(1, id);
            st.executeUpdate();
        }
    }

    public void removeServiceFee(ServiceFee fee) throws SQLException {
        try(var st = connection.prepareStatement("DELETE FROM service_fees WHERE id = ?")) {
            st.setInt(1, fee.getId());
            st.executeUpdate();
        }
    }

    public void removePayment(Payment payment) throws SQLException {
        try(var st = connection.prepareStatement("DELETE FROM payments WHERE id = ?")) {
            st.setInt(1, payment.getId());
            st.executeUpdate();
        }
    }

    public  void removeResident(int id) throws SQLException {
        try(var st = connection.prepareStatement("DELETE FROM citizens WHERE id = ?")){
            st.setInt(1,id);
            st.executeUpdate();
        }
    }

    public void removeRoom(Room room) throws SQLException {
        try(var st = connection.prepareStatement("DELETE FROM rooms WHERE id = ?")) {
            st.setInt(1, room.getId());
            st.executeUpdate();
        }
    }

    public List<Payment> getPayments(String feeS, String roomS, int limit, int offset) throws SQLException {
        var list = new ArrayList<Payment>();
        try(var st = connection.prepareStatement("""
                SELECT
                    payments.*, service_fees.*, users.*, rooms.*
                FROM
                    payments, service_fees, users, rooms
                WHERE
                    payments.fee_id = service_fees.id AND payments.user_id = users.user_id AND payments.room_id = rooms.id
                AND
                    INSTR(rooms.name, ?) != 0 AND INSTR(service_fees.name, ?) != 0
                LIMIT ? OFFSET ?;
                """)) {
            st.setString(1, roomS);
            st.setString(2, feeS);
            st.setInt(3, limit);
            st.setInt(4, offset);
            final var rs = st.executeQuery();
            while(rs.next()) {
                list.add(new Payment(
                        rs.getInt("payments.id"),
                        new ServiceFee(
                                rs.getInt("service_fees.id"),
                                FeeType.valueOf(rs.getString("service_fees.type")),
                                rs.getString("service_fees.name"),
                                rs.getLong("service_fees.value1"),
                                rs.getLong("service_fees.value2"),
                                rs.getDate("service_fees.start_date").toLocalDate(),
                                rs.getDate("service_fees.end_date").toLocalDate()
                        ),
                        new User(
                                rs.getInt("users.user_id"),
                                rs.getString("users.user_name"),
                                rs.getString("users.user_fullname"),
                                rs.getString("users.user_phone_number"),
                                Role.valueOf(rs.getString("users.user_role"))
                        ),
                        new Room(
                                rs.getInt("rooms.id"),
                                rs.getString("rooms.name"),
                                rs.getString("rooms.owner_name"),
                                rs.getFloat("rooms.area"),
                                rs.getInt("rooms.number_of_motors"),
                                rs.getInt("rooms.number_of_cars")
                        ),
                        rs.getLong("payments.value"),
                        rs.getTimestamp("payments.commit_timestamp").toLocalDateTime()
                ));
            }
        }

        return list;
    }

    public ObservableList<Citizen> getResidents(String search, int limit, int offset) throws SQLException {
        ObservableList<Citizen> residentList = FXCollections.observableArrayList();
        String query = """
                SELECT * FROM Citizens WHERE INSTR(full_name, ?) > 0 LIMIT ? OFFSET ?""";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, search);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String fullname = rs.getString("full_name");
                    Date date = rs.getDate("date_of_birth");
                    String gender = rs.getString("gender");
                    String passportId = rs.getString("passport_id");
                    String nationality = rs.getString("nationality");
                    String room = rs.getString("room");
                    var createdAt = rs.getTimestamp("created_at");
                    var updatedAt = rs.getTimestamp("updated_at");

                    residentList.add(new Citizen(id, fullname, date.toLocalDate(), Gender.valueOf(gender), passportId, nationality, room, createdAt.toLocalDateTime(), updatedAt.toLocalDateTime()));
                }
            }
        }
        return residentList;
    }
}
