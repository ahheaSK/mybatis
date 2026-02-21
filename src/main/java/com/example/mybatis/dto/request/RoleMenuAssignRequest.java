package com.example.mybatis.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body to assign menus to a role")
public class RoleMenuAssignRequest {

    @Schema(description = "List of menu IDs to assign to the role (replaces existing)")
    private List<Long> menuIds;
}
