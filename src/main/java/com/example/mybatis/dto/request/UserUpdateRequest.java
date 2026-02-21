package com.example.mybatis.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
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
@Schema(description = "Request body to update an existing user; only non-null fields are updated")
public class UserUpdateRequest {

    @Size(min = 1, max = 100)
    @Schema(description = "Username", example = "jane_updated")
    private String username;

    @Size(max = 255)
    @Schema(description = "New password (leave null to keep current)")
    private String password;

    @Email(message = "Email must be valid")
    @Size(max = 255)
    @Schema(description = "Email address", example = "jane@example.com")
    private String email;

    @Schema(description = "Account enabled", example = "true")
    private Boolean enabled;

    @Size(min = 1, message = "roleIds must have at least 1 role when provided")
    @Schema(description = "Role IDs to assign (replaces existing)")
    private List<Long> roleIds;
}
