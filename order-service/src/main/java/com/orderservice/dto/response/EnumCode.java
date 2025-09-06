package com.orderservice.dto.response;

public enum EnumCode {

    GET_ALL_ORDERS_SUCCESS(1200,"Successfully retrieved all orders"),
    CREATE_ORDER_SUCCESS(1201,"Order created successfully"),
    GET_USER_ORDERS_SUCCESS(1202,"Successfully retrieved user's orders"),
    GET_ORDER_DETAIL_SUCCESS(1203,"Successfully retrieved order details"),
    CANCEL_ORDER_SUCCESS(1204,"Order cancelled successfully");

    private final int statusCode;
    private final String message;

    EnumCode(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
