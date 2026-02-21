package com.example.mybatis.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseAccessDeniedHandlerTest {

    private ApiResponseAccessDeniedHandler handler;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        handler = new ApiResponseAccessDeniedHandler();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("handle sets status 403 and JSON body with Forbidden")
    void handle_sets403AndJsonBody() throws IOException {
        handler.handle(request, response, new AccessDeniedException("Access denied"));

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentType()).isEqualTo("application/json");
        String body = response.getContentAsString(StandardCharsets.UTF_8);
        assertThat(body).contains("\"status\":false");
        assertThat(body).contains("\"code\":403");
        assertThat(body).contains("\"message\":\"Forbidden\"");
    }
}
