package com.example.mybatis.service.impl;

import com.example.mybatis.dto.request.RoleCreateRequest;
import com.example.mybatis.dto.request.RoleUpdateRequest;
import com.example.mybatis.dto.response.MenuResponse;
import com.example.mybatis.dto.response.PageResponse;
import com.example.mybatis.dto.response.RoleResponse;
import com.example.mybatis.entity.Menu;
import com.example.mybatis.entity.Role;
import com.example.mybatis.exception.BadRequestException;
import com.example.mybatis.exception.ResourceNotFoundException;
import com.example.mybatis.audit.CurrentUserService;
import com.example.mybatis.mapper.MenuMapper;
import com.example.mybatis.mapper.RoleMapper;
import com.example.mybatis.mapper.RoleMenuMapper;
import com.example.mybatis.mapper.dto.MenuDtoMapper;
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

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private RoleMenuMapper roleMenuMapper;

    @Mock
    private MenuDtoMapper menuDtoMapper;

    @Mock
    private MenuMapper menuMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Nested
    @DisplayName("findById")
    class FindById {
        @Test
        @DisplayName("returns role when found")
        void found() {
            Role entity = new Role(1L, "ADMIN", "Administrator", null, null);
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
                    new Role(1L, "ADMIN", "Administrator", null, null),
                    new Role(2L, "USER", "User", null, null));
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
            Role entity = new Role(null, "NEW", "New Role", "desc", null);
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
            when(roleDtoMapper.toEntity(request)).thenReturn(new Role(null, "X", "X", null, null));
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
            Role existing = new Role(id, "OLD", "Old Name", null, null);
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
            when(currentUserService.getCurrentUsername()).thenReturn("audit-user");
            when(roleMapper.deleteById(1L, "audit-user")).thenReturn(1);

            roleService.deleteById(1L);

            verify(roleMapper).deleteById(1L, "audit-user");
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when no row deleted")
        void notFound() {
            when(currentUserService.getCurrentUsername()).thenReturn(null);
            when(roleMapper.deleteById(999L, null)).thenReturn(0);

            assertThatThrownBy(() -> roleService.deleteById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Role");
            verify(roleMapper).deleteById(999L, null);
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

    @Nested
    @DisplayName("getMenusByRoleId")
    class GetMenusByRoleId {
        @Test
        @DisplayName("returns menu tree when role exists")
        void success() {
            Role role = new Role(1L, "ADMIN", "Admin", null, null);
            Menu rootMenu = new Menu(1L, "Admin", "/admin", null, false, false, "Admin", null, false, null, "", null, 0, null, "#000", null, null, null, null);
            Menu childMenu = new Menu(6L, "Users", "/users", null, false, false, "Users", null, false, null, "", null, 0, 1L, "#000", null, null, null, null);
            MenuResponse rootDto = new MenuResponse(1L, "Admin", "/admin", null, false, false, "Admin", null, false, null, "", null, 0, null, "#000", null, null, new java.util.ArrayList<>());
            MenuResponse childDto = new MenuResponse(6L, "Users", "/users", null, false, false, "Users", null, false, null, "", null, 0, 1L, "#000", null, null, new java.util.ArrayList<>());
            when(roleMapper.selectById(1L)).thenReturn(role);
            when(roleMenuMapper.selectMenusByRoleId(1L)).thenReturn(List.of(rootMenu, childMenu));
            when(menuDtoMapper.toDTO(rootMenu)).thenReturn(rootDto);
            when(menuDtoMapper.toDTO(childMenu)).thenReturn(childDto);

            List<MenuResponse> result = roleService.getMenusByRoleId(1L);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(1L);
            assertThat(result.get(0).getChildren()).hasSize(1);
            assertThat(result.get(0).getChildren().get(0).getId()).isEqualTo(6L);
            verify(roleMenuMapper).selectMenusByRoleId(1L);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when role not found")
        void roleNotFound() {
            when(roleMapper.selectById(999L)).thenReturn(null);

            assertThatThrownBy(() -> roleService.getMenusByRoleId(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Role");
        }
    }

    @Nested
    @DisplayName("assignMenusToRole")
    class AssignMenusToRole {
        @Test
        @DisplayName("replaces assignments and inserts new menu ids")
        void success() {
            Role role = new Role(1L, "ADMIN", "Admin", null, null);
            when(roleMapper.selectById(1L)).thenReturn(role);
            when(menuMapper.selectExistingIds(List.of(2L, 3L))).thenReturn(List.of(2L, 3L));

            roleService.assignMenusToRole(1L, List.of(2L, 3L));

            verify(roleMenuMapper).deleteByRoleId(1L);
            verify(roleMenuMapper).insert(1L, 2L);
            verify(roleMenuMapper).insert(1L, 3L);
        }

        @Test
        @DisplayName("clears assignments when menuIds is empty")
        void clearAssignments() {
            Role role = new Role(1L, "ADMIN", "Admin", null, null);
            when(roleMapper.selectById(1L)).thenReturn(role);

            roleService.assignMenusToRole(1L, List.of());

            verify(roleMenuMapper).deleteByRoleId(1L);
            verify(roleMenuMapper, org.mockito.Mockito.never()).insert(any(Long.class), any(Long.class));
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when role not found")
        void roleNotFound() {
            when(roleMapper.selectById(999L)).thenReturn(null);

            assertThatThrownBy(() -> roleService.assignMenusToRole(999L, List.of(1L)))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Role");
        }

        @Test
        @DisplayName("throws BadRequestException when menu id does not exist")
        void menuNotFound() {
            Role role = new Role(1L, "ADMIN", "Admin", null, null);
            when(roleMapper.selectById(1L)).thenReturn(role);
            when(menuMapper.selectExistingIds(List.of(99L))).thenReturn(List.of());

            assertThatThrownBy(() -> roleService.assignMenusToRole(1L, List.of(99L)))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("Menu not found");
            verify(menuMapper).selectExistingIds(List.of(99L));
        }
    }
}
