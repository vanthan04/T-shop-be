package com.paymentservice.exception;

import com.paymentservice.dto.response.ApiResponse;
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
}
