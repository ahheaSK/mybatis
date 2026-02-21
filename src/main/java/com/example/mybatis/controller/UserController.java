package com.example.mybatis.controller;

import com.example.mybatis.dto.request.UserCreateRequest;
import com.example.mybatis.dto.request.UserUpdateRequest;
import com.example.mybatis.dto.response.ApiResponse;
import com.example.mybatis.dto.response.PageResponse;
import com.example.mybatis.dto.response.PaginationDto;
import com.example.mybatis.dto.response.UserResponse;
import com.example.mybatis.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "CRUD and list users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "List users", description = "Returns paginated users, optionally filtered by name and email")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> list(
            @Parameter(description = "Zero-based page index") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Filter by name (username)") @RequestParam(required = false) String name,
            @Parameter(description = "Filter by email") @RequestParam(required = false) String email
    ) {
        log.debug("list users page={}, size={}, name={}, email={}", page, size, name, email);
        PageResponse<UserResponse> pr = userService.findAll(page, size, name, email);
        PaginationDto pagination = PaginationDto.builder()
                .pageSize(pr.getSize())
                .pageNumber(pr.getNumber() + 1)
                .totalPages(pr.getTotalPages())
                .totalElements(pr.getTotalElements())
                .numberOfElements(pr.getContent().size())
                .first(pr.getNumber() == 0)
                .last(pr.getNumber() >= pr.getTotalPages() - 1)
                .empty(pr.getContent().isEmpty())
                .build();
        ApiResponse<List<UserResponse>> response = ApiResponse.successWithPage(
                pr.getContent(),
                pagination,
                "Get All With Pagination Data Successfully",
                200
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get user by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getOne(
            @Parameter(description = "User ID") @PathVariable Long id) {
        log.debug("getOne user id={}", id);
        return ResponseEntity.ok(ApiResponse.success(userService.findById(id), "Get user successfully", 200));
    }

    @Operation(summary = "Create user")
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> create(@Valid @RequestBody UserCreateRequest request) {
        log.info("create user username={}", request.getUsername());
        userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(null, "User created successfully", 201));
    }

    @Operation(summary = "Update user", description = "Updates only non-null fields; omit password to keep current")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> update(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        log.info("update user id={}", id);
        userService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(null, "User updated successfully", 200));
    }

    @Operation(summary = "Delete user")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "User ID") @PathVariable Long id) {
        log.info("delete user id={}", id);
        userService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully", 200));
    }
}
