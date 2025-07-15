package com.inventoryservice.exception;

public enum ErrorCode {

    INVENTORY_ORDER_NOT_FOUND(1350,"Order not found"),
    INVENTORY_ORDER_EXISTED(1351,"Order already exists"),
    INVENTORY_PRODUCT_NOT_FOUND(1352,"Product not found"),
    INVENTORY_OUT_OF_STOCK(1353,"Not enough stock for product"),
    INSUFFICIENT_INVENTORY(1354, "Insufficient inventory for product"),
    INVENTORY_INTERNAL_SERVER_ERROR(1304,"Internal server error");

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
