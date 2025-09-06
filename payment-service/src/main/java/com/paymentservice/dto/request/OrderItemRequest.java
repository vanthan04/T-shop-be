package com.paymentservice.dto.request;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class OrderItemRequest {
    private UUID productId;
    private String productName;
    private int quantity;
    private BigDecimal price;
}
