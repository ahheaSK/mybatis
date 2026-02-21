package com.example.mybatis.controller;

import com.example.mybatis.dto.request.UserCreateRequest;
import com.example.mybatis.dto.request.UserUpdateRequest;
import com.example.mybatis.dto.response.ApiResponse;
import com.example.mybatis.dto.response.PageResponse;
import com.example.mybatis.dto.response.UserResponse;
import com.example.mybatis.exception.ResourceNotFoundException;
import com.example.mybatis.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController(userService);
    }

    @Nested
    @DisplayName("list")
    class ListEndpoint {
        @Test
        @DisplayName("returns 200 with paginated users")
        void success() {
            UserResponse user = new UserResponse(1L, "jane", "jane@example.com", true, null, null, List.of());
            PageResponse<UserResponse> pr = new PageResponse<>(List.of(user), 1, 10, 0);
            when(userService.findAll(0, 10, null, null)).thenReturn(pr);

            ResponseEntity<ApiResponse<List<UserResponse>>> result = userController.list(0, 10, null, null);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().getData()).hasSize(1);
            assertThat(result.getBody().getData().get(0).getUsername()).isEqualTo("jane");
            verify(userService).findAll(0, 10, null, null);
        }
    }

    @Nested
    @DisplayName("getOne")
    class GetOne {
        @Test
        @DisplayName("returns 200 with user when found")
        void success() {
            UserResponse user = new UserResponse(1L, "john", "john@example.com", true, null, null, List.of());
            when(userService.findById(1L)).thenReturn(user);

            ResponseEntity<ApiResponse<UserResponse>> result = userController.getOne(1L);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody().getData()).isEqualTo(user);
            verify(userService).findById(1L);
        }

        @Test
        @DisplayName("throws when service throws ResourceNotFoundException")
        void notFound() {
            when(userService.findById(999L)).thenThrow(new ResourceNotFoundException("User", 999L));

            assertThatThrownBy(() -> userController.getOne(999L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("create")
    class Create {
        @Test
        @DisplayName("returns 201 and calls service")
        void success() {
            UserCreateRequest request = new UserCreateRequest("newuser", "secret", "new@example.com", true, List.of(1L));

            ResponseEntity<ApiResponse<Void>> result = userController.create(request);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(result.getBody().getCode()).isEqualTo(201);
            assertThat(result.getBody().getMessage()).contains("User created successfully");
            verify(userService).create(request);
        }
    }

    @Nested
    @DisplayName("update")
    class Update {
        @Test
        @DisplayName("returns 200 and calls service")
        void success() {
            UserUpdateRequest request = new UserUpdateRequest("updated", null, null, null, null);

            ResponseEntity<ApiResponse<Void>> result = userController.update(1L, request);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(userService).update(eq(1L), eq(request));
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {
        @Test
        @DisplayName("returns 200 and calls service")
        void success() {
            ResponseEntity<ApiResponse<Void>> result = userController.delete(1L);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(userService).deleteById(1L);
        }
    }
}
