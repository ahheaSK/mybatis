package com.example.mybatis.security;

import com.example.mybatis.dto.response.ApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for SecurityResponseWriter (package-private; test is in same package).
 */
class SecurityResponseWriterTest {

    @Test
    @DisplayName("toJson includes status, code, message, timestamp, trackingId")
    void toJson_includesExpectedFields() {
        String ts = "2025-01-15 12:00:00";
        UUID trackingId = UUID.fromString("11111111-2222-3333-4444-555555555555");
        ApiResponse<Void> r = ApiResponse.<Void>builder()
                .status(false)
                .code(401)
                .message("Unauthorized")
                .timestamp(ts)
                .trackingId(trackingId)
                .build();

        String json = SecurityResponseWriter.toJson(r);

        assertThat(json).contains("\"status\":false");
        assertThat(json).contains("\"code\":401");
        assertThat(json).contains("\"message\":\"Unauthorized\"");
        assertThat(json).contains("\"timestamp\":\"2025-01-15 12:00:00\"");
        assertThat(json).contains("\"trackingId\":\"11111111-2222-3333-4444-555555555555\"");
    }

    @Test
    @DisplayName("toJson escapes double quote in message")
    void toJson_escapesDoubleQuote() {
        ApiResponse<Void> r = ApiResponse.error("Say \"hello\"", 400);
        String json = SecurityResponseWriter.toJson(r);
        assertThat(json).contains("\\\"hello\\\"");
    }

    @Test
    @DisplayName("toJson escapes backslash in message")
    void toJson_escapesBackslash() {
        ApiResponse<Void> r = ApiResponse.<Void>builder()
                .status(false)
                .code(500)
                .message("path\\to\\file")
                .timestamp("2025-01-01 00:00:00")
                .trackingId(null)
                .build();
        String json = SecurityResponseWriter.toJson(r);
        assertThat(json).contains("path\\\\to\\\\file");
    }

    @Test
    @DisplayName("toJson handles null message and null trackingId")
    void toJson_handlesNulls() {
        ApiResponse<Void> r = ApiResponse.<Void>builder()
                .status(false)
                .code(403)
                .message(null)
                .timestamp("2025-01-01 00:00:00")
                .trackingId(null)
                .build();
        String json = SecurityResponseWriter.toJson(r);
        assertThat(json).contains("\"message\":\"\"");
        assertThat(json).contains("\"trackingId\":\"\"");
    }
}
