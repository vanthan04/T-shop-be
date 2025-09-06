package com.orderservice.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class OrderItemRequest {
    private UUID productId;
    private String productName;
    private int quantity;
    private BigDecimal price;
}
