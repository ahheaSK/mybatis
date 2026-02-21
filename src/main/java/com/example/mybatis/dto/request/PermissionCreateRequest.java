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
@Schema(description = "Request body to create a new permission")
public class PermissionCreateRequest {

    @NotBlank(message = "Code must not be blank")
    @Size(min = 1, max = 100)
    @Schema(description = "Unique permission code", example = "USER_READ", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @NotBlank(message = "Name must not be blank")
    @Size(min = 1, max = 200)
    @Schema(description = "Display name", example = "Read users", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Size(max = 500)
    @Schema(description = "Optional description", example = "View user list and details")
    private String description;
}
