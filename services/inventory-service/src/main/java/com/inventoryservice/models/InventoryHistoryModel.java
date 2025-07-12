package com.inventoryservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory_histories")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryHistoryModel {

    @Id
    @Column(name = "history_id", nullable = false, updatable = false)
    private UUID historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private InventoryModel inventory;

    @Column(name = "change_quantity", nullable = false)
    private int changeQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", length = 50, nullable = false)
    private InventoryActionType actionType;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "action_date", nullable = false)
    private LocalDateTime actionDate;

    @Column(name = "note", length = 255)
    private String note;

}
