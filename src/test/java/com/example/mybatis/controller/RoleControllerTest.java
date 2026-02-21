package com.example.mybatis.controller;

import com.example.mybatis.dto.request.RoleCreateRequest;
import com.example.mybatis.dto.request.RoleMenuAssignRequest;
import com.example.mybatis.dto.request.RoleUpdateRequest;
import com.example.mybatis.dto.response.ApiResponse;
import com.example.mybatis.dto.response.MenuResponse;
import com.example.mybatis.dto.response.PageResponse;
import com.example.mybatis.dto.response.RoleResponse;
import com.example.mybatis.exception.ResourceNotFoundException;
import com.example.mybatis.service.RoleService;
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
class RoleControllerTest {

    @Mock
    private RoleService roleService;

    private RoleController roleController;

    @BeforeEach
    void setUp() {
        roleController = new RoleController(roleService);
    }

    @Nested
    @DisplayName("list")
    class ListTests {
        @Test
        @DisplayName("returns 200 with paginated roles")
        void success() {
            List<RoleResponse> content = List.of(new RoleResponse(1L, "ADMIN", "Admin", null));
            PageResponse<RoleResponse> pr = new PageResponse<>(content, 1, 10, 0);
            when(roleService.findAll(0, 10, null, null)).thenReturn(pr);

            ResponseEntity<ApiResponse<List<RoleResponse>>> result = roleController.list(0, 10, null, null);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().getCode()).isEqualTo(200);
            assertThat(result.getBody().getData()).hasSize(1);
            assertThat(result.getBody().getData().get(0).getCode()).isEqualTo("ADMIN");
            verify(roleService).findAll(0, 10, null, null);
        }

        @Test
        @DisplayName("pagination first is true when page is 0")
        void firstTrueWhenPageZero() {
            List<RoleResponse> content = List.of(new RoleResponse(1L, "A", "Role A", null));
            PageResponse<RoleResponse> pr = new PageResponse<>(content, 25, 10, 0);
            when(roleService.findAll(0, 10, null, null)).thenReturn(pr);

            ResponseEntity<ApiResponse<List<RoleResponse>>> result = roleController.list(0, 10, null, null);

            assertThat(result.getBody().getPagination().isFirst()).isTrue();
            assertThat(result.getBody().getPagination().isLast()).isFalse();
            assertThat(result.getBody().getPagination().getPageNumber()).isEqualTo(1);
            assertThat(result.getBody().getPagination().getTotalPages()).isEqualTo(3);
        }

        @Test
        @DisplayName("pagination last is true when on last page")
        void lastTrueWhenOnLastPage() {
            List<RoleResponse> content = List.of(new RoleResponse(3L, "C", "Role C", null));
            PageResponse<RoleResponse> pr = new PageResponse<>(content, 25, 10, 2);
            when(roleService.findAll(2, 10, null, null)).thenReturn(pr);

            ResponseEntity<ApiResponse<List<RoleResponse>>> result = roleController.list(2, 10, null, null);

            assertThat(result.getBody().getPagination().isFirst()).isFalse();
            assertThat(result.getBody().getPagination().isLast()).isTrue();
            assertThat(result.getBody().getPagination().getPageNumber()).isEqualTo(3);
        }

        @Test
        @DisplayName("pagination first and last true when single page")
        void firstAndLastTrueWhenSinglePage() {
            List<RoleResponse> content = List.of(new RoleResponse(1L, "A", "Role A", null));
            PageResponse<RoleResponse> pr = new PageResponse<>(content, 1, 10, 0);
            when(roleService.findAll(0, 10, null, null)).thenReturn(pr);

            ResponseEntity<ApiResponse<List<RoleResponse>>> result = roleController.list(0, 10, null, null);

            assertThat(result.getBody().getPagination().isFirst()).isTrue();
            assertThat(result.getBody().getPagination().isLast()).isTrue();
        }
    }

    @Nested
    @DisplayName("getOne")
    class GetOneTests {
        @Test
        @DisplayName("returns 200 with role when found")
        void success() {
            RoleResponse role = new RoleResponse(1L, "USER", "User", null);
            when(roleService.findById(1L)).thenReturn(role);

            ResponseEntity<ApiResponse<RoleResponse>> result = roleController.getOne(1L);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().getData()).isEqualTo(role);
            verify(roleService).findById(1L);
        }

        @Test
        @DisplayName("throws when not found")
        void notFound() {
            when(roleService.findById(999L)).thenThrow(new ResourceNotFoundException("Role", 999L));

            assertThatThrownBy(() -> roleController.getOne(999L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("create")
    class CreateTests {
        @Test
        @DisplayName("returns 201 and calls service")
        void success() {
            RoleCreateRequest request = new RoleCreateRequest("NEW", "New Role", "Desc");

            ResponseEntity<ApiResponse<Void>> result = roleController.create(request);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(result.getBody().getCode()).isEqualTo(201);
            verify(roleService).create(request);
        }
    }

    @Nested
    @DisplayName("update")
    class UpdateTests {
        @Test
        @DisplayName("returns 200 and calls service")
        void success() {
            RoleUpdateRequest request = new RoleUpdateRequest("UPD", "Updated", null);

            ResponseEntity<ApiResponse<Void>> result = roleController.update(1L, request);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(roleService).update(eq(1L), eq(request));
        }
    }

    @Nested
    @DisplayName("delete")
    class DeleteTests {
        @Test
        @DisplayName("returns 200 and calls service")
        void success() {
            ResponseEntity<ApiResponse<Void>> result = roleController.delete(1L);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(roleService).deleteById(1L);
        }
    }

    @Nested
    @DisplayName("getMenusByRole")
    class GetMenusByRoleTests {
        @Test
        @DisplayName("returns 200 with menu tree when role exists")
        void success() {
            MenuResponse menu = new MenuResponse(1L, "Admin", "/admin", null, false, false, "Admin", null, false, null, "", null, 0, null, "#000", null, null, null);
            List<MenuResponse> menus = List.of(menu);
            when(roleService.getMenusByRoleId(1L)).thenReturn(menus);

            ResponseEntity<ApiResponse<List<MenuResponse>>> result = roleController.getMenusByRole(1L);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().getData()).hasSize(1);
            assertThat(result.getBody().getData().get(0).getName()).isEqualTo("Admin");
            verify(roleService).getMenusByRoleId(1L);
        }

        @Test
        @DisplayName("throws when role not found")
        void notFound() {
            when(roleService.getMenusByRoleId(999L)).thenThrow(new ResourceNotFoundException("Role", 999L));

            assertThatThrownBy(() -> roleController.getMenusByRole(999L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("assignMenus")
    class AssignMenusTests {
        @Test
        @DisplayName("returns 200 and calls service with menu ids")
        void success() {
            RoleMenuAssignRequest request = new RoleMenuAssignRequest(List.of(1L, 2L, 3L));

            ResponseEntity<ApiResponse<Void>> result = roleController.assignMenus(1L, request);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody().getMessage()).isEqualTo("Menus assigned successfully");
            verify(roleService).assignMenusToRole(1L, List.of(1L, 2L, 3L));
        }

        @Test
        @DisplayName("handles null request and calls service with null menu ids")
        void nullRequest() {
            ResponseEntity<ApiResponse<Void>> result = roleController.assignMenus(1L, null);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(roleService).assignMenusToRole(1L, null);
        }
    }
}
