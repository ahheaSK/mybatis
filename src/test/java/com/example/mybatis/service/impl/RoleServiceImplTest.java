package com.example.mybatis.service.impl;

import com.example.mybatis.dto.request.RoleCreateRequest;
import com.example.mybatis.dto.request.RoleUpdateRequest;
import com.example.mybatis.dto.response.PageResponse;
import com.example.mybatis.dto.response.RoleResponse;
import com.example.mybatis.entity.Role;
import com.example.mybatis.exception.BadRequestException;
import com.example.mybatis.exception.ResourceNotFoundException;
import com.example.mybatis.mapper.RoleMapper;
import com.example.mybatis.mapper.dto.RoleDtoMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class RoleServiceImplTest {

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private RoleDtoMapper roleDtoMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Nested
    @DisplayName("findById")
    class FindById {
        @Test
        @DisplayName("returns role when found")
        void found() {
            Role entity = new Role(1L, "ADMIN", "Administrator", null);
            RoleResponse dto = new RoleResponse(1L, "ADMIN", "Administrator", null);
            when(roleMapper.selectById(1L)).thenReturn(entity);
            when(roleDtoMapper.toDTO(entity)).thenReturn(dto);

            RoleResponse result = roleService.findById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo("ADMIN");
            verify(roleMapper).selectById(1L);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when not found")
        void notFound() {
            when(roleMapper.selectById(999L)).thenReturn(null);

            assertThatThrownBy(() -> roleService.findById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Role");

            verify(roleMapper).selectById(999L);
        }
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {
        @Test
        @DisplayName("returns paginated roles with code and name filter")
        void success() {
            List<Role> entities = List.of(
                    new Role(1L, "ADMIN", "Administrator", null),
                    new Role(2L, "USER", "User", null));
            List<RoleResponse> dtos = List.of(
                    new RoleResponse(1L, "ADMIN", "Administrator", null),
                    new RoleResponse(2L, "USER", "User", null));
            when(roleMapper.selectByCondition("ADMIN", "Admin", 10, 5))
                    .thenReturn(entities);
            when(roleMapper.countByCondition("ADMIN", "Admin")).thenReturn(2L);
            when(roleDtoMapper.toDTO(entities.get(0))).thenReturn(dtos.get(0));
            when(roleDtoMapper.toDTO(entities.get(1))).thenReturn(dtos.get(1));

            PageResponse<RoleResponse> result = roleService.findAll(2, 5, "ADMIN", "Admin");

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent().get(0).getCode()).isEqualTo("ADMIN");
            assertThat(result.getTotalElements()).isEqualTo(2L);
            assertThat(result.getSize()).isEqualTo(5);
            assertThat(result.getNumber()).isEqualTo(2);
            verify(roleMapper).selectByCondition("ADMIN", "Admin", 10, 5);
            verify(roleMapper).countByCondition("ADMIN", "Admin");
        }
    }

    @Nested
    @DisplayName("create")
    class Create {
        @Test
        @DisplayName("maps request, inserts and succeeds")
        void success() {
            RoleCreateRequest request = new RoleCreateRequest("NEW", "New Role", "desc");
            Role entity = new Role(null, "NEW", "New Role", "desc");
            when(roleDtoMapper.toEntity(request)).thenReturn(entity);
            when(roleMapper.insert(any(Role.class))).thenReturn(1);

            roleService.create(request);

            ArgumentCaptor<Role> captor = ArgumentCaptor.forClass(Role.class);
            verify(roleMapper).insert(captor.capture());
            assertThat(captor.getValue().getCode()).isEqualTo("NEW");
        }

        @Test
        @DisplayName("throws BadRequestException when insert returns 0")
        void insertFails() {
            RoleCreateRequest request = new RoleCreateRequest("X", "X", null);
            when(roleDtoMapper.toEntity(request)).thenReturn(new Role(null, "X", "X", null));
            when(roleMapper.insert(any(Role.class))).thenReturn(0);

            assertThatThrownBy(() -> roleService.create(request))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("Role creation failed");
        }
    }

    @Nested
    @DisplayName("update")
    class Update {
        @Test
        @DisplayName("loads existing, updates entity and calls mapper")
        void success() {
            Long id = 2L;
            Role existing = new Role(id, "OLD", "Old Name", null);
            RoleUpdateRequest request = new RoleUpdateRequest("UPD", "Updated", "desc");
            when(roleMapper.selectById(id)).thenReturn(existing);
            when(roleMapper.update(any(Role.class))).thenReturn(1);
            org.mockito.Mockito.doAnswer(inv -> {
                Role target = inv.getArgument(0);
                target.setCode(request.getCode());
                target.setName(request.getName());
                target.setDescription(request.getDescription());
                return null;
            }).when(roleDtoMapper).updateEntity(eq(existing), eq(request));

            roleService.update(id, request);

            verify(roleDtoMapper).updateEntity(existing, request);
            ArgumentCaptor<Role> captor = ArgumentCaptor.forClass(Role.class);
            verify(roleMapper).update(captor.capture());
            assertThat(captor.getValue().getId()).isEqualTo(id);
            assertThat(captor.getValue().getCode()).isEqualTo("UPD");
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when role not found")
        void notFound() {
            when(roleMapper.selectById(999L)).thenReturn(null);

            assertThatThrownBy(() -> roleService.update(999L, new RoleUpdateRequest("X", "X", null)))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Role");
            verify(roleMapper).selectById(999L);
        }
    }

    @Nested
    @DisplayName("deleteById")
    class DeleteById {
        @Test
        @DisplayName("succeeds when row deleted")
        void success() {
            when(roleMapper.deleteById(1L)).thenReturn(1);

            roleService.deleteById(1L);

            verify(roleMapper).deleteById(1L);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when no row deleted")
        void notFound() {
            when(roleMapper.deleteById(999L)).thenReturn(0);

            assertThatThrownBy(() -> roleService.deleteById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Role");
            verify(roleMapper).deleteById(999L);
        }
    }

    @Nested
    @DisplayName("validateRoleIds")
    class ValidateRoleIds {
        @Test
        @DisplayName("does nothing when roleIds is null")
        void nullIds() {
            roleService.validateRoleIds(null);
            verify(roleMapper, org.mockito.Mockito.never()).selectExistingIds(any());
        }

        @Test
        @DisplayName("does nothing when roleIds is empty")
        void emptyIds() {
            roleService.validateRoleIds(List.of());
            verify(roleMapper, org.mockito.Mockito.never()).selectExistingIds(any());
        }

        @Test
        @DisplayName("does nothing when all ids exist")
        void allExist() {
            List<Long> ids = List.of(1L, 2L);
            when(roleMapper.selectExistingIds(ids)).thenReturn(List.of(1L, 2L));

            roleService.validateRoleIds(ids);

            verify(roleMapper).selectExistingIds(ids);
        }

        @Test
        @DisplayName("throws BadRequestException with invalid ids when some do not exist")
        void someInvalid() {
            List<Long> ids = List.of(1L, 99L, 100L);
            when(roleMapper.selectExistingIds(ids)).thenReturn(List.of(1L));

            assertThatThrownBy(() -> roleService.validateRoleIds(ids))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("Role not found in database")
                    .hasMessageContaining("[99, 100]");
            verify(roleMapper).selectExistingIds(ids);
        }
    }
}
