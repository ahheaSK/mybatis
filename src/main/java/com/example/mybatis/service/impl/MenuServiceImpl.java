package com.example.mybatis.service.impl;

import com.example.mybatis.audit.CurrentUserService;
import com.example.mybatis.dto.request.MenuCreateRequest;
import com.example.mybatis.dto.request.MenuUpdateRequest;
import com.example.mybatis.dto.response.MenuResponse;
import com.example.mybatis.dto.response.PageResponse;
import com.example.mybatis.entity.Menu;
import com.example.mybatis.exception.BadRequestException;
import com.example.mybatis.exception.ResourceNotFoundException;
import com.example.mybatis.mapper.MenuMapper;
import com.example.mybatis.mapper.dto.MenuDtoMapper;
import com.example.mybatis.service.MenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;
    private final MenuDtoMapper menuDtoMapper;
    private final CurrentUserService currentUserService;

    public MenuServiceImpl(MenuMapper menuMapper, MenuDtoMapper menuDtoMapper, CurrentUserService currentUserService) {
        this.menuMapper = menuMapper;
        this.menuDtoMapper = menuDtoMapper;
        this.currentUserService = currentUserService;
    }

    @Override
    public PageResponse<MenuResponse> findAll(int page, int size, String name, String path, Long parentId) {
        int offset = page * size;
        List<Menu> menus = menuMapper.selectByCondition(name, path, parentId, offset, size);
        long total = menuMapper.countByCondition(name, path, parentId);
        List<MenuResponse> content = menus.stream().map(menuDtoMapper::toDTO).collect(Collectors.toList());
        return new PageResponse<>(content, total, size, page);
    }

    @Override
    public MenuResponse findById(Long id) {
        Menu menu = menuMapper.selectById(id);
        if (menu == null) throw new ResourceNotFoundException("Menu", id);
        return menuDtoMapper.toDTO(menu);
    }

    @Override
    @Transactional
    public void create(MenuCreateRequest request) {
        Menu menu = menuDtoMapper.toEntity(request);
        menu.setUsername(currentUserService.getCurrentUsername());
        if (menuMapper.insert(menu) <= 0) throw new BadRequestException("Menu creation failed");
    }

    @Override
    @Transactional
    public void update(Long id, MenuUpdateRequest request) {
        Menu existing = menuMapper.selectById(id);
        if (existing == null) throw new ResourceNotFoundException("Menu", id);
        menuDtoMapper.updateEntity(existing, request);
        existing.setId(id);
        existing.setUsername(currentUserService.getCurrentUsername());
        menuMapper.update(existing);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (menuMapper.deleteById(id, currentUserService.getCurrentUsername()) <= 0) throw new ResourceNotFoundException("Menu", id);
    }
}
