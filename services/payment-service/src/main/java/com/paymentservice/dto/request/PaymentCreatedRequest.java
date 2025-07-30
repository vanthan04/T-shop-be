package com.paymentservice.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class PaymentCreatedRequest {
    private UUID userId;
    private String fullName;
    private String email;
    private UUID orderId;
    private BigDecimal totalAmount;
    private List<OrderItemRequest> items;
}
