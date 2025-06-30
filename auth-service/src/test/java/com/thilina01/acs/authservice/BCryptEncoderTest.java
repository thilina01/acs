package com.thilina01.acs.authservice;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptEncoderTest {

    @Test
    public void shouldEncodePassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        System.out.println("Encoded password for 'admin123': " + encoder.encode("admin123"));
        System.out.println("Encoded password for 'password123': " + encoder.encode("password123"));
    }
}
