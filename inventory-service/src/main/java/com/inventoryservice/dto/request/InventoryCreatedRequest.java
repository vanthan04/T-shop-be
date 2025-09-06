package com.inventoryservice.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class InventoryCreatedRequest {
    private UUID productId;
    private int quantity;
}
