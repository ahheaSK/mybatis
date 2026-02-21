package com.example.mybatis.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "app.audit-log")
public class AuditLogProperties {

    private boolean enabled = true;
    private int maxBodyLength = 4096;
    private List<String> excludePaths = new ArrayList<>();

    public AuditLogProperties() {
        excludePaths.add("/actuator");
        excludePaths.add("/error");
    }

    public void setExcludePaths(List<String> excludePaths) {
        this.excludePaths = excludePaths != null ? excludePaths : new ArrayList<>();
    }
}
