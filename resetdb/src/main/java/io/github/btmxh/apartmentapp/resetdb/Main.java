package io.github.btmxh.apartmentapp.resetdb;

import io.github.btmxh.apartmentapp.DatabaseConnection;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        DatabaseConnection.getInstance().resetDatabase();
    }
}