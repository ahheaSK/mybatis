package com.example.mybatis.controller;

import com.example.mybatis.dto.request.RoleCreateRequest;
import com.example.mybatis.dto.request.RoleUpdateRequest;
import com.example.mybatis.dto.response.ApiResponse;
import com.example.mybatis.dto.response.PageResponse;
import com.example.mybatis.dto.response.PaginationDto;
import com.example.mybatis.dto.response.RoleResponse;
import com.example.mybatis.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@Tag(name = "Roles", description = "CRUD and list roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @Operation(summary = "List roles", description = "Returns paginated roles, optionally filtered by code and name")
    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleResponse>>> list(
            @Parameter(description = "Zero-based page index") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Filter by code") @RequestParam(required = false) String code,
            @Parameter(description = "Filter by name") @RequestParam(required = false) String name
    ) {
        PageResponse<RoleResponse> pr = roleService.findAll(page, size, code, name);
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
        return ResponseEntity.ok(ApiResponse.successWithPage(
                pr.getContent(),
                pagination,
                "Get All With Pagination Data Successfully",
                200
        ));
    }

    @Operation(summary = "Get role by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponse>> getOne(
            @Parameter(description = "Role ID") @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(roleService.findById(id), "Get role successfully", 200));
    }

    @Operation(summary = "Create role")
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> create(@Valid @RequestBody RoleCreateRequest request) {
        roleService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(null, "Role created successfully", 201));
    }

    @Operation(summary = "Update role", description = "Updates only non-null fields")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> update(
            @Parameter(description = "Role ID") @PathVariable Long id,
            @Valid @RequestBody RoleUpdateRequest request
    ) {
        roleService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Role updated successfully", 200));
    }

    @Operation(summary = "Delete role")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "Role ID") @PathVariable Long id) {
        roleService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Role deleted successfully", 200));
    }
}
