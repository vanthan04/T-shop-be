package com.paymentservice.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "returns")
@Data
public class ReturnModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID returnId;

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private UUID userId;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "total_amount")
    private Double total_amount;

    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;

}
