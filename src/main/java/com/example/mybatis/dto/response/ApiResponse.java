package com.example.mybatis.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private T data;
    private PaginationDto pagination;
    private boolean status;
    private int code;
    private String message;
    private String timestamp;
    private UUID trackingId;

    public static <T> ApiResponse<T> successWithPage(
            T data,
            PaginationDto pagination,
            String message,
            int code
    ) {
        return ApiResponse.<T>builder()
                .data(data)
                .pagination(pagination)
                .status(true)
                .code(code)
                .message(message)
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .trackingId(UUID.randomUUID())
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String message, int code) {
        return ApiResponse.<T>builder()
                .data(data)
                .status(true)
                .code(code)
                .message(message)
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .trackingId(UUID.randomUUID())
                .build();
    }

    public static <T> ApiResponse<T> error(String message, int code) {
        return ApiResponse.<T>builder()
                .status(false)
                .code(code)
                .message(message)
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .trackingId(UUID.randomUUID())
                .build();
    }
}
