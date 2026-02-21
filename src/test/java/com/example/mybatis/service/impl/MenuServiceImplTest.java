package com.example.mybatis.service.impl;

import com.example.mybatis.audit.CurrentUserService;
import com.example.mybatis.dto.request.MenuCreateRequest;
import com.example.mybatis.dto.request.MenuUpdateRequest;
import com.example.mybatis.dto.response.MenuResponse;
import com.example.mybatis.dto.response.PageResponse;
import com.example.mybatis.entity.Menu;
import com.example.mybatis.exception.BadRequestException;
import com.example.mybatis.exception.ResourceNotFoundException;
import com.example.mybatis.mapper.MenuMapper;
import com.example.mybatis.mapper.dto.MenuDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuServiceImplTest {

    @Mock
    private MenuMapper menuMapper;

    @Mock
    private MenuDtoMapper menuDtoMapper;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private MenuServiceImpl menuService;

    private Menu entity;
    private MenuResponse dto;

    @BeforeEach
    void setUp() {
        entity = new Menu(1L, "Dashboard", "/dashboard", null, false, false, "Dashboard", null, false, null, "", null, 1, null, "#000000", null, null, null, null);
        dto = new MenuResponse(1L, "Dashboard", "/dashboard", null, false, false, "Dashboard", null, false, null, "", null, 1, null, "#000000", null, null, null);
    }

    @Nested
    @DisplayName("findById")
    class FindById {
        @Test
        @DisplayName("returns menu when found")
        void found() {
            when(menuMapper.selectById(1L)).thenReturn(entity);
            when(menuDtoMapper.toDTO(entity)).thenReturn(dto);

            MenuResponse result = menuService.findById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("Dashboard");
            assertThat(result.getPath()).isEqualTo("/dashboard");
            verify(menuMapper).selectById(1L);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when not found")
        void notFound() {
            when(menuMapper.selectById(999L)).thenReturn(null);

            assertThatThrownBy(() -> menuService.findById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Menu");

            verify(menuMapper).selectById(999L);
        }
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {
        @Test
        @DisplayName("returns paginated list")
        void returnsPage() {
            when(menuMapper.selectByCondition(null, null, null, 0, 10)).thenReturn(List.of(entity));
            when(menuMapper.countByCondition(null, null, null)).thenReturn(1L);
            when(menuDtoMapper.toDTO(entity)).thenReturn(dto);

            PageResponse<MenuResponse> result = menuService.findAll(0, 10, null, null, null);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getName()).isEqualTo("Dashboard");
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getNumber()).isZero();
            assertThat(result.getSize()).isEqualTo(10);
        }

        @Test
        @DisplayName("passes filters to mapper")
        void passesFilters() {
            when(menuMapper.selectByCondition("Admin", "/admin", 1L, 20, 5)).thenReturn(List.of());
            when(menuMapper.countByCondition("Admin", "/admin", 1L)).thenReturn(0L);

            menuService.findAll(4, 5, "Admin", "/admin", 1L);

            verify(menuMapper).selectByCondition("Admin", "/admin", 1L, 20, 5);
            verify(menuMapper).countByCondition("Admin", "/admin", 1L);
        }
    }

    @Nested
    @DisplayName("create")
    class Create {
        @Test
        @DisplayName("inserts and succeeds")
        void success() {
            MenuCreateRequest request = new MenuCreateRequest("New Menu", "/new", null, null, null, "New", null, null, null, "", null, 10, null, "#000000");
            Menu toInsert = new Menu(null, "New Menu", "/new", null, null, null, "New", null, null, null, "", null, 10, null, "#000000", null, null, null, null);
            when(menuDtoMapper.toEntity(request)).thenReturn(toInsert);
            when(menuMapper.insert(any(Menu.class))).thenReturn(1);

            menuService.create(request);

            verify(menuMapper).insert(any(Menu.class));
        }

        @Test
        @DisplayName("throws BadRequestException when insert returns 0")
        void insertFails() {
            MenuCreateRequest request = new MenuCreateRequest("X", "/x", null, null, null, "X", null, null, null, "", null, 0, null, null);
            when(menuDtoMapper.toEntity(request)).thenReturn(new Menu(null, "X", "/x", null, null, null, "X", null, null, null, "", null, 0, null, null, null, null, null, null));
            when(menuMapper.insert(any(Menu.class))).thenReturn(0);

            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("Menu creation failed");

            verify(menuMapper).insert(any(Menu.class));
        }
    }

    @Nested
    @DisplayName("update")
    class Update {
        @Test
        @DisplayName("updates when entity exists")
        void success() {
            MenuUpdateRequest request = new MenuUpdateRequest("Updated", "/updated", null, null, null, "Updated", null, null, null, "", null, 2, null, null);
            when(menuMapper.selectById(1L)).thenReturn(entity);
            when(menuMapper.update(any(Menu.class))).thenReturn(1);

            menuService.update(1L, request);

            verify(menuDtoMapper).updateEntity(eq(entity), eq(request));
            verify(menuMapper).update(entity);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when not found")
        void notFound() {
            when(menuMapper.selectById(999L)).thenReturn(null);

            assertThatThrownBy(() -> menuService.update(999L, new MenuUpdateRequest("X", "/x", null, null, null, "X", null, null, null, "", null, null, null, null)))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(menuMapper).selectById(999L);
        }
    }

    @Nested
    @DisplayName("deleteById")
    class DeleteById {
        @Test
        @DisplayName("soft-deletes when exists")
        void success() {
            when(menuMapper.deleteById(1L, null)).thenReturn(1);

            menuService.deleteById(1L);

            verify(menuMapper).deleteById(1L, null);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when not found")
        void notFound() {
            when(menuMapper.deleteById(999L, null)).thenReturn(0);

            assertThatThrownBy(() -> menuService.deleteById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Menu");

            verify(menuMapper).deleteById(999L, null);
        }
    }
}
