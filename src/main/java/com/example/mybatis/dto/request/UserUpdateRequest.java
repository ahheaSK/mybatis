package com.example.mybatis.dto.request;

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
public class UserUpdateRequest {

    @Size(min = 1, max = 100)
    private String username;

    @Size(max = 255)
    private String password;

    @Email(message = "Email must be valid")
    @Size(max = 255)
    private String email;

    private Boolean enabled;

    @Size(min = 1, message = "roleIds must have at least 1 role when provided")
    private List<Long> roleIds;
}
