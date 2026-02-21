package com.example.mybatis.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private static final String TEST_SECRET = "test-secret-key-for-hmac-sha256-must-be-at-least-32-chars";
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(TEST_SECRET, 3600_000L);
    }

    @Test
    void generateToken_and_getUsernameFromToken() {
        String token = jwtUtil.generateToken("testuser");
        assertThat(token).isNotBlank();
        assertThat(jwtUtil.getUsernameFromToken(token)).isEqualTo("testuser");
    }

    @Test
    void validateToken_returnsTrue_whenValidAndUsernameMatches() {
        String token = jwtUtil.generateToken("john");
        assertThat(jwtUtil.validateToken(token, "john")).isTrue();
    }

    @Test
    void validateToken_returnsFalse_whenUsernameMismatch() {
        String token = jwtUtil.generateToken("john");
        assertThat(jwtUtil.validateToken(token, "jane")).isFalse();
    }

    @Test
    void validateToken_returnsFalse_whenTokenInvalid() {
        assertThat(jwtUtil.validateToken("bad.token.here", "john")).isFalse();
    }
}
