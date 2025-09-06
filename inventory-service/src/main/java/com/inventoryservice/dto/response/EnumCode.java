package com.inventoryservice.dto.response;

public enum EnumCode {

    INVENTORY_CREATED(1300, "Inventory created or imported successfully"),
    INVENTORY_FETCHED(1301,"Inventory fetched successfully"),
    INVENTORY_UPDATED(1302,"Inventory manually updated successfully"),
    INVENTORY_DELETED(1303,"Inventory deleted successfully"),
    INVENTORY_LOG_FETCHED(1304,"Inventory history fetched successfully"),
    INVENTORY_RESERVED(1305,"Inventory reserved successfully"),
    INVENTORY_CONFIRMED(1306,"Inventory reservation confirmed successfully"),
    INVENTORY_CANCELLED(1307,"Inventory reservation cancelled successfully");

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
