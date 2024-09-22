package io.github.btmxh.apartmentapp;

import java.sql.*;

public class DatabaseConnection
{
    private static DatabaseConnection instance;
    private Connection connection;
    private DatabaseConnection() {
        try {
            String url = "jdbc:mysql://localhost/apartment";
            String username = "root";
            String password = "PASSWORD";
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
                                        "id INT AUTO_INCREMENT, " +
                                        "username VARCHAR(30) NOT NULL UNIQUE, " +
                                        "password VARCHAR(30) NOT NULL, " +
                                        "PRIMARY KEY(id)" +
                                        ")";
        try {
            Statement statement = connection.createStatement();
            statement.execute(sql_createUsersTable);
            System.out.println("Successfully created a table for users!");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean login(String username, String password) throws SQLException {

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?;";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, username);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();
        return rs.next();

    }

    public boolean signup(String username, String password) throws SQLException {
        String sql = "SELECT username FROM users WHERE username = ?;";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
            String sql2 = "INSERT INTO users (username, password) VALUES (?, ?);";
            ps = connection.prepareStatement(sql2);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            return true;
        }
        else return false;
    }
}