package com.orderservice.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderservice.dto.response.ApiResponse;
import feign.FeignException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<?>> handleAppException(AppException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(new ApiResponse<>(
                        false,
                        ex.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleOther(Exception ex) {
        ex.printStackTrace(); // log lỗi để debug

        // bọc tất cả exception ngoài ý muốn thành AppException
        AppException appEx = new AppException(500, "Lỗi hệ thống: " + ex.getMessage());
        return ResponseEntity
                .status(appEx.getStatusCode())
                .body(new ApiResponse<>(
                        false,
                        appEx.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiResponse<?>> handleFeignException(FeignException e) {
        String body = e.contentUTF8();
        String message = "Lỗi gọi service";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode json = objectMapper.readTree(body);

            String rawMessage = json.has("message") ? json.get("message").asText() : null;

            if (rawMessage != null && rawMessage.trim().startsWith("{")) {
                JsonNode nested = objectMapper.readTree(rawMessage);
                message = nested.has("message") ? nested.get("message").asText() : rawMessage;
            } else if (rawMessage != null) {
                message = rawMessage;
            }

        } catch (Exception parseEx) {
            message = "Lỗi gọi service: " + body;
        }

        return ResponseEntity
                .status(400)
                .body(new ApiResponse<>(false, message, null));
    }
}
