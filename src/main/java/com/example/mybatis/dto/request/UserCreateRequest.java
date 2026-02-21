package com.example.mybatis.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body to create a new user")
public class UserCreateRequest {

    @NotBlank(message = "Username must not be blank")
    @Size(min = 1, max = 100)
    @Schema(description = "Login username", example = "jane", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 1, max = 255)
    @Schema(description = "Password (stored hashed)", example = "secret123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Email(message = "Email must be valid")
    @Size(max = 255)
    @Schema(description = "Email address", example = "jane@example.com")
    private String email;

    @Schema(description = "Account enabled", example = "true")
    private Boolean enabled = true;

    @NotNull(message = "At least one role is required")
    @Size(min = 1, message = "roleIds must have at least 1 role")
    @Schema(description = "Role IDs to assign (e.g. 1 for ADMIN)", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> roleIds;
}
