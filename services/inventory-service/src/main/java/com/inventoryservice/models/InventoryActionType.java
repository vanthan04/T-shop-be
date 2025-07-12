package com.inventoryservice.models;

public enum InventoryActionType {
    INIT,              // Khởi tạo inventory khi tạo product
    IMPORT,            // Nhập kho
    EXPORT,            // Xuất kho
    RESERVE,           // Đặt trước số lượng
    CONFIRM,           // Xác nhận đơn hàng (trừ số lượng)
    CANCEL_RESERVE,    // Huỷ giữ hàng
    ADJUST             // Điều chỉnh thủ công
}
