package com.example.mybatis.service;

import com.example.mybatis.dto.request.PermissionCreateRequest;
import com.example.mybatis.dto.request.PermissionUpdateRequest;
import com.example.mybatis.dto.response.PageResponse;
import com.example.mybatis.dto.response.PermissionResponse;

public interface PermissionService {

    PageResponse<PermissionResponse> findAll(int page, int size, String code, String name);

    PermissionResponse findById(Long id);

    void create(PermissionCreateRequest request);

    void update(Long id, PermissionUpdateRequest request);

    void deleteById(Long id);
}
