package com.inventoryservice.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class InventoryManualUpdatedRequest {
    private UUID productId;
    private int quantity;
    private String reason;
}
