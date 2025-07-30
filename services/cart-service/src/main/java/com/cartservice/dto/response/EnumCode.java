package com.cartservice.dto.response;

public enum EnumCode {

    CART_ITEM_ADDED(1800, "Product added to cart successfully"),
    CART_ITEMS_FETCHED(1801, "Cart items fetched successfully"),
    CART_ITEM_UPDATED(1802, "Cart item quantity updated successfully"),
    CART_ITEM_REMOVED(1803, "Product removed from cart successfully"),
    CART_ITEM_COUNTED(1804, "Total number of items in cart retrieved successfully");


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
