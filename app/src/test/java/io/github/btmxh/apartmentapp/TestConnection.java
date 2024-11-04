package io.github.btmxh.apartmentapp;

import org.junit.jupiter.api.Test;

public class TestConnection {
    @Test
    void testConnectionToDB() {
        DatabaseConnection.getInstance();
    }
}
