package com.paymentservice.dto.request;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
public class PaymentCreatedRequest {
    private UUID userId;
    private String fullName;
    private String email;
    private UUID orderId;
    private BigDecimal totalAmount;
    private List<OrderItemRequest> items;
}
