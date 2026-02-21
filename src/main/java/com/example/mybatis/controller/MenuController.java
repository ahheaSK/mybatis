package com.example.mybatis.controller;

import com.example.mybatis.dto.request.MenuCreateRequest;
import com.example.mybatis.dto.request.MenuUpdateRequest;
import com.example.mybatis.dto.response.ApiResponse;
import com.example.mybatis.dto.response.MenuResponse;
import com.example.mybatis.dto.response.PageResponse;
import com.example.mybatis.dto.response.PaginationDto;
import com.example.mybatis.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menus")
@Tag(name = "Menus", description = "CRUD and list menus")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @Operation(summary = "List menus", description = "Returns paginated menus, optionally filtered by name, path, parentId")
    @GetMapping
    public ResponseEntity<ApiResponse<List<MenuResponse>>> list(
            @Parameter(description = "Zero-based page index") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Filter by name") @RequestParam(required = false) String name,
            @Parameter(description = "Filter by path") @RequestParam(required = false) String path,
            @Parameter(description = "Filter by parent menu ID") @RequestParam(required = false) Long parentId
    ) {
        PageResponse<MenuResponse> pr = menuService.findAll(page, size, name, path, parentId);
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

    @Operation(summary = "Get menu by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuResponse>> getOne(
            @Parameter(description = "Menu ID") @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(menuService.findById(id), "Get menu successfully", 200));
    }

    @Operation(summary = "Create menu")
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> create(@Valid @RequestBody MenuCreateRequest request) {
        menuService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(null, "Menu created successfully", 201));
    }

    @Operation(summary = "Update menu", description = "Updates only non-null fields")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> update(
            @Parameter(description = "Menu ID") @PathVariable Long id,
            @Valid @RequestBody MenuUpdateRequest request
    ) {
        menuService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Menu updated successfully", 200));
    }

    @Operation(summary = "Delete menu")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "Menu ID") @PathVariable Long id) {
        menuService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Menu deleted successfully", 200));
    }
}
