package io.github.btmxh.apartmentapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class TestValidate {

    @Test
    public void testValidateSignUpInfo() {
        // Test for empty username
        assertEquals("Tên người dùng không được để trống!", RegisterController.validateSignUpInfo("", "password123", "user@example.com", "1234567890", "password123"));

        // Test for empty password
        assertEquals("Mật khẩu không được để trống!", RegisterController.validateSignUpInfo("user", "", "user@example.com", "1234567890", "password123"));

        // Test for empty email
        assertEquals("Email không được để trống!", RegisterController.validateSignUpInfo("user", "password123", "", "1234567890", "password123"));

        // Test for empty phone number
        assertEquals("Số điện thoại không được để trống!", RegisterController.validateSignUpInfo("user", "password123", "user@example.com", "", "password123"));

        // Test for invalid email
        assertEquals("Email không hợp lệ: user@", RegisterController.validateSignUpInfo("user", "password123", "user@", "1234567890", "password123"));

        // Test for invalid phone number
        assertEquals("Số điện thoại không hợp lệ: 123", RegisterController.validateSignUpInfo("user", "password123", "user@example.com", "123", "password123"));

        // Test for empty reentered password
        assertEquals("Vui lòng nhập lại mật khẩu!", RegisterController.validateSignUpInfo("user", "password123", "user@example.com", "1234567890", ""));

        // Test for mismatched passwords
        assertEquals("Mật khẩu không khớp!", RegisterController.validateSignUpInfo("user", "password123", "user@example.com", "1234567890", "password456"));

        // Test for valid input
        assertNull(RegisterController.validateSignUpInfo("user", "password123", "user@example.com", "1234567890", "password123"));
    }
}
