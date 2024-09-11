package io.github.btmxh.apartmentapp;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    public static String queryDataFromDatabase() throws SQLException {
        final var conn = DriverManager.getConnection("jdbc:postgresql:apartment");
        final var stmt = conn.createStatement();
        final var query = stmt.executeQuery("SELECT 2 + 2");
        query.next();
        final var value = query.getInt(1);
        return String.valueOf(value);
    }
}
