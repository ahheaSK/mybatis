package com.example.mybatis.service;

import com.example.mybatis.dto.request.MenuCreateRequest;
import com.example.mybatis.dto.request.MenuUpdateRequest;
import com.example.mybatis.dto.response.MenuResponse;
import com.example.mybatis.dto.response.PageResponse;

public interface MenuService {

    PageResponse<MenuResponse> findAll(int page, int size, String name, String path, Long parentId);

    MenuResponse findById(Long id);

    void create(MenuCreateRequest request);

    void update(Long id, MenuUpdateRequest request);

    void deleteById(Long id);
}
