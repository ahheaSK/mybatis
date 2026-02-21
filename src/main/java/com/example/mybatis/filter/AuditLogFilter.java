package com.example.mybatis.filter;

import com.example.mybatis.audit.CurrentUserService;
import com.example.mybatis.entity.AuditLog;
import com.example.mybatis.mapper.AuditLogMapper;
import com.example.mybatis.properties.AuditLogProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Order(-500)
public class AuditLogFilter extends OncePerRequestFilter {

    private final AuditLogProperties properties;
    private final AuditLogMapper auditLogMapper;
    private final CurrentUserService currentUserService;

    public AuditLogFilter(AuditLogProperties properties, AuditLogMapper auditLogMapper,
                          CurrentUserService currentUserService) {
        this.properties = properties;
        this.auditLogMapper = auditLogMapper;
        this.currentUserService = currentUserService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!properties.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }
        String path = request.getRequestURI();
        if (isExcluded(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        int cacheLimit = Math.max(0, properties.getMaxBodyLength());
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request, cacheLimit);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            saveAuditLog(wrappedRequest, wrappedResponse);
            wrappedResponse.copyBodyToResponse();
        }
    }

    private boolean isExcluded(String path) {
        return properties.getExcludePaths().stream()
                .anyMatch(pattern -> path.equals(pattern) || path.startsWith(pattern + "/"));
    }

    private void saveAuditLog(ContentCachingRequestWrapper request,
                              ContentCachingResponseWrapper response) {
        try {
            String method = request.getMethod();
            String httpUrl = request.getRequestURI();
            if (request.getQueryString() != null) {
                httpUrl = httpUrl + "?" + request.getQueryString();
            }
            String requestData = getTruncatedBody(request.getContentAsByteArray());
            String responseData = getTruncatedBody(response.getContentAsByteArray());
            String ousername = currentUserService.getCurrentUsername();

            AuditLog log = new AuditLog();
            log.setMethod(method);
            log.setHttpUrl(truncateUrl(httpUrl));
            log.setRequestData(requestData);
            log.setResponseData(responseData);
            log.setOusername(ousername);
            auditLogMapper.insert(log);
        } catch (Exception e) {
            // Log but do not fail the request
            if (logger.isWarnEnabled()) {
                logger.warn("Failed to save audit log: " + e.getMessage());
            }
        }
    }

    private String getTruncatedBody(byte[] content) {
        if (content == null || content.length == 0) {
            return null;
        }
        String body = new String(content, StandardCharsets.UTF_8);
        int max = Math.max(0, properties.getMaxBodyLength());
        if (max > 0 && body.length() > max) {
            return body.substring(0, max) + "...[truncated]";
        }
        return body;
    }

    private String truncateUrl(String url) {
        int max = 2000;
        if (url != null && url.length() > max) {
            return url.substring(0, max) + "...";
        }
        return url;
    }

}
