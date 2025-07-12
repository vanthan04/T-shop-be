package com.paymentservice.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PaymentCreatedRequest {
    private UUID userId;
    private UUID orderId;
    private BigDecimal totalAmount;
}
