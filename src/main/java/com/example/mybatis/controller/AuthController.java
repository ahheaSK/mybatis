package com.example.mybatis.controller;

import com.example.mybatis.constants.ApiMessages;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

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

    @Operation(
            summary = "Login",
            description = "Authenticate with username and password. Returns a JWT token to use in the **Authorize** button for other endpoints.",
            security = {}
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful; returns token and user info"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid username or password")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("login attempt username={}", request.getUsername());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityUser user = (SecurityUser) authentication.getPrincipal();
        String token = jwtUtil.generateToken(user.getUsername());

        List<RoleResponse> roles = roleDtoMapper.toDTOList(roleMapper.selectByUserId(user.getId()));
        List<PermissionResponse> permissions = permissionMapper.selectByUserId(user.getId()).stream()
                .map(permissionDtoMapper::toDTO)
                .collect(Collectors.toList());

        LoginResponse response = new LoginResponse(token, user.getId(), user.getUsername(), roles, permissions);
        log.info("login success userId={} username={}", user.getId(), user.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response, ApiMessages.LOGIN_SUCCESS, 200));
    }
}
