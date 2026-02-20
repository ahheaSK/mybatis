package com.example.mybatis.service;

import com.example.mybatis.dto.request.UserCreateRequest;
import com.example.mybatis.dto.request.UserUpdateRequest;
import com.example.mybatis.dto.response.PageResponse;
import com.example.mybatis.dto.response.UserResponse;

public interface UserService {

    PageResponse<UserResponse> findAll(int page, int size, String name, String email);

    UserResponse findById(Long id);

    void create(UserCreateRequest request);

    void update(Long id, UserUpdateRequest request);

    void deleteById(Long id);
}
