package com.paymentservice.model;

import com.paymentservice.models.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Data
public class PaymentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID paymentId;

    @Column(nullable = false, unique = true)
    private UUID orderId;

    @Column(nullable = false)
    private double amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String paymentUrl;

    private String transactionId;

    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private LocalDateTime refundedAt;
}
