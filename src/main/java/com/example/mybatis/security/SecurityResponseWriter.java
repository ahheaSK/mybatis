package com.example.mybatis.security;

import com.example.mybatis.dto.response.ApiResponse;

/**
 * Writes ApiResponse as JSON without depending on Jackson in security package
 * (avoids ObjectMapper resolution issues in some IDE/classpath setups).
 */
final class SecurityResponseWriter {

    private SecurityResponseWriter() {
    }

    static String toJson(ApiResponse<?> r) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"status\":").append(r.isStatus());
        sb.append(",\"code\":").append(r.getCode());
        sb.append(",\"message\":\"").append(escape(r.getMessage())).append("\"");
        sb.append(",\"timestamp\":\"").append(escape(r.getTimestamp())).append("\"");
        sb.append(",\"trackingId\":\"").append(r.getTrackingId() != null ? r.getTrackingId().toString() : "").append("\"}");
        return sb.toString();
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
