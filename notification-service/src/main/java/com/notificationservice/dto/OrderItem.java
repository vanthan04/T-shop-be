package com.notificationservice.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class OrderItem {
    private UUID productId;
    private String productName;
    private int quantity;
    private BigDecimal price;
}
