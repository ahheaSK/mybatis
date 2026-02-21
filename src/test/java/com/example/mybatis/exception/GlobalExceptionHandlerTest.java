package com.example.mybatis.exception;

import com.example.mybatis.dto.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Nested
    @DisplayName("handleResourceNotFound")
    class ResourceNotFound {
        @Test
        @DisplayName("returns 404 with exception message (resourceName, id)")
        void withResourceNameAndId() {
            ResourceNotFoundException ex = new ResourceNotFoundException("User", 99L);

            ResponseEntity<ApiResponse<Void>> result = handler.handleResourceNotFound(ex);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().isStatus()).isFalse();
            assertThat(result.getBody().getCode()).isEqualTo(404);
            assertThat(result.getBody().getMessage()).contains("User").contains("99");
        }

        @Test
        @DisplayName("returns 404 with custom message (String constructor)")
        void withMessageOnly() {
            ResourceNotFoundException ex = new ResourceNotFoundException("Resource no longer available");

            ResponseEntity<ApiResponse<Void>> result = handler.handleResourceNotFound(ex);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().getCode()).isEqualTo(404);
            assertThat(result.getBody().getMessage()).isEqualTo("Resource no longer available");
        }
    }

    @Nested
    @DisplayName("handleBadRequest")
    class BadRequest {
        @Test
        @DisplayName("returns 400 with exception message")
        void success() {
            BadRequestException ex = new BadRequestException("Invalid input");

            ResponseEntity<ApiResponse<Void>> result = handler.handleBadRequest(ex);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().getCode()).isEqualTo(400);
            assertThat(result.getBody().getMessage()).isEqualTo("Invalid input");
        }
    }

    @Nested
    @DisplayName("handleBadCredentials")
    class BadCredentials {
        @Test
        @DisplayName("returns 401 with fixed message")
        void success() {
            BadCredentialsException ex = new BadCredentialsException("Bad credentials");

            ResponseEntity<ApiResponse<Void>> result = handler.handleBadCredentials(ex);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().getCode()).isEqualTo(401);
            assertThat(result.getBody().getMessage()).isEqualTo("Invalid username or password");
        }
    }

    @Nested
    @DisplayName("handleHttpMessageNotReadable")
    class HttpMessageNotReadable {
        @Test
        @DisplayName("returns 400 with fixed message")
        void success() {
            HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
            when(ex.getMessage()).thenReturn("Invalid JSON");

            ResponseEntity<ApiResponse<Void>> result = handler.handleHttpMessageNotReadable(ex);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().getCode()).isEqualTo(400);
            assertThat(result.getBody().getMessage()).isEqualTo("Invalid request body or JSON format");
        }
    }

    @Nested
    @DisplayName("handleDuplicateKey")
    class DuplicateKey {
        @Test
        @DisplayName("returns 409 with duplicate message")
        void success() {
            DuplicateKeyException ex = new DuplicateKeyException("duplicate key");

            ResponseEntity<ApiResponse<Void>> result = handler.handleDuplicateKey(ex);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().getCode()).isEqualTo(409);
            assertThat(result.getBody().getMessage()).contains("Duplicate");
        }
    }

    @Nested
    @DisplayName("handleDataIntegrity")
    class DataIntegrity {
        @Test
        @DisplayName("returns 409 when message contains duplicate key")
        void duplicateKey() {
            DataIntegrityViolationException ex = new DataIntegrityViolationException("duplicate key value");

            ResponseEntity<ApiResponse<Void>> result = handler.handleDataIntegrity(ex);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(result.getBody().getCode()).isEqualTo(409);
        }

        @Test
        @DisplayName("returns 400 for other integrity violations")
        void other() {
            DataIntegrityViolationException ex = new DataIntegrityViolationException("foreign key violation");

            ResponseEntity<ApiResponse<Void>> result = handler.handleDataIntegrity(ex);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(result.getBody().getCode()).isEqualTo(400);
            assertThat(result.getBody().getMessage()).isEqualTo("Data integrity violation");
        }
    }

    @Nested
    @DisplayName("handleException")
    class GenericException {
        @Test
        @DisplayName("returns 409 when cause message contains duplicate key")
        void duplicateKeyInCause() {
            Exception ex = new Exception("wrapper", new RuntimeException("unique constraint violated"));

            ResponseEntity<ApiResponse<Void>> result = handler.handleException(ex);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(result.getBody().getCode()).isEqualTo(409);
        }

        @Test
        @DisplayName("returns 500 with exception message")
        void generic() {
            Exception ex = new RuntimeException("Something broke");

            ResponseEntity<ApiResponse<Void>> result = handler.handleException(ex);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().getCode()).isEqualTo(500);
            assertThat(result.getBody().getMessage()).isEqualTo("Something broke");
        }

        @Test
        @DisplayName("returns 500 with default message when message is null")
        void nullMessage() {
            Exception ex = new RuntimeException();

            ResponseEntity<ApiResponse<Void>> result = handler.handleException(ex);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(result.getBody().getCode()).isEqualTo(500);
            assertThat(result.getBody().getMessage()).isEqualTo("Internal server error");
        }
    }

    @Nested
    @DisplayName("handleValidation")
    class Validation {
        @Test
        @DisplayName("returns 400 with field errors joined")
        void success() {
            BindingResult bindingResult = mock(BindingResult.class);
            when(bindingResult.getFieldErrors()).thenReturn(List.of(
                    new FieldError("req", "username", "must not be blank"),
                    new FieldError("req", "email", "must be valid")
            ));
            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

            ResponseEntity<ApiResponse<Void>> result = handler.handleValidation(ex);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().getCode()).isEqualTo(400);
            assertThat(result.getBody().getMessage()).contains("username").contains("must not be blank");
            assertThat(result.getBody().getMessage()).contains("email").contains("must be valid");
        }
    }
}
