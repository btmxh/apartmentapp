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

    public void createUsersTable() {
        String sql_createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                user_id INT PRIMARY KEY AUTO_INCREMENT,
                user_name VARCHAR(50) NOT NULL,
                user_password VARCHAR(50) NOT NULL,
                user_phone_number VARCHAR(15) NOT NULL,
                user_email VARCHAR(50) NOT NULL
                )""";
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql_createUsersTable);
            logger.info("Successfully created a table for users!");
        } catch(SQLException e) {
            throw new RuntimeException("Unable to create users table on database", e);
        }
    }

    public boolean login(String username, String password) throws SQLException {

        String sql = "SELECT * FROM users WHERE user_name = ? AND user_password = ?;";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, username);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();
        return rs.next();

    }

    public boolean signup(String username, String email, String phoneNumber, String password) throws SQLException {
        String sql = "SELECT user_name FROM users WHERE user_name = ?;";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
            String sql2 = "INSERT INTO users (user_name, user_email, user_phone_number, user_password) VALUES (?, ?, ?, ?);";
            ps = connection.prepareStatement(sql2);
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, phoneNumber);
            ps.setString(4, password);
            ps.executeUpdate();
            return true;
        }
        else return false;
    }

    public void resetDatabase() throws SQLException {
        try(final var st = connection.createStatement()) {
            st.execute("DROP TABLE IF EXISTS users");
            logger.info("Successfully reset database");
        }
    }
}