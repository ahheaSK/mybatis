package com.example.mybatis.controller;

import com.example.mybatis.dto.request.PermissionCreateRequest;
import com.example.mybatis.dto.request.PermissionUpdateRequest;
import com.example.mybatis.dto.response.ApiResponse;
import com.example.mybatis.dto.response.PageResponse;
import com.example.mybatis.dto.response.PaginationDto;
import com.example.mybatis.dto.response.PermissionResponse;
import com.example.mybatis.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@Tag(name = "Permissions", description = "CRUD and list permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Operation(summary = "List permissions", description = "Returns paginated permissions, optionally filtered by code and name")
    @GetMapping
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> list(
            @Parameter(description = "Zero-based page index") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Filter by code") @RequestParam(required = false) String code,
            @Parameter(description = "Filter by name") @RequestParam(required = false) String name
    ) {
        PageResponse<PermissionResponse> pr = permissionService.findAll(page, size, code, name);
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

    @Operation(summary = "Get permission by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PermissionResponse>> getOne(
            @Parameter(description = "Permission ID") @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(permissionService.findById(id), "Get permission successfully", 200));
    }

    @Operation(summary = "Create permission")
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> create(@Valid @RequestBody PermissionCreateRequest request) {
        permissionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(null, "Permission created successfully", 201));
    }

    @Operation(summary = "Update permission", description = "Updates only non-null fields")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> update(
            @Parameter(description = "Permission ID") @PathVariable Long id,
            @Valid @RequestBody PermissionUpdateRequest request
    ) {
        permissionService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Permission updated successfully", 200));
    }

    @Operation(summary = "Delete permission")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "Permission ID") @PathVariable Long id) {
        permissionService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Permission deleted successfully", 200));
    }
}
