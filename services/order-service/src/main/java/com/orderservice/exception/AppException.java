package com.orderservice.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    private final int statusCode;

    public AppException(int statusCode, String message) {
        super(message);

        if (statusCode < 100 || statusCode > 599) {
            throw new IllegalArgumentException("Status code không hợp lệ: " + statusCode);
        }
        this.statusCode = statusCode;
    }
}
