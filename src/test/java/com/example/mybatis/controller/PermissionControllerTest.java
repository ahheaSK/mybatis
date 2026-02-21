package com.example.mybatis.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.mybatis.dto.request.PermissionCreateRequest;
import com.example.mybatis.dto.request.PermissionUpdateRequest;
import com.example.mybatis.dto.response.ApiResponse;
import com.example.mybatis.dto.response.PageResponse;
import com.example.mybatis.dto.response.PermissionResponse;
import com.example.mybatis.exception.ResourceNotFoundException;
import com.example.mybatis.service.PermissionService;

@ExtendWith(MockitoExtension.class)
class PermissionControllerTest {

    @Mock
    private PermissionService permissionService;

    private PermissionController permissionController;

    @BeforeEach
    void setUp() {
        permissionController = new PermissionController(permissionService);
    }

    @Nested
    @DisplayName("list")
    class ListEndpoint {
        @Test
        @DisplayName("returns 200 with paginated permissions")
        void success() {
            List<PermissionResponse> content = List.of(new PermissionResponse(1L, "READ", "Read", null));
            PageResponse<PermissionResponse> pr = new PageResponse<>(content, 1, 10, 0);
            when(permissionService.findAll(0, 10, null, null)).thenReturn(pr);

            ResponseEntity<ApiResponse<List<PermissionResponse>>> result = permissionController.list(0, 10, null, null);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().getData()).hasSize(1);
            assertThat(result.getBody().getData().get(0).getCode()).isEqualTo("READ");
            verify(permissionService).findAll(0, 10, null, null);
        }
    }

    @Nested
    @DisplayName("getOne")
    class GetOne {
        @Test
        @DisplayName("returns 200 with permission when found")
        void success() {
            PermissionResponse perm = new PermissionResponse(1L, "WRITE", "Write", null);
            when(permissionService.findById(1L)).thenReturn(perm);

            ResponseEntity<ApiResponse<PermissionResponse>> result = permissionController.getOne(1L);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody().getData()).isEqualTo(perm);
            verify(permissionService).findById(1L);
        }

        @Test
        @DisplayName("throws when service throws ResourceNotFoundException")
        void notFound() {
            when(permissionService.findById(999L)).thenThrow(new ResourceNotFoundException("Permission", 999L));

            assertThatThrownBy(() -> permissionController.getOne(999L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("create")
    class Create {
        @Test
        @DisplayName("returns 201 and calls service")
        void success() {
            PermissionCreateRequest request = new PermissionCreateRequest("NEW", "New Perm", "Desc");

            ResponseEntity<ApiResponse<Void>> result = permissionController.create(request);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(result.getBody().getCode()).isEqualTo(201);
            verify(permissionService).create(request);
        }
    }

    @Nested
    @DisplayName("update")
    class Update {
        @Test
        @DisplayName("returns 200 and calls service")
        void success() {
            PermissionUpdateRequest request = new PermissionUpdateRequest("UPD", "Updated", null);

            ResponseEntity<ApiResponse<Void>> result = permissionController.update(1L, request);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(permissionService).update(eq(1L), eq(request));
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {
        @Test
        @DisplayName("returns 200 and calls service")
        void success() {
            ResponseEntity<ApiResponse<Void>> result = permissionController.delete(1L);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(permissionService).deleteById(1L);
        }
    }
}
