package com.example.mybatis.controller;

import com.example.mybatis.dto.request.RoleCreateRequest;
import com.example.mybatis.dto.request.RoleUpdateRequest;
import com.example.mybatis.dto.response.ApiResponse;
import com.example.mybatis.dto.response.PageResponse;
import com.example.mybatis.dto.response.PaginationDto;
import com.example.mybatis.dto.response.RoleResponse;
import com.example.mybatis.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name
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

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponse>> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(roleService.findById(id), "Get role successfully", 200));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> create(@Valid @RequestBody RoleCreateRequest request) {
        roleService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(null, "Role created successfully", 201));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> update(
            @PathVariable Long id,
            @Valid @RequestBody RoleUpdateRequest request
    ) {
        roleService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Role updated successfully", 200));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        roleService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Role deleted successfully", 200));
    }
}
