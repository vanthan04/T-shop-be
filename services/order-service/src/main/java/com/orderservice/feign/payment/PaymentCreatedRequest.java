package com.orderservice.feign.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCreatedRequest {
    private UUID userId;
    private UUID orderId;
    private BigDecimal totalAmount;
}
