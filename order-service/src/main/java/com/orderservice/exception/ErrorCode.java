package com.orderservice.exception;

public enum ErrorCode {

    ORDER_NOT_FOUND(1250, "Order not found"),
    ORDER_OUT_OF_STOCK(1251, "Insufficient stock for one or more products"),
    ORDER_CANNOT_CANCEL_IF_NOT_PENDING(1252, "Only orders in PENDING status can be cancelled"),
    ORDER_CANNOT_RETURN_TO_INVENTORY(1253, "Failed to return items to inventory"),


    ORDER_INTERNAL_SERVER_ERROR(1299, "Internal server error!");
    private final int statusCode;
    private final String message;

    ErrorCode(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage(){
        return message;
    }
}
