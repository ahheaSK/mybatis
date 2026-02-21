package com.example.mybatis.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body to create a new role")
public class RoleCreateRequest {

    @NotBlank(message = "Code must not be blank")
    @Size(min = 1, max = 50)
    @Schema(description = "Unique role code", example = "MANAGER", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @NotBlank(message = "Name must not be blank")
    @Size(min = 1, max = 100)
    @Schema(description = "Display name", example = "Manager", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Size(max = 255)
    @Schema(description = "Optional description", example = "Can manage users and roles")
    private String description;
}
