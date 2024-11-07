package io.github.btmxh.apartmentapp;

import java.sql.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

public class DatabaseConnection
{
    private static DatabaseConnection instance;
    private Connection connection;
    private static final Logger logger = LogManager.getLogger(DatabaseConnection.class);
    public enum Role {
        ADMIN("admin"),
        RESIDENT("resident");

        private String sqlName;
        Role(String sqlName) {
            this.sqlName = sqlName;
        }

        public static List<Role> nonAdminRoles() {
            return Arrays.stream(Role.values()).filter(r -> r != ADMIN).toList();
        }

        public String getSQLName() {
            return sqlName;
        }

        public static String getRoleEnum() {
            String roleEnum = Arrays.stream(Role.values())
                    .map(Role::getSQLName)
                    .map(role -> "'" + role + "'")
                    .collect (Collectors.joining(", "));
            return "Enum(" + roleEnum + ")";
        }

        public static Role getRole(String sqlName) {
            for (Role role : Role.values()) {
                if (role.sqlName.equals(sqlName)) {
                    return role;
                }
            }
            throw new IllegalArgumentException("Unknown value: " + sqlName);
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
            logger.info("Successfully connected to Database!");
        } catch(SQLException e) {
            throw new RuntimeException("Unable to connect to database", e);
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
        }
        catch (SQLException e) {
            throw new RuntimeException("Unable to disconnect from Database", e);
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
            logger.info("Successfully created a table for users!");
        } catch(SQLException e) {
            throw new RuntimeException("Unable to create users table on database", e);
        }
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
                }
                else {
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
                }
                else {
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
        try(final var st = connection.createStatement()) {
            st.execute("DROP TABLE IF EXISTS users");
            logger.info("Successfully reset database");
        }
    }

    public ObservableList<User> getUserList(int limit, int offset) throws SQLException {
        ObservableList<User> userList = FXCollections.observableArrayList();
        String query = "SELECT user_id, user_name, user_role FROM users LIMIT " + limit + " OFFSET " + offset;
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

    public int getNumUsers() throws SQLException {
        String query = "SELECT COUNT(*) FROM users";
        try (Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            else {
                return 0;
            }
        }
    }
}