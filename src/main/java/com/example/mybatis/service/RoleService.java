package com.example.mybatis.service;

import com.example.mybatis.dto.request.RoleCreateRequest;
import com.example.mybatis.dto.request.RoleUpdateRequest;
import com.example.mybatis.dto.response.MenuResponse;
import com.example.mybatis.dto.response.PageResponse;
import com.example.mybatis.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {

    PageResponse<RoleResponse> findAll(int page, int size, String code, String name);

    RoleResponse findById(Long id);

    void create(RoleCreateRequest request);

    void update(Long id, RoleUpdateRequest request);

    void deleteById(Long id);

    /** Validates that all given role ids exist in DB (and are not soft-deleted). Throws BadRequestException if any are invalid. */
    void validateRoleIds(List<Long> roleIds);

    /** Get all menus assigned to the role. */
    List<MenuResponse> getMenusByRoleId(Long roleId);

    /** Assign menus to role (replaces existing assignments). */
    void assignMenusToRole(Long roleId, List<Long> menuIds);
}
