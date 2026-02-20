package com.example.mybatis.controller;

import com.example.mybatis.dto.request.UserCreateRequest;
import com.example.mybatis.dto.request.UserUpdateRequest;
import com.example.mybatis.dto.response.ApiResponse;
import com.example.mybatis.dto.response.PageResponse;
import com.example.mybatis.dto.response.PaginationDto;
import com.example.mybatis.dto.response.UserResponse;
import com.example.mybatis.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email
    ) {
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

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.findById(id), "Get user successfully", 200));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> create(@Valid @RequestBody UserCreateRequest request) {
        userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(null, "User created successfully", 201));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> update(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        userService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(null, "User updated successfully", 200));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully", 200));
    }
}
