package io.github.btmxh.apartmentapp;

import java.io.*;
import java.sql.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.commons.compiler.CompileException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private static final Logger logger = LogManager.getLogger(DatabaseConnection.class);

    public void removeUser(int id) throws SQLException {
        try(var st = connection.prepareStatement("DELETE FROM users WHERE user_id = ?")) {
            st.setInt(1, id);
            st.executeUpdate();
        }
    }

    public void removeServiceFee(int id) throws SQLException {
        try(var st = connection.prepareStatement("DELETE FROM service_fees WHERE fee_id = ?")) {
            st.setInt(1, id);
            st.executeUpdate();
        }
    }

    public enum Role {
        ADMIN("admin", "Quản trị viên"),
        RESIDENT("resident", "Người dân");

        private final String sqlName;
        private final String displayName;

        Role(String sqlName, String displayName) {
            this.sqlName = sqlName;
            this.displayName = displayName;
        }

        public static List<Role> nonAdminRoles() {
            return Arrays.stream(Role.values()).filter(r -> r != ADMIN).toList();
        }

        public String getSQLName() {
            return sqlName;
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
                if (role.sqlName.equals(sqlName)) {
                    return role;
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
                user_password VARCHAR(50) NOT NULL,
                user_phone_number VARCHAR(15) NOT NULL,
                user_email VARCHAR(50) NOT NULL,
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
        String sql = """
                CREATE TABLE IF NOT EXISTS service_fees (
                fee_id INT PRIMARY KEY AUTO_INCREMENT,
                fee_name VARCHAR(50) NOT NULL,
                fee_formula_json JSON NOT NULL);
                """;
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
                fee_id INT NOT NULL,
                user_id INT NOT NULL,
                amount INT NOT NULL,
                commit_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                PRIMARY KEY (fee_id, user_id));
                """;
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
            logger.info("Đã tạo thành công bảng thanh toán!");
        } catch (SQLException e) {
            throw new RuntimeException("Không thể tạo bảng thanh toán trên cơ sở dữ liệu", e);
        }
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    private static JsonNode getJson(ResultSet rs, String key) throws IOException, SQLException {
        return mapper.readTree(rs.getBinaryStream(key));
    }

    public List<ServiceFee> getServiceFees(int limit, int offset) throws IOException, CompileException, SQLException {
        final var fees = new ArrayList<ServiceFee>();
        try(var st = connection.prepareStatement("SELECT fee_id, fee_name, fee_formula_json FROM service_fees LIMIT ? OFFSET ?")) {
            st.setInt(1, limit);
            st.setInt(2, offset);
            final var rs = st.executeQuery();
            while(rs.next()) {
                final var id = rs.getInt("fee_id");
                final var name = rs.getString("fee_name");
                final var formula = getJson(rs, "fee_formula_json");
                final var fee = new ServiceFee(id, name, ServiceFee.Formula.fromJSON(formula));
                fees.add(fee);
            }
        }
        return fees;
    }

    public void updateServiceFee(ServiceFee fee, String name, ServiceFee.Formula formula) throws SQLException, IOException {
        name = name != null? name : fee.getName();
        formula = formula != null? formula : fee.getFormula();

        if(fee.getId() == ServiceFee.NULL_ID) {
            try(var st = connection.prepareStatement("INSERT INTO service_fees (fee_name, fee_formula_json) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                st.setString(1, name);
                try(var pis = new PipedInputStream(); var pos = new PipedOutputStream(pis)) {
                    mapper.writeValue(pos, formula.toJSON());
                    st.setBinaryStream(2, pis);
                    if(st.executeUpdate() == 0) {
                        throw new RuntimeException("Lỗi khi chèn phí dịch vụ vào DB");
                    }
                    try(var rs = st.getGeneratedKeys()) {
                        rs.next();
                        fee.setId(rs.getInt(1));
                    }
                }
            }
        } else {
            try(var st = connection.prepareStatement("UPDATE service_fees SET fee_name = ?, fee_formula_json = ? WHERE fee_id = ?")) {
                st.setString(1, fee.getName());
                st.setInt(3, fee.getId());
                try(var pis = new PipedInputStream(); var pos = new PipedOutputStream(pis)) {
                    mapper.writeValue(pos, fee.getFormula().toJSON());
                    st.setBinaryStream(2, pis);
                    st.executeUpdate();
                }
            }
        }
        fee.setName(name);
        fee.setFormula(formula);
    }

    public boolean login(String username, String password) throws SQLException {

        String sql = "SELECT * FROM users WHERE user_name = ? AND user_password = ?;";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean signup(String username, String email, String phoneNumber, String password) throws SQLException {

        String sql = "SELECT user_name FROM users WHERE user_name = ?;";
        try (PreparedStatement ps1 = connection.prepareStatement(sql)) {
            ps1.setString(1, username);
            try (ResultSet rs = ps1.executeQuery()) {
                if (rs.next()) {
                    return false;
                } else {
                    String role = isFirstAccount() ? Role.ADMIN.getSQLName() : Role.RESIDENT.getSQLName();
                    String sql2 = "INSERT INTO users (user_name, user_email, user_phone_number, user_password, user_role) VALUES (?, ?, ?, ?, ?);";
                    try (PreparedStatement ps2 = connection.prepareStatement(sql2)) {
                        ps2.setString(1, username);
                        ps2.setString(2, email);
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
            logger.info("Successfully reset database");
        }
    }

    public ObservableList<User> getNonAdminUserList(int limit, int offset) throws SQLException {
        ObservableList<User> userList = FXCollections.observableArrayList();
        String query = "SELECT user_id, user_name, user_role FROM users WHERE user_role != '" + Role.ADMIN.getSQLName() + "' LIMIT " + limit + " OFFSET " + offset;
        try (Statement s = connection.createStatement();
             ResultSet rs = s.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("user_id");
                String name = rs.getString("user_name");
                String role = rs.getString("user_role");
                userList.add(new User(id, name, Role.getRole(role)));
            }
        }
        return userList;
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

    public int getNumServiceFees() throws SQLException {
        String query = "SELECT COUNT(*) FROM service_fees";
        try (Statement s = connection.createStatement();
             ResultSet rs = s.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }
        }
    }
}