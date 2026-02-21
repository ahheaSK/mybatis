package com.example.mybatis.service.impl;

import com.example.mybatis.audit.CurrentUserService;
import com.example.mybatis.dto.request.RoleCreateRequest;
import com.example.mybatis.dto.request.RoleUpdateRequest;
import com.example.mybatis.dto.response.PageResponse;
import com.example.mybatis.dto.response.RoleResponse;
import com.example.mybatis.entity.Role;
import com.example.mybatis.exception.BadRequestException;
import com.example.mybatis.exception.ResourceNotFoundException;
import com.example.mybatis.mapper.RoleMapper;
import com.example.mybatis.mapper.dto.RoleDtoMapper;
import com.example.mybatis.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final RoleDtoMapper roleDtoMapper;
    private final CurrentUserService currentUserService;

    public RoleServiceImpl(RoleMapper roleMapper, RoleDtoMapper roleDtoMapper,
                           CurrentUserService currentUserService) {
        this.roleMapper = roleMapper;
        this.roleDtoMapper = roleDtoMapper;
        this.currentUserService = currentUserService;
    }

    @Override
    public PageResponse<RoleResponse> findAll(int page, int size, String code, String name) {
        int offset = page * size;
        List<Role> roles = roleMapper.selectByCondition(code, name, offset, size);
        long total = roleMapper.countByCondition(code, name);
        List<RoleResponse> content = roles.stream()
                .map(roleDtoMapper::toDTO)
                .collect(Collectors.toList());
        return new PageResponse<>(content, total, size, page);
    }

    @Override
    public RoleResponse findById(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null) throw new ResourceNotFoundException("Role", id);
        return roleDtoMapper.toDTO(role);
    }

    @Override
    @Transactional
    public void create(RoleCreateRequest request) {
        Role role = roleDtoMapper.toEntity(request);
        role.setOusername(currentUserService.getCurrentUsername());
        if (roleMapper.insert(role) <= 0) throw new BadRequestException("Role creation failed");
    }

    @Override
    @Transactional
    public void update(Long id, RoleUpdateRequest request) {
        Role existing = roleMapper.selectById(id);
        if (existing == null) throw new ResourceNotFoundException("Role", id);
        roleDtoMapper.updateEntity(existing, request);
        existing.setId(id);
        existing.setOusername(currentUserService.getCurrentUsername());
        roleMapper.update(existing);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (roleMapper.deleteById(id, currentUserService.getCurrentUsername()) <= 0) throw new ResourceNotFoundException("Role", id);
    }

    @Override
    public void validateRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) return;
        List<Long> existingIds = roleMapper.selectExistingIds(roleIds);
        if (existingIds.size() != roleIds.size()) {
            List<Long> invalid = roleIds.stream()
                    .filter(id -> !existingIds.contains(id))
                    .toList();
            throw new BadRequestException("Role not found in database: " + invalid);
        }
    }
}
