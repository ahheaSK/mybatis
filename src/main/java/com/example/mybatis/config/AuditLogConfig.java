package com.example.mybatis.config;

import com.example.mybatis.audit.CurrentUserService;
import com.example.mybatis.filter.AuditLogFilter;
import com.example.mybatis.properties.AuditLogProperties;
import com.example.mybatis.mapper.AuditLogMapper;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class AuditLogConfig {

    @Bean
    public FilterRegistrationBean<AuditLogFilter> auditLogFilterRegistration(
            AuditLogProperties properties,
            AuditLogMapper auditLogMapper,
            CurrentUserService currentUserService) {
        FilterRegistrationBean<AuditLogFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new AuditLogFilter(properties, auditLogMapper, currentUserService));
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.LOWEST_PRECEDENCE - 100);
        registration.setName("auditLogFilter");
        return registration;
    }
}
