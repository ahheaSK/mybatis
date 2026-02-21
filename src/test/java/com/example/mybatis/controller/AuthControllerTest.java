package com.example.mybatis.controller;

import com.example.mybatis.dto.response.PermissionResponse;
import com.example.mybatis.dto.response.RoleResponse;
import com.example.mybatis.entity.Permission;
import com.example.mybatis.entity.Role;
import com.example.mybatis.entity.User;
import com.example.mybatis.mapper.PermissionMapper;
import com.example.mybatis.mapper.RoleMapper;
import com.example.mybatis.mapper.dto.PermissionDtoMapper;
import com.example.mybatis.mapper.dto.RoleDtoMapper;
import com.example.mybatis.security.JwtUtil;
import com.example.mybatis.security.SecurityUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private PermissionMapper permissionMapper;

    @Mock
    private RoleDtoMapper roleDtoMapper;

    @Mock
    private PermissionDtoMapper permissionDtoMapper;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController(
                authenticationManager, jwtUtil,
                roleMapper, permissionMapper,
                roleDtoMapper, permissionDtoMapper);
    }

    @Nested
    @DisplayName("login")
    class Login {
        @Test
        @DisplayName("returns 200 with token, roles and permissions when credentials valid")
        void success() {
            User user = new User(1L, "testuser", "encoded", "test@example.com", true, null, null, null, null);
            SecurityUser securityUser = new SecurityUser(user, List.of("USER"));
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities()));
            when(jwtUtil.generateToken("testuser")).thenReturn("jwt-token-123");
            when(roleMapper.selectByUserId(1L)).thenReturn(List.of(new Role(1L, "USER", "User", null, null)));
            when(permissionMapper.selectByUserId(1L)).thenReturn(List.of(new Permission(1L, "READ", "Read", null, null)));
            when(roleDtoMapper.toDTOList(any())).thenReturn(List.of(new RoleResponse(1L, "USER", "User", null)));
            when(permissionDtoMapper.toDTO(any(Permission.class))).thenReturn(new PermissionResponse(1L, "READ", "Read", null));

            ResponseEntity<?> result = authController.login(new com.example.mybatis.dto.request.LoginRequest("testuser", "secret"));

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody()).isNotNull();
            com.example.mybatis.dto.response.ApiResponse<?> body = (com.example.mybatis.dto.response.ApiResponse<?>) result.getBody();
            assertThat(body.getCode()).isEqualTo(200);
            assertThat(body.getData()).isNotNull();
            com.example.mybatis.dto.response.LoginResponse data = (com.example.mybatis.dto.response.LoginResponse) body.getData();
            assertThat(data.getToken()).isEqualTo("jwt-token-123");
            assertThat(data.getUsername()).isEqualTo("testuser");
            assertThat(data.getRoles()).hasSize(1);
            assertThat(data.getPermissions()).hasSize(1);
            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtUtil).generateToken("testuser");
            verify(roleMapper).selectByUserId(1L);
            verify(permissionMapper).selectByUserId(1L);
        }

        @Test
        @DisplayName("throws BadCredentialsException when credentials invalid")
        void invalidCredentials() {
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            assertThatThrownBy(() -> authController.login(new com.example.mybatis.dto.request.LoginRequest("bad", "wrong")))
                    .isInstanceOf(BadCredentialsException.class);

            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        }
    }
}
