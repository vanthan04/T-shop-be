package com.cartservice.exception;

public class AppException extends RuntimeException {
    private final ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorCode.getMessage();
    }

    public ErrorCode getErrorCodeEnum() {
        return errorCode;
    }
}
