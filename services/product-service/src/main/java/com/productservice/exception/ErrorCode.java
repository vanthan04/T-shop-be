package com.productservice.exception;

public enum ErrorCode {

    PRODUCT_TYPE_NOT_FOUND(1450, "Product type not found"),
    PRODUCT_TYPE_ALREADY_EXISTS(1451, "Product type name already exists"),
    PRODUCT_TYPE_NOT_EMPTY(1452, "Product type is not empty"),

    PRODUCT_IMAGE_UPLOAD_FAILED(1453, "Failed to upload product image"),
    PRODUCT_IMAGE_DELETE_FAILED(1454, "Failed to delete product image"),
    PRODUCT_CREATION_FAILED(1455, "Failed to create product"),
    PRODUCT_NOT_FOUND(1456, "Product not found"),
    PRODUCT_UPDATE_FAILED(1457, "Failed to update product"),
    PRODUCT_NAME_ALREADY_EXISTS(1458, "Product name already exists"),
    PRODUCT_IMAGE_PROCESSING_ERROR(1452, "Error while processing product image"),


    PRODUCT_INTERNAL_SERVER_ERROR(1499, "Internal server error");

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
