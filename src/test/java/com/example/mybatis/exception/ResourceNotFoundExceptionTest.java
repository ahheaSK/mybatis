package com.example.mybatis.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceNotFoundExceptionTest {

    @Test
    @DisplayName("ResourceNotFoundException(String) sets message, id null, status NOT_FOUND")
    void constructorWithMessage() {
        String message = "Custom not found message";
        ResourceNotFoundException ex = new ResourceNotFoundException(message);

        assertThat(ex.getMessage()).isEqualTo(message);
        assertThat(ex.getId()).isNull();
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("ResourceNotFoundException(String, Long) sets formatted message, id, status NOT_FOUND")
    void constructorWithResourceNameAndId() {
        ResourceNotFoundException ex = new ResourceNotFoundException("User", 99L);

        assertThat(ex.getMessage()).isEqualTo("User With id = 99 not found");
        assertThat(ex.getId()).isEqualTo(99L);
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("ResourceNotFoundException(String, Long) with null id")
    void constructorWithResourceNameAndNullId() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Permission", null);

        assertThat(ex.getMessage()).isEqualTo("Permission With id = null not found");
        assertThat(ex.getId()).isNull();
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
