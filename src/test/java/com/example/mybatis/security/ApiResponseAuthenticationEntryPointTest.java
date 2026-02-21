package com.example.mybatis.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseAuthenticationEntryPointTest {

    private ApiResponseAuthenticationEntryPoint entryPoint;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        entryPoint = new ApiResponseAuthenticationEntryPoint();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("commence sets status 401 and JSON body with Unauthorized")
    void commence_sets401AndJsonBody() throws IOException {
        entryPoint.commence(request, response, null);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).isEqualTo("application/json");
        String body = response.getContentAsString(StandardCharsets.UTF_8);
        assertThat(body).contains("\"status\":false");
        assertThat(body).contains("\"code\":401");
        assertThat(body).contains("\"message\":\"Unauthorized\"");
    }
}
