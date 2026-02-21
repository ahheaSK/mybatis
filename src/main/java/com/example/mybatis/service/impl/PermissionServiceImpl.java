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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements PermissionService {

    private static final Logger log = LoggerFactory.getLogger(PermissionServiceImpl.class);

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
        log.debug("findAll page={}, size={}, code={}, name={}", page, size, code, name);
        int offset = page * size;
        List<Permission> permissions = permissionMapper.selectByCondition(code, name, offset, size);
        long total = permissionMapper.countByCondition(code, name);
        List<PermissionResponse> content = permissions.stream()
                .map(permissionDtoMapper::toDTO)
                .collect(Collectors.toList());
        log.info("findAll permissions returned {} items, total={}", content.size(), total);
        return new PageResponse<>(content, total, size, page);
    }

    @Override
    public PermissionResponse findById(Long id) {
        log.debug("findById permission id={}", id);
        Permission permission = permissionMapper.selectById(id);
        if (permission == null) {
            log.warn("findById permission not found id={}", id);
            throw new ResourceNotFoundException("Permission", id);
        }
        return permissionDtoMapper.toDTO(permission);
    }

    @Override
    @Transactional
    public void create(PermissionCreateRequest request) {
        log.info("create permission code={}", request.getCode());
        Permission permission = permissionDtoMapper.toEntity(request);
        permission.setOusername(currentUserService.getCurrentUsername());
        if (permissionMapper.insert(permission) <= 0) {
            log.error("create permission failed code={}", request.getCode());
            throw new BadRequestException("Permission creation failed");
        }
        log.info("create permission success id={} code={}", permission.getId(), request.getCode());
    }

    @Override
    @Transactional
    public void update(Long id, PermissionUpdateRequest request) {
        log.info("update permission id={}", id);
        Permission existing = permissionMapper.selectById(id);
        if (existing == null) {
            log.warn("update permission not found id={}", id);
            throw new ResourceNotFoundException("Permission", id);
        }
        permissionDtoMapper.updateEntity(existing, request);
        existing.setId(id);
        existing.setOusername(currentUserService.getCurrentUsername());
        permissionMapper.update(existing);
        log.info("update permission success id={}", id);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("deleteById permission id={}", id);
        if (permissionMapper.deleteById(id) <= 0) {
            log.warn("deleteById permission not found id={}", id);
            throw new ResourceNotFoundException("Permission", id);
        }
        log.info("deleteById permission success id={}", id);
    }
}
