package com.example.mybatis.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AppUserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(jwtUtil, userDetailsService);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("when no Authorization header, chain continues and context stays empty")
    void noAuthHeader_chainContinues() throws ServletException, IOException {
        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(jwtUtil, userDetailsService);
    }

    @Test
    @DisplayName("when Authorization header without Bearer prefix, chain continues")
    void authHeaderWithoutBearer_chainContinues() throws ServletException, IOException {
        request.addHeader("Authorization", "Basic xyz");

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(userDetailsService);
    }

    @Test
    @DisplayName("when valid token and user exists, sets authentication and continues")
    void validToken_setsAuthentication() throws ServletException, IOException {
        String token = "valid-jwt";
        request.addHeader("Authorization", "Bearer " + token);
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("admin")
                .password("encoded")
                .authorities("ROLE_ADMIN")
                .build();
        when(jwtUtil.getUsernameFromToken(token)).thenReturn("admin");
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtUtil.validateToken(eq(token), eq("admin"))).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("admin");
        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("when token valid but loadUserByUsername throws, chain continues without auth")
    void tokenValidButUserNotFound_chainContinuesWithoutAuth() throws ServletException, IOException {
        String token = "valid-jwt";
        request.addHeader("Authorization", "Bearer " + token);
        when(jwtUtil.getUsernameFromToken(token)).thenReturn("nobody");
        when(userDetailsService.loadUserByUsername("nobody"))
                .thenThrow(new org.springframework.security.core.userdetails.UsernameNotFoundException("not found"));

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("when token invalid (getUsernameFromToken returns null), chain continues")
    void invalidToken_chainContinues() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer bad-token");
        when(jwtUtil.getUsernameFromToken("bad-token")).thenReturn(null);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(userDetailsService);
    }

    @Test
    @DisplayName("when token valid but validateToken false, authentication not set")
    void tokenValidButValidateReturnsFalse_noAuthSet() throws ServletException, IOException {
        String token = "valid-jwt";
        request.addHeader("Authorization", "Bearer " + token);
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("admin")
                .password("encoded")
                .authorities("ROLE_ADMIN")
                .build();
        when(jwtUtil.getUsernameFromToken(token)).thenReturn("admin");
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtUtil.validateToken(eq(token), eq("admin"))).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
