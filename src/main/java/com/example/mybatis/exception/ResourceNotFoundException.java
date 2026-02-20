package com.example.mybatis.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final Long id;
    private final HttpStatus status;

    public ResourceNotFoundException(String message) {
        super(message);
        this.id = null;
        this.status = HttpStatus.NOT_FOUND;
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super("%s With id = %d not found".formatted(resourceName, id));
        this.id = id;
        this.status = HttpStatus.NOT_FOUND;
    }
}
