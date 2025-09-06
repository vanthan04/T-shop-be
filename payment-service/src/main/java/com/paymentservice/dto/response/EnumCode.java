package com.paymentservice.dto.response;

public enum EnumCode {

    PAYMENT_SUCCESS(1600, "Payment successfully"),
    PAYMENT_FAILED(1601, "Payment failed"),
    PAYMENT_URL(1602, "Create url to payment successfully"),
    PAYMENT_REFUND(1603, "Refund");

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
