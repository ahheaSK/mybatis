package com.example.mybatis.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleUpdateRequest {

    @Size(min = 1, max = 50)
    private String code;

    @Size(min = 1, max = 100)
    private String name;

    @Size(max = 255)
    private String description;
}
