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
public class PermissionUpdateRequest {

    @Size(min = 1, max = 100)
    private String code;

    @Size(min = 1, max = 200)
    private String name;

    @Size(max = 500)
    private String description;
}
