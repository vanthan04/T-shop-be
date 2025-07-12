package com.inventoryservice.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class OrderItem {
    private UUID productId;
    private int quantity;
    private BigDecimal price;
}
