package com.example.mybatis.service.impl;

import com.example.mybatis.dto.response.RoleResponse;
import com.example.mybatis.entity.Role;
import com.example.mybatis.exception.ResourceNotFoundException;
import com.example.mybatis.mapper.RoleMapper;
import com.example.mybatis.mapper.dto.RoleDtoMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Test
    @DisplayName("findById returns role when found")
    void findById_found() {
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
    @DisplayName("findById throws ResourceNotFoundException when not found")
    void findById_notFound() {
        when(roleMapper.selectById(999L)).thenReturn(null);

        assertThatThrownBy(() -> roleService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Role");

        verify(roleMapper).selectById(999L);
    }
}
