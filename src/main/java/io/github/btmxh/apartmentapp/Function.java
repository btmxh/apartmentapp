package io.github.btmxh.apartmentapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Function {

    private boolean login(Connection conn, String username, String password) throws SQLException {

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?;";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();
        return rs.next();

    }

    private boolean signup(Connection conn, String username, String password) throws SQLException {
        String sql = "SELECT username FROM users WHERE username = ?;";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
            String sql2 = "INSERT INTO users (username, password) VALUES (?, ?);";
            ps = conn.prepareStatement(sql2);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            return true;
        }
        else return false;
    }
}
