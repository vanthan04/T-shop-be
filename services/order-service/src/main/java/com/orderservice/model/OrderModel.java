package com.orderservice.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.orderservice.dto.request.OrderItemRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderModel {

    @Id
    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "address_shipping", nullable = false)
    private String addressShip;

    @Column(name = "phone")
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private OrderStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderLineModel> orderLines = new ArrayList<>();


    public void createOrder(
            UUID orderId,
            UUID userId,
            String email,
            String addressShip,
            String phone,
            OrderStatus orderStatus,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            List<OrderItemRequest> orderItems
    ){
        this.orderId = orderId;
        this.userId = userId;
        this.email = email;
        this.addressShip = addressShip;
        this.phone = phone;
        this.status = orderStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        for (OrderItemRequest orderItem : orderItems){
            OrderLineModel orderLineModel = new OrderLineModel(this, orderItem.getProductId(), orderItem.getQuantity(), orderItem.getPrice());
            this.orderLines.add(orderLineModel);
        }
    }

    public void updateOrder(
            OrderStatus orderStatus,
            LocalDateTime updatedAt
    ){
        this.status = orderStatus;
        this.updatedAt = updatedAt;
    }
}
