package com.example.mybatis.service.impl;

import com.example.mybatis.audit.CurrentUserService;
import com.example.mybatis.dto.request.RoleCreateRequest;
import com.example.mybatis.dto.request.RoleUpdateRequest;
import com.example.mybatis.dto.response.MenuResponse;
import com.example.mybatis.dto.response.PageResponse;
import com.example.mybatis.dto.response.RoleResponse;
import com.example.mybatis.entity.Menu;
import com.example.mybatis.entity.Role;
import com.example.mybatis.exception.BadRequestException;
import com.example.mybatis.exception.ResourceNotFoundException;
import com.example.mybatis.mapper.MenuMapper;
import com.example.mybatis.mapper.RoleMapper;
import com.example.mybatis.mapper.RoleMenuMapper;
import com.example.mybatis.mapper.dto.MenuDtoMapper;
import com.example.mybatis.mapper.dto.RoleDtoMapper;
import com.example.mybatis.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final RoleDtoMapper roleDtoMapper;
    private final CurrentUserService currentUserService;
    private final RoleMenuMapper roleMenuMapper;
    private final MenuDtoMapper menuDtoMapper;
    private final MenuMapper menuMapper;

    public RoleServiceImpl(RoleMapper roleMapper, RoleDtoMapper roleDtoMapper,
                           CurrentUserService currentUserService, RoleMenuMapper roleMenuMapper,
                           MenuDtoMapper menuDtoMapper, MenuMapper menuMapper) {
        this.roleMapper = roleMapper;
        this.roleDtoMapper = roleDtoMapper;
        this.currentUserService = currentUserService;
        this.roleMenuMapper = roleMenuMapper;
        this.menuDtoMapper = menuDtoMapper;
        this.menuMapper = menuMapper;
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

    @Override
    public List<MenuResponse> getMenusByRoleId(Long roleId) {
        if (roleMapper.selectById(roleId) == null) throw new ResourceNotFoundException("Role", roleId);
        List<Menu> menus = roleMenuMapper.selectMenusByRoleId(roleId);
        List<MenuResponse> list = menus.stream().map(menuDtoMapper::toDTO).collect(Collectors.toList());
        return buildMenuTree(list);
    }

    /** Builds tree: items with parentId become children of their parent; returns only roots ordered by sortOrder. */
    private List<MenuResponse> buildMenuTree(List<MenuResponse> flat) {
        Comparator<MenuResponse> bySortOrder = Comparator.comparing(MenuResponse::getSortOrder, Comparator.nullsLast(Comparator.naturalOrder()));
        Map<Long, MenuResponse> byId = new LinkedHashMap<>();
        for (MenuResponse r : flat) {
            r.setChildren(new ArrayList<>());
            byId.put(r.getId(), r);
        }
        List<MenuResponse> roots = new ArrayList<>();
        for (MenuResponse r : flat) {
            if (r.getParentId() == null) {
                roots.add(r);
            } else {
                MenuResponse parent = byId.get(r.getParentId());
                if (parent != null) {
                    parent.getChildren().add(r);
                } else {
                    roots.add(r);
                }
            }
        }
        roots.sort(bySortOrder);
        for (MenuResponse r : byId.values()) {
            if (r.getChildren() != null && !r.getChildren().isEmpty()) {
                r.getChildren().sort(bySortOrder);
            }
        }
        return roots;
    }

    @Override
    @Transactional
    public void assignMenusToRole(Long roleId, List<Long> menuIds) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) throw new ResourceNotFoundException("Role", roleId);
        if (menuIds != null && !menuIds.isEmpty()) {
            List<Long> existingIds = menuMapper.selectExistingIds(menuIds);
            if (existingIds.size() != menuIds.size()) {
                List<Long> invalid = menuIds.stream().filter(id -> !existingIds.contains(id)).toList();
                throw new BadRequestException("Menu not found in database: " + invalid);
            }
        }
        roleMenuMapper.deleteByRoleId(roleId);
        if (menuIds != null) {
            for (Long menuId : menuIds) {
                roleMenuMapper.insert(roleId, menuId);
            }
        }
    }
}
