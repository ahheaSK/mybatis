package com.example.mybatis.service.impl;

import com.example.mybatis.audit.CurrentUserService;
import com.example.mybatis.dto.request.PermissionCreateRequest;
import com.example.mybatis.dto.request.PermissionUpdateRequest;
import com.example.mybatis.dto.response.PageResponse;
import com.example.mybatis.dto.response.PermissionResponse;
import com.example.mybatis.entity.Permission;
import com.example.mybatis.exception.BadRequestException;
import com.example.mybatis.exception.ResourceNotFoundException;
import com.example.mybatis.mapper.PermissionMapper;
import com.example.mybatis.mapper.dto.PermissionDtoMapper;
import com.example.mybatis.service.PermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionMapper permissionMapper;
    private final PermissionDtoMapper permissionDtoMapper;
    private final CurrentUserService currentUserService;

    public PermissionServiceImpl(PermissionMapper permissionMapper, PermissionDtoMapper permissionDtoMapper,
                                 CurrentUserService currentUserService) {
        this.permissionMapper = permissionMapper;
        this.permissionDtoMapper = permissionDtoMapper;
        this.currentUserService = currentUserService;
    }

    @Override
    public PageResponse<PermissionResponse> findAll(int page, int size, String code, String name) {
        int offset = page * size;
        List<Permission> permissions = permissionMapper.selectByCondition(code, name, offset, size);
        long total = permissionMapper.countByCondition(code, name);
        List<PermissionResponse> content = permissions.stream()
                .map(permissionDtoMapper::toDTO)
                .collect(Collectors.toList());
        return new PageResponse<>(content, total, size, page);
    }

    @Override
    public PermissionResponse findById(Long id) {
        Permission permission = permissionMapper.selectById(id);
        if (permission == null) {
            throw new ResourceNotFoundException("Permission", id);
        }
        return permissionDtoMapper.toDTO(permission);
    }

    @Override
    @Transactional
    public void create(PermissionCreateRequest request) {
        Permission permission = permissionDtoMapper.toEntity(request);
        permission.setOusername(currentUserService.getCurrentUsername());
        if (permissionMapper.insert(permission) <= 0) {
            throw new BadRequestException("Permission creation failed");
        }
    }

    @Override
    @Transactional
    public void update(Long id, PermissionUpdateRequest request) {
        Permission existing = permissionMapper.selectById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("Permission", id);
        }
        permissionDtoMapper.updateEntity(existing, request);
        existing.setId(id);
        existing.setOusername(currentUserService.getCurrentUsername());
        permissionMapper.update(existing);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (permissionMapper.deleteById(id) <= 0) {
            throw new ResourceNotFoundException("Permission", id);
        }
    }
}
