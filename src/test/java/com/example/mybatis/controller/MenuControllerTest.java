package com.example.mybatis.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import com.example.mybatis.dto.request.MenuCreateRequest;
import com.example.mybatis.dto.request.MenuUpdateRequest;
import com.example.mybatis.dto.response.ApiResponse;
import com.example.mybatis.dto.response.MenuResponse;
import com.example.mybatis.dto.response.PageResponse;
import com.example.mybatis.exception.ResourceNotFoundException;
import com.example.mybatis.service.MenuService;

@ExtendWith(MockitoExtension.class)
class MenuControllerTest {

    @Mock
    private MenuService menuService;

    private MenuController menuController;

    @BeforeEach
    void setUp() {
        menuController = new MenuController(menuService);
    }

    @Nested
    @DisplayName("list")
    class ListEndpoint {
        @Test
        @DisplayName("returns 200 with paginated menus")
        void success() {
            MenuResponse menu = new MenuResponse(1L, "Dashboard", "/dashboard", null, false, false, "Dashboard", null, false, null, "", null, 1, null, "#000", null, null, null);
            PageResponse<MenuResponse> pr = new PageResponse<>(List.of(menu), 1, 10, 0);
            when(menuService.findAll(0, 10, null, null, null)).thenReturn(pr);

            ResponseEntity<ApiResponse<List<MenuResponse>>> result = menuController.list(0, 10, null, null, null);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().getData()).hasSize(1);
            assertThat(result.getBody().getData().get(0).getName()).isEqualTo("Dashboard");
            verify(menuService).findAll(0, 10, null, null, null);
        }
    }

    @Nested
    @DisplayName("getOne")
    class GetOne {
        @Test
        @DisplayName("returns 200 with menu when found")
        void success() {
            MenuResponse menu = new MenuResponse(1L, "Admin", "/admin", null, false, false, "Admin", null, false, null, "", null, 0, null, "#000", null, null, null);
            when(menuService.findById(1L)).thenReturn(menu);

            ResponseEntity<ApiResponse<MenuResponse>> result = menuController.getOne(1L);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody().getData()).isEqualTo(menu);
            verify(menuService).findById(1L);
        }

        @Test
        @DisplayName("throws when service throws ResourceNotFoundException")
        void notFound() {
            when(menuService.findById(999L)).thenThrow(new ResourceNotFoundException("Menu", 999L));

            assertThatThrownBy(() -> menuController.getOne(999L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("create")
    class Create {
        @Test
        @DisplayName("returns 201 and calls service")
        void success() {
            MenuCreateRequest request = new MenuCreateRequest("New Menu", "/new", null, null, null, "New", null, null, null, "", null, 0, null, "#000000");

            ResponseEntity<ApiResponse<Void>> result = menuController.create(request);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(result.getBody().getCode()).isEqualTo(201);
            verify(menuService).create(request);
        }
    }

    @Nested
    @DisplayName("update")
    class Update {
        @Test
        @DisplayName("returns 200 and calls service")
        void success() {
            MenuUpdateRequest request = new MenuUpdateRequest("Updated", "/updated", null, null, null, "Updated", null, null, null, "", null, 1, null, null);

            ResponseEntity<ApiResponse<Void>> result = menuController.update(1L, request);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(menuService).update(eq(1L), eq(request));
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {
        @Test
        @DisplayName("returns 200 and calls service")
        void success() {
            ResponseEntity<ApiResponse<Void>> result = menuController.delete(1L);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(menuService).deleteById(1L);
        }
    }
}
