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
@ConfigurationProperties(prefix = "app.rate-limit")
public class RateLimitProperties {

    /** Whether rate limiting is enabled. */
    private boolean enabled = true;

    /** Maximum requests per minute per client (IP). */
    private int requestsPerMinute = 60;

    /** Path patterns to exclude from rate limiting (e.g. /actuator/health, /error). */
    private List<String> excludePaths = new ArrayList<>(List.of("/actuator/health", "/error"));

    public void setExcludePaths(List<String> excludePaths) {
        this.excludePaths = excludePaths != null ? excludePaths : new ArrayList<>();
    }
}
