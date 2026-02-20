package com.example.mybatis.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mybatis.dto.request.UserCreateRequest;
import com.example.mybatis.dto.request.UserUpdateRequest;
import com.example.mybatis.dto.response.PageResponse;
import com.example.mybatis.dto.response.UserResponse;
import com.example.mybatis.entity.User;
import com.example.mybatis.exception.BadRequestException;
import com.example.mybatis.exception.ResourceNotFoundException;
import com.example.mybatis.mapper.UserMapper;
import com.example.mybatis.mapper.dto.UserDtoMapper;
import com.example.mybatis.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserDtoMapper userDtoMapper;

    public UserServiceImpl(UserMapper userMapper, UserDtoMapper userDtoMapper) {
        this.userMapper = userMapper;
        this.userDtoMapper = userDtoMapper;
    }

    @Override
    public PageResponse<UserResponse> findAll(int page, int size, String name, String email) {
        int offset = page * size;
        List<User> users = userMapper.selectByCondition(name, email, offset, size);
        long total = userMapper.countByCondition(name, email);
        List<UserResponse> content = users.stream()
                .map(userDtoMapper::toDTO)
                .collect(Collectors.toList());
        return new PageResponse<>(content, total, size, page);
    }

    @Override
    public UserResponse findById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) throw new ResourceNotFoundException("User", id);
        return userDtoMapper.toDTO(user);
    }

    @Override
    @Transactional
    public void create(UserCreateRequest request) {
        User user = userDtoMapper.toEntity(request);
        if (userMapper.insert(user) <= 0) throw new BadRequestException("User creation failed");
    }

    @Override
    @Transactional
    public void update(Long id, UserUpdateRequest request) {
        User existing = userMapper.selectById(id);
        if (existing == null) throw new ResourceNotFoundException("User", id);
        userDtoMapper.updateEntity(existing, request);
        userMapper.update(existing);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (userMapper.deleteById(id) <= 0) throw new ResourceNotFoundException("User", id);
    }
}
