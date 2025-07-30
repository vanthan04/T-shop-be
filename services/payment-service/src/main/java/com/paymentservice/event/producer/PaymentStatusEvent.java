package com.paymentservice.event.producer;

import com.paymentservice.dto.request.OrderItemRequest;
import com.paymentservice.dto.request.PaymentCreatedRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentStatusEvent {
    private UUID userId;
    private String fullName;
    private String email;
    private UUID orderId;
    private BigDecimal totalAmount;
    private List<OrderItemRequest> items;

    public PaymentStatusEvent(PaymentCreatedRequest request){
        this.userId = request.getUserId();
        this.fullName = request.getFullName();
        this.email = request.getEmail();
        this.orderId = request.getOrderId();
        this.totalAmount = request.getTotalAmount();
        this.items = request.getItems();
    }
}
