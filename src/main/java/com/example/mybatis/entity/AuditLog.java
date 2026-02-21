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
public class AuditLog {

    private Long id;
    private String method;
    private String httpUrl;
    private String requestData;
    private String responseData;
    private String ousername;
    private Instant createdAt;
}
