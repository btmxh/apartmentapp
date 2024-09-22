package io.github.btmxh.apartmentapp;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;
public class DatabaseConnection
{
    private static DatabaseConnection instance;
    private Connection connection;
    private DatabaseConnection() {
        try {
            Dotenv dotenv = Dotenv.load();
            String url = "jdbc:mysql://localhost:3306/apartment";
            String username = dotenv.get("DB_USERNAME");
            String password = dotenv.get("DB_PASSWORD");
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Successfully connected to Database!");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    public void createUsersTable() {
        String sql_createUsersTable =   "CREATE TABLE IF NOT EXISTS users (" +
                                        "user_id INT PRIMARY KEY AUTO_INCREMENT, " +
                                        "user_name VARCHAR(50) NOT NULL, " +
                                        "user_password VARCHAR(50) NOT NULL, " +
                                        "user_phone_number VARCHAR(15) NOT NULL, " +
                                        "user_email VARCHAR(50) NOT NULL, " +
                                        ")";
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql_createUsersTable);
            System.out.println("Successfully created a table for users!");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}