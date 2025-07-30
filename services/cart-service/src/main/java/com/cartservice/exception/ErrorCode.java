package com.cartservice.exception;

public enum ErrorCode {

    CART_PRODUCT_NOT_FOUND(1752,"Product not found in cart"),
    CART_INVALID_ITEM_QUANTITY(1754, "Invalid item quantity"),
    CART_INTERNAL_SERVER_ERROR(1799,"Internal server error");

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
