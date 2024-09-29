package io.github.btmxh.apartmentapp;

import java.sql.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class DatabaseConnection
{
    private static DatabaseConnection instance;
    private Connection connection;
    private static final Logger logger = LogManager.getLogger(DatabaseConnection.class);
    private boolean admin = false;
    public enum Role {
        ADMIN("admin"),
        RESIDENT("resident");

        private String sqlName;
        Role(String sqlName) {
            this.sqlName = sqlName;
        }

        public String getsqlName() {
            return sqlName;
        }

        public static String getRoleEnum() {
            StringBuilder roleEnum = new StringBuilder("ENUM('");
            for (Role r: Role.values()) {
                roleEnum.append(r.getsqlName()).append("', '");
            }
            roleEnum.delete(roleEnum.length()-3, roleEnum.length());
            roleEnum.append(")");
            return roleEnum.toString();
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
                    String role;
                    if (admin) {
                        role = Role.RESIDENT.getsqlName();
                    }
                    else {
                        role = Role.ADMIN.getsqlName();
                        admin = true;
                    }
                    String sql2 = "INSERT INTO   users (user_name, user_email, user_phone_number, user_password, user_role) VALUES (?, ?, ?, ?, ?);";
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

    public String get_role(String username) throws SQLException {
        String sql = "SELECT user_role FROM users WHERE user_name = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
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

    public void resetDatabase() throws SQLException {
        try(final var st = connection.createStatement()) {
            st.execute("DROP TABLE IF EXISTS users");
            logger.info("Successfully reset database");
        }
    }
}