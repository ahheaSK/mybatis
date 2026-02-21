package com.example.mybatis.service.impl;

import com.example.mybatis.dto.request.UserCreateRequest;
import com.example.mybatis.dto.request.UserUpdateRequest;
import com.example.mybatis.dto.response.PageResponse;
import com.example.mybatis.dto.response.RoleResponse;
import com.example.mybatis.dto.response.UserResponse;
import com.example.mybatis.entity.Role;
import com.example.mybatis.entity.User;
import com.example.mybatis.exception.BadRequestException;
import com.example.mybatis.exception.ResourceNotFoundException;
import com.example.mybatis.mapper.RoleMapper;
import com.example.mybatis.mapper.UserMapper;
import com.example.mybatis.mapper.UserRoleMapper;
import com.example.mybatis.mapper.dto.RoleDtoMapper;
import com.example.mybatis.mapper.dto.UserDtoMapper;
import com.example.mybatis.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private UserRoleMapper userRoleMapper;

    @Mock
    private RoleService roleService;

    @Mock
    private UserDtoMapper userDtoMapper;

    @Mock
    private RoleDtoMapper roleDtoMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User userEntity;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        userEntity = new User(1L, "jane", "encoded", "jane@example.com", true, null, null, null);
        userResponse = new UserResponse(1L, "jane", "jane@example.com", true, null, null, List.of());
    }

    @Nested
    @DisplayName("findById")
    class FindById {
        @Test
        @DisplayName("returns user with roles when found")
        void found() {
            when(userMapper.selectById(1L)).thenReturn(userEntity);
            when(userDtoMapper.toDTO(userEntity)).thenReturn(userResponse);
            when(roleMapper.selectByUserId(1L)).thenReturn(List.of(new Role(1L, "USER", "User", null)));
            when(roleDtoMapper.toDTOList(any())).thenReturn(List.of(new RoleResponse(1L, "USER", "User", null)));

            UserResponse result = userService.findById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getUsername()).isEqualTo("jane");
            assertThat(result.getRoles()).hasSize(1);
            verify(userMapper).selectById(1L);
            verify(roleMapper).selectByUserId(1L);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when not found")
        void notFound() {
            when(userMapper.selectById(999L)).thenReturn(null);

            assertThatThrownBy(() -> userService.findById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User");

            verify(userMapper).selectById(999L);
        }
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {
        @Test
        @DisplayName("returns paginated list with roles")
        void returnsPage() {
            when(userMapper.selectByCondition(null, null, 0, 10)).thenReturn(List.of(userEntity));
            when(userMapper.countByCondition(null, null)).thenReturn(1L);
            when(userDtoMapper.toDTO(userEntity)).thenReturn(userResponse);
            when(roleMapper.selectByUserId(1L)).thenReturn(List.of());
            when(roleDtoMapper.toDTOList(any())).thenReturn(List.of());

            PageResponse<UserResponse> result = userService.findAll(0, 10, null, null);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getUsername()).isEqualTo("jane");
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getNumber()).isZero();
            assertThat(result.getSize()).isEqualTo(10);
            verify(userMapper).selectByCondition(null, null, 0, 10);
            verify(userMapper).countByCondition(null, null);
        }
    }

    @Nested
    @DisplayName("create")
    class Create {
        @Test
        @DisplayName("validates roleIds, encodes password, inserts user and user_role")
        void success() {
            UserCreateRequest request = new UserCreateRequest("newuser", "plain", "new@example.com", true, List.of(1L, 2L));
            User entityToInsert = new User(null, "newuser", null, "new@example.com", true, null, null, null);
            when(userDtoMapper.toEntity(request)).thenReturn(entityToInsert);
            when(passwordEncoder.encode("plain")).thenReturn("encoded");
            when(userMapper.insert(any(User.class))).thenAnswer(inv -> {
                User u = inv.getArgument(0);
                u.setId(10L);
                return 1;
            });

            userService.create(request);

            verify(roleService).validateRoleIds(List.of(1L, 2L));
            verify(passwordEncoder).encode("plain");
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userMapper).insert(userCaptor.capture());
            assertThat(userCaptor.getValue().getPassword()).isEqualTo("encoded");
            verify(userRoleMapper).insert(10L, 1L);
            verify(userRoleMapper).insert(10L, 2L);
        }

        @Test
        @DisplayName("throws BadRequestException when insert returns 0")
        void insertFails() {
            UserCreateRequest request = new UserCreateRequest("x", "y", "x@e.com", true, List.of(1L));
            when(userDtoMapper.toEntity(request)).thenReturn(new User(null, "x", null, "x@e.com", true, null, null, null));
            when(passwordEncoder.encode("y")).thenReturn("enc");
            when(userMapper.insert(any(User.class))).thenReturn(0);

            assertThatThrownBy(() -> userService.create(request))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("User creation failed");

            verify(roleService).validateRoleIds(List.of(1L));
            verify(userRoleMapper, never()).insert(any(), any(Long.class));
        }
    }

    @Nested
    @DisplayName("update")
    class Update {
        @Test
        @DisplayName("updates user when found, no new password")
        void successNoPasswordChange() {
            UserUpdateRequest request = new UserUpdateRequest("updated", null, "up@example.com", false, null);
            when(userMapper.selectById(1L)).thenReturn(userEntity);
            when(userMapper.update(any(User.class))).thenReturn(1);

            userService.update(1L, request);

            verify(userDtoMapper).updateEntity(eq(userEntity), eq(request));
            verify(passwordEncoder, never()).encode(any());
            verify(userMapper).update(userEntity);
            verify(userRoleMapper, never()).deleteByUserId(any());
        }

        @Test
        @DisplayName("encodes and sets password when request has new password")
        void successWithNewPassword() {
            UserUpdateRequest request = new UserUpdateRequest(null, "newpass", null, null, null);
            when(userMapper.selectById(1L)).thenReturn(userEntity);
            when(passwordEncoder.encode("newpass")).thenReturn("encodedNew");
            when(userMapper.update(any(User.class))).thenReturn(1);

            userService.update(1L, request);

            verify(passwordEncoder).encode("newpass");
            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userMapper).update(captor.capture());
            assertThat(captor.getValue().getPassword()).isEqualTo("encodedNew");
        }

        @Test
        @DisplayName("updates roles when roleIds provided")
        void successWithRoleIds() {
            UserUpdateRequest request = new UserUpdateRequest(null, null, null, null, List.of(2L, 3L));
            when(userMapper.selectById(1L)).thenReturn(userEntity);
            when(userMapper.update(any(User.class))).thenReturn(1);

            userService.update(1L, request);

            verify(roleService).validateRoleIds(List.of(2L, 3L));
            verify(userRoleMapper).deleteByUserId(1L);
            verify(userRoleMapper).insert(1L, 2L);
            verify(userRoleMapper).insert(1L, 3L);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when user not found")
        void notFound() {
            when(userMapper.selectById(999L)).thenReturn(null);

            assertThatThrownBy(() -> userService.update(999L, new UserUpdateRequest("x", null, null, null, null)))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User");

            verify(userMapper).selectById(999L);
            verify(userMapper, never()).update(any());
        }
    }

    @Nested
    @DisplayName("deleteById")
    class DeleteById {
        @Test
        @DisplayName("calls delete when user exists")
        void success() {
            when(userMapper.deleteById(1L)).thenReturn(1);

            userService.deleteById(1L);

            verify(userMapper).deleteById(1L);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when not found")
        void notFound() {
            when(userMapper.deleteById(999L)).thenReturn(0);

            assertThatThrownBy(() -> userService.deleteById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User");

            verify(userMapper).deleteById(999L);
        }
    }
}
