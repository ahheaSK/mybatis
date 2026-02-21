package com.example.mybatis.audit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class CurrentUserServiceImplTest {

    private CurrentUserServiceImpl currentUserService;

    @BeforeEach
    void setUp() {
        currentUserService = new CurrentUserServiceImpl();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("getCurrentUsername")
    class GetCurrentUsername {

        @Test
        @DisplayName("returns username when authenticated")
        void returnsUsernameWhenAuthenticated() {
            Authentication auth = new UsernamePasswordAuthenticationToken("jane", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
            SecurityContextHolder.getContext().setAuthentication(auth);

            String result = currentUserService.getCurrentUsername();

            assertThat(result).isEqualTo("jane");
        }

        @Test
        @DisplayName("returns null when context has no authentication")
        void returnsNullWhenNoAuthentication() {
            SecurityContextHolder.clearContext();

            String result = currentUserService.getCurrentUsername();

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("returns null when principal is anonymousUser")
        void returnsNullWhenAnonymousUser() {
            Authentication auth = new UsernamePasswordAuthenticationToken("anonymousUser", null);
            SecurityContextHolder.getContext().setAuthentication(auth);

            String result = currentUserService.getCurrentUsername();

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("returns null when not authenticated")
        void returnsNullWhenNotAuthenticated() {
            Authentication auth = mock(Authentication.class);
            given(auth.isAuthenticated()).willReturn(false);
            given(auth.getPrincipal()).willReturn("someone");
            given(auth.getName()).willReturn("someone");
            SecurityContextHolder.getContext().setAuthentication(auth);

            String result = currentUserService.getCurrentUsername();

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("returns name when authenticated with custom principal")
        void returnsNameWhenAuthenticatedWithCustomPrincipal() {
            Authentication auth = mock(Authentication.class);
            given(auth.isAuthenticated()).willReturn(true);
            given(auth.getPrincipal()).willReturn("admin");
            given(auth.getName()).willReturn("admin");
            SecurityContextHolder.getContext().setAuthentication(auth);

            String result = currentUserService.getCurrentUsername();

            assertThat(result).isEqualTo("admin");
        }
    }
}
