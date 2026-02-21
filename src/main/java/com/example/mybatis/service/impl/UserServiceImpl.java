package com.example.mybatis.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mybatis.audit.CurrentUserService;
import com.example.mybatis.dto.request.UserCreateRequest;
import com.example.mybatis.dto.request.UserUpdateRequest;
import com.example.mybatis.dto.response.PageResponse;
import com.example.mybatis.dto.response.UserResponse;
import com.example.mybatis.entity.User;
import com.example.mybatis.exception.BadRequestException;
import com.example.mybatis.exception.ResourceNotFoundException;
import com.example.mybatis.mapper.RoleMapper;
import com.example.mybatis.mapper.UserMapper;
import com.example.mybatis.mapper.UserRoleMapper;
import com.example.mybatis.mapper.dto.RoleDtoMapper;
import com.example.mybatis.mapper.dto.UserDtoMapper;
import com.example.mybatis.service.RoleService;
import com.example.mybatis.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleService roleService;
    private final UserDtoMapper userDtoMapper;
    private final RoleDtoMapper roleDtoMapper;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;

    public UserServiceImpl(UserMapper userMapper, RoleMapper roleMapper, UserRoleMapper userRoleMapper,
                           RoleService roleService, UserDtoMapper userDtoMapper, RoleDtoMapper roleDtoMapper,
                           PasswordEncoder passwordEncoder, CurrentUserService currentUserService) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
        this.roleService = roleService;
        this.userDtoMapper = userDtoMapper;
        this.roleDtoMapper = roleDtoMapper;
        this.passwordEncoder = passwordEncoder;
        this.currentUserService = currentUserService;
    }

    @Override
    public PageResponse<UserResponse> findAll(int page, int size, String name, String email) {
        int offset = page * size;
        List<User> users = userMapper.selectByCondition(name, email, offset, size);
        long total = userMapper.countByCondition(name, email);
        List<UserResponse> content = users.stream()
                .map(user -> toUserResponseWithRoles(user))
                .collect(Collectors.toList());
        return new PageResponse<>(content, total, size, page);
    }

    @Override
    public UserResponse findById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) throw new ResourceNotFoundException("User", id);
        return toUserResponseWithRoles(user);
    }

    private UserResponse toUserResponseWithRoles(User user) {
        UserResponse response = userDtoMapper.toDTO(user);
        response.setRoles(roleDtoMapper.toDTOList(roleMapper.selectByUserId(user.getId())));
        return response;
    }

    @Override
    @Transactional
    public void create(UserCreateRequest request) {
        roleService.validateRoleIds(request.getRoleIds());
        User user = userDtoMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setOusername(currentUserService.getCurrentUsername());
        if (userMapper.insert(user) <= 0) throw new BadRequestException("User creation failed");
        request.getRoleIds().forEach(roleId -> userRoleMapper.insert(user.getId(), roleId));
    }

    @Override
    @Transactional
    public void update(Long id, UserUpdateRequest request) {
        User existing = userMapper.selectById(id);
        if (existing == null) throw new ResourceNotFoundException("User", id);
        userDtoMapper.updateEntity(existing, request);
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        existing.setOusername(currentUserService.getCurrentUsername());
        userMapper.update(existing);
        if (request.getRoleIds() != null) {
            roleService.validateRoleIds(request.getRoleIds());
            userRoleMapper.deleteByUserId(id);
            request.getRoleIds().forEach(roleId -> userRoleMapper.insert(id, roleId));
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (userMapper.deleteById(id, currentUserService.getCurrentUsername()) <= 0) throw new ResourceNotFoundException("User", id);
    }
}
