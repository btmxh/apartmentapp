package io.github.btmxh.apartmentapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class TestValidate {

    @Test
    public void testValidateSignUpInfo() {
        // Test for empty username
        assertEquals("Username must not be empty", RegisterController.validateSignUpInfo("", "password123", "user@example.com", "1234567890", "password123"));

        // Test for empty password
        assertEquals("Password must not be empty", RegisterController.validateSignUpInfo("user", "", "user@example.com", "1234567890", "password123"));

        // Test for empty email
        assertEquals("Email must not be empty", RegisterController.validateSignUpInfo("user", "password123", "", "1234567890", "password123"));

        // Test for empty phone number
        assertEquals("Phone number must not be empty", RegisterController.validateSignUpInfo("user", "password123", "user@example.com", "", "password123"));

        // Test for invalid email
        assertEquals("Invalid email: user@", RegisterController.validateSignUpInfo("user", "password123", "user@", "1234567890", "password123"));

        // Test for invalid phone number
        assertEquals("Invalid phone number: 123", RegisterController.validateSignUpInfo("user", "password123", "user@example.com", "123", "password123"));

        // Test for empty reentered password
        assertEquals("Please reenter password", RegisterController.validateSignUpInfo("user", "password123", "user@example.com", "1234567890", ""));

        // Test for mismatched passwords
        assertEquals("Password does not match", RegisterController.validateSignUpInfo("user", "password123", "user@example.com", "1234567890", "password456"));

        // Test for valid input
        assertNull(RegisterController.validateSignUpInfo("user", "password123", "user@example.com", "1234567890", "password123"));
    }
}
