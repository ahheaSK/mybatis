package com.example.mybatis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private List<RoleResponse> roles;
    private List<PermissionResponse> permissions;

    public LoginResponse(String token, Long id, String username,
                         List<RoleResponse> roles, List<PermissionResponse> permissions) {
        this.token = token;
        this.type = "Bearer";
        this.id = id;
        this.username = username;
        this.roles = roles;
        this.permissions = permissions;
    }
}
