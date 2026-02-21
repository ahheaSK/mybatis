package com.example.mybatis.filter;

import com.example.mybatis.dto.response.ApiResponse;
import com.example.mybatis.properties.RateLimitProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateLimitFilterTest {

    @Mock
    private FilterChain filterChain;

    private RateLimitFilter filter;
    private RateLimitProperties properties;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        properties = new RateLimitProperties();
        properties.setEnabled(true);
        properties.setRequestsPerMinute(2);
        properties.setExcludePaths(List.of("/actuator/health", "/error"));
        filter = new RateLimitFilter(properties, objectMapper);
    }

    @Nested
    @DisplayName("when enabled and path not excluded")
    class EnabledAndLimited {

        @Test
        @DisplayName("requests under limit continue chain")
        void underLimit_chainContinues() throws Exception {
            MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/roles");
            req.setRemoteAddr("192.168.1.1");
            MockHttpServletResponse res1 = new MockHttpServletResponse();
            MockHttpServletResponse res2 = new MockHttpServletResponse();

            filter.doFilter(req, res1, filterChain);
            filter.doFilter(req, res2, filterChain);

            verify(filterChain, times(2)).doFilter(any(), any());
            assertThat(res1.getStatus()).isNotEqualTo(429);
            assertThat(res2.getStatus()).isNotEqualTo(429);
        }

        @Test
        @DisplayName("request over limit returns 429 and JSON error body")
        void overLimit_returns429() throws Exception {
            MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/roles");
            req.setRemoteAddr("192.168.1.1");
            MockHttpServletResponse res1 = new MockHttpServletResponse();
            MockHttpServletResponse res2 = new MockHttpServletResponse();
            MockHttpServletResponse res3 = new MockHttpServletResponse();

            filter.doFilter(req, res1, filterChain);
            filter.doFilter(req, res2, filterChain);
            filter.doFilter(req, res3, filterChain);

            verify(filterChain, times(2)).doFilter(any(), any());
            assertThat(res3.getStatus()).isEqualTo(429);
            assertThat(res3.getContentType()).contains(MediaType.APPLICATION_JSON_VALUE);

            ApiResponse<Void> body = objectMapper.readValue(
                    res3.getContentAsString(),
                    new TypeReference<ApiResponse<Void>>() {});
            assertThat(body.getCode()).isEqualTo(429);
            assertThat(body.getMessage()).contains("Too many requests");
            assertThat(body.isStatus()).isFalse();
        }
    }

    @Nested
    @DisplayName("excluded paths")
    class ExcludedPaths {

        @Test
        @DisplayName("excluded path is never rate limited")
        void excludedPath_chainAlwaysContinues() throws Exception {
            MockHttpServletRequest req = new MockHttpServletRequest("GET", "/actuator/health");
            req.setRemoteAddr("192.168.1.1");

            for (int i = 0; i < 5; i++) {
                MockHttpServletResponse res = new MockHttpServletResponse();
                filter.doFilter(req, res, filterChain);
                assertThat(res.getStatus()).isNotEqualTo(429);
            }
            verify(filterChain, times(5)).doFilter(any(), any());
        }

        @Test
        @DisplayName("excluded path with subpath is not rate limited")
        void excludedPathSubpath_chainContinues() throws Exception {
            MockHttpServletRequest req = new MockHttpServletRequest("GET", "/actuator/health/readiness");
            req.setRemoteAddr("192.168.1.1");
            MockHttpServletResponse res = new MockHttpServletResponse();

            filter.doFilter(req, res, filterChain);

            verify(filterChain).doFilter(any(), any());
            assertThat(res.getStatus()).isNotEqualTo(429);
        }
    }

    @Nested
    @DisplayName("when disabled")
    class Disabled {

        @Test
        @DisplayName("chain always continues regardless of request count")
        void disabled_chainAlwaysContinues() throws Exception {
            properties.setEnabled(false);
            MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/roles");
            req.setRemoteAddr("192.168.1.1");

            for (int i = 0; i < 5; i++) {
                MockHttpServletResponse res = new MockHttpServletResponse();
                filter.doFilter(req, res, filterChain);
                assertThat(res.getStatus()).isNotEqualTo(429);
            }
            verify(filterChain, times(5)).doFilter(any(), any());
        }
    }

    @Nested
    @DisplayName("client key resolution")
    class ClientKey {

        @Test
        @DisplayName("X-Forwarded-For used when present")
        void xForwardedFor_usedAsClientKey() throws Exception {
            MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/roles");
            req.addHeader("X-Forwarded-For", "10.0.0.1");
            req.setRemoteAddr("192.168.1.1");

            filter.doFilter(req, new MockHttpServletResponse(), filterChain);
            filter.doFilter(req, new MockHttpServletResponse(), filterChain);
            MockHttpServletResponse res3 = new MockHttpServletResponse();
            filter.doFilter(req, res3, filterChain);

            verify(filterChain, times(2)).doFilter(any(), any());
            assertThat(res3.getStatus()).isEqualTo(429);
        }

        @Test
        @DisplayName("different IPs have separate buckets")
        void differentIps_separateBuckets() throws Exception {
            MockHttpServletRequest req1 = new MockHttpServletRequest("GET", "/api/roles");
            req1.setRemoteAddr("192.168.1.1");
            MockHttpServletRequest req2 = new MockHttpServletRequest("GET", "/api/roles");
            req2.setRemoteAddr("192.168.1.2");

            filter.doFilter(req1, new MockHttpServletResponse(), filterChain);
            filter.doFilter(req1, new MockHttpServletResponse(), filterChain);
            MockHttpServletResponse res1Third = new MockHttpServletResponse();
            filter.doFilter(req1, res1Third, filterChain);

            filter.doFilter(req2, new MockHttpServletResponse(), filterChain);
            MockHttpServletResponse res2Second = new MockHttpServletResponse();
            filter.doFilter(req2, res2Second, filterChain);

            assertThat(res1Third.getStatus()).isEqualTo(429);
            assertThat(res2Second.getStatus()).isNotEqualTo(429);
            verify(filterChain, times(4)).doFilter(any(), any());
        }
    }
}
