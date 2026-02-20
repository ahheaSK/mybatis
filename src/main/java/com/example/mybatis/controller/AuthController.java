package com.example.mybatis.controller;

import com.example.mybatis.dto.request.LoginRequest;
import com.example.mybatis.dto.response.ApiResponse;
import com.example.mybatis.dto.response.LoginResponse;
import com.example.mybatis.dto.response.PermissionResponse;
import com.example.mybatis.dto.response.RoleResponse;
import com.example.mybatis.mapper.PermissionMapper;
import com.example.mybatis.mapper.RoleMapper;
import com.example.mybatis.mapper.dto.PermissionDtoMapper;
import com.example.mybatis.mapper.dto.RoleDtoMapper;
import com.example.mybatis.security.JwtUtil;
import com.example.mybatis.security.SecurityUser;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final RoleDtoMapper roleDtoMapper;
    private final PermissionDtoMapper permissionDtoMapper;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                          RoleMapper roleMapper, PermissionMapper permissionMapper,
                          RoleDtoMapper roleDtoMapper, PermissionDtoMapper permissionDtoMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
        this.roleDtoMapper = roleDtoMapper;
        this.permissionDtoMapper = permissionDtoMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityUser user = (SecurityUser) authentication.getPrincipal();
        String token = jwtUtil.generateToken(user.getUsername());

        List<RoleResponse> roles = roleDtoMapper.toDTOList(roleMapper.selectByUserId(user.getId()));
        List<PermissionResponse> permissions = permissionMapper.selectByUserId(user.getId()).stream()
                .map(permissionDtoMapper::toDTO)
                .collect(Collectors.toList());

        LoginResponse response = new LoginResponse(token, user.getId(), user.getUsername(), roles, permissions);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful", 200));
    }
}
