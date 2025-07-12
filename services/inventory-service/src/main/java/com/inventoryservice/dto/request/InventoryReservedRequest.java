package com.inventoryservice.dto.request;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class InventoryReservedRequest {
    private UUID orderId;
    private List<OrderItem> items;
}
