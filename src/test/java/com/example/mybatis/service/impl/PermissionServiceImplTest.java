package com.example.mybatis.service.impl;

import com.example.mybatis.dto.request.PermissionCreateRequest;
import com.example.mybatis.dto.request.PermissionUpdateRequest;
import com.example.mybatis.dto.response.PermissionResponse;
import com.example.mybatis.entity.Permission;
import com.example.mybatis.exception.ResourceNotFoundException;
import com.example.mybatis.mapper.PermissionMapper;
import com.example.mybatis.mapper.dto.PermissionDtoMapper;
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
class PermissionServiceImplTest {

    @Mock
    private PermissionMapper permissionMapper;

    @Mock
    private PermissionDtoMapper permissionDtoMapper;

    @InjectMocks
    private PermissionServiceImpl permissionService;

    private Permission entity;
    private PermissionResponse dto;

    @BeforeEach
    void setUp() {
        entity = new Permission(1L, "READ_USERS", "Read users", "View user list");
        dto = new PermissionResponse(1L, "READ_USERS", "Read users", "View user list");
    }

    @Nested
    @DisplayName("findById")
    class FindById {
        @Test
        @DisplayName("returns permission when found")
        void found() {
            when(permissionMapper.selectById(1L)).thenReturn(entity);
            when(permissionDtoMapper.toDTO(entity)).thenReturn(dto);

            PermissionResponse result = permissionService.findById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getCode()).isEqualTo("READ_USERS");
            verify(permissionMapper).selectById(1L);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when not found")
        void notFound() {
            when(permissionMapper.selectById(999L)).thenReturn(null);

            assertThatThrownBy(() -> permissionService.findById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Permission");

            verify(permissionMapper).selectById(999L);
        }
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {
        @Test
        @DisplayName("returns paginated list")
        void returnsPage() {
            when(permissionMapper.selectByCondition(null, null, 0, 10))
                    .thenReturn(List.of(entity));
            when(permissionMapper.countByCondition(null, null)).thenReturn(1L);
            when(permissionDtoMapper.toDTO(entity)).thenReturn(dto);

            var result = permissionService.findAll(0, 10, null, null);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getCode()).isEqualTo("READ_USERS");
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getNumber()).isZero();
            assertThat(result.getSize()).isEqualTo(10);
        }
    }

    @Nested
    @DisplayName("create")
    class Create {
        @Test
        @DisplayName("inserts and sets id on entity")
        void success() {
            PermissionCreateRequest request = new PermissionCreateRequest("NEW_CODE", "New", "Desc");
            when(permissionDtoMapper.toEntity(request)).thenReturn(new Permission(null, "NEW_CODE", "New", "Desc"));
            when(permissionMapper.insert(any(Permission.class))).thenAnswer(inv -> {
                Permission p = inv.getArgument(0);
                p.setId(2L);
                return 1;
            });

            permissionService.create(request);

            verify(permissionMapper).insert(any(Permission.class));
        }
    }

    @Nested
    @DisplayName("update")
    class Update {
        @Test
        @DisplayName("updates when entity exists")
        void success() {
            PermissionUpdateRequest request = new PermissionUpdateRequest("UPDATED", "Updated name", null);
            when(permissionMapper.selectById(1L)).thenReturn(entity);
            when(permissionMapper.update(any(Permission.class))).thenReturn(1);

            permissionService.update(1L, request);

            verify(permissionDtoMapper).updateEntity(eq(entity), eq(request));
            verify(permissionMapper).update(entity);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when not found")
        void notFound() {
            when(permissionMapper.selectById(999L)).thenReturn(null);

            assertThatThrownBy(() -> permissionService.update(999L, new PermissionUpdateRequest("X", "Y", null)))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(permissionMapper).selectById(999L);
        }
    }

    @Nested
    @DisplayName("deleteById")
    class DeleteById {
        @Test
        @DisplayName("deletes when exists")
        void success() {
            when(permissionMapper.deleteById(1L)).thenReturn(1);

            permissionService.deleteById(1L);

            verify(permissionMapper).deleteById(1L);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when not found")
        void notFound() {
            when(permissionMapper.deleteById(999L)).thenReturn(0);

            assertThatThrownBy(() -> permissionService.deleteById(999L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(permissionMapper).deleteById(999L);
        }
    }
}
