package com.example.mybatis.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;
    private String username;
    private String password;
    private String email;
    private Boolean enabled;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private String ousername;
}
