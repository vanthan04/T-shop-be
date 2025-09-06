package com.productservice.dto.response;

public enum EnumCode {

    // --- Product Type Codes ---
    PRODUCT_TYPE_FETCHED(1400, "Product types fetched successfully"),
    PRODUCT_TYPE_CREATED(1401, "Product type created successfully"),
    PRODUCT_TYPE_UPDATED(1402, "Product type updated successfully"),
    PRODUCT_TYPE_DELETED(1403, "Product type deleted successfully"),

    // --- Product Codes ---
    PRODUCT_CREATED(1404, "Product created successfully"),
    PRODUCT_UPDATED(1405, "Product updated successfully"),
    PRODUCT_DELETED(1406, "Product deleted successfully"),
    PRODUCT_FETCHED(1407, "Product fetched successfully"),
    PRODUCT_LIST_FETCHED(1408, "Product list fetched successfully"),
    PRODUCT_IMAGE_ADDED(1409, "Product image(s) added successfully"),
    PRODUCT_IMAGE_DELETED(1410, "Product image(s) deleted successfully");

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
