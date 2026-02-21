package com.example.mybatis.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body to update an existing role; only non-null fields are updated")
public class RoleUpdateRequest {

    @Size(min = 1, max = 50)
    @Schema(description = "Role code", example = "MANAGER")
    private String code;

    @Size(min = 1, max = 100)
    @Schema(description = "Display name", example = "Manager")
    private String name;

    @Size(max = 255)
    @Schema(description = "Optional description", example = "Can manage users")
    private String description;
}
