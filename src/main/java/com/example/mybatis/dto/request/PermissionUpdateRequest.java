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
@Schema(description = "Request body to update an existing permission; only non-null fields are updated")
public class PermissionUpdateRequest {

    @Size(min = 1, max = 100)
    @Schema(description = "Permission code", example = "USER_WRITE")
    private String code;

    @Size(min = 1, max = 200)
    @Schema(description = "Display name", example = "Write users")
    private String name;

    @Size(max = 500)
    @Schema(description = "Optional description", example = "Create and update users")
    private String description;
}
