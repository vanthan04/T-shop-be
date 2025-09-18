package com.inventoryservice.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory_histories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    @JsonBackReference
    private Inventory inventory;

    private int changeQuantity;

    @Enumerated(EnumType.STRING)
    private InventoryActionType actionType;

    private UUID orderId;

    private LocalDateTime actionDate;

    private String note;

    public boolean isCancelableReserve(UUID orderId) {
        return this.orderId.equals(orderId) && actionType == InventoryActionType.RESERVE;
    }

    public boolean isConfirmableReserve(UUID orderId) {
        return this.orderId.equals(orderId) && actionType == InventoryActionType.RESERVE;
    }

    public void reserveInventoryHistory(Inventory inventory, UUID orderId, int changeQuantity){
        this.historyId = UUID.randomUUID();
        this.inventory = inventory;
        this.changeQuantity = changeQuantity;
        this.actionType = InventoryActionType.RESERVE;
        this.orderId = orderId;
        this.actionDate = LocalDateTime.now();
        this.note = "Reserve for productId: " + inventory.getProductId();
    }
    public void confirmInventoryHistory(Inventory inventory, UUID orderId, int changeQuantity){
        this.historyId = UUID.randomUUID();
        this.inventory = inventory;
        this.changeQuantity = changeQuantity;
        this.actionType = InventoryActionType.CONFIRM;
        this.orderId = orderId;
        this.actionDate = LocalDateTime.now();
        this.note = "Confirm for productId: " + inventory.getProductId();

    }

    public void cancelInventoryHistory(Inventory inventory, UUID orderId, int changeQuantity){
        this.historyId = UUID.randomUUID();
        this.inventory = inventory;
        this.changeQuantity = changeQuantity;
        this.actionType = InventoryActionType.CANCEL_RESERVE;
        this.orderId = orderId;
        this.actionDate = LocalDateTime.now();
        this.note = "Cancel reserve for productId: " + inventory.getProductId();
    }

    public void initInventoryHistory(Inventory inventory){
        this.inventory = inventory;
        this.changeQuantity = 0;
        this.actionType = InventoryActionType.INIT;
        this.orderId = null;
        this.actionDate = LocalDateTime.now();
        this.note = "init inventory";
    }
    public void createOrImportInventoryHistory(Inventory inventory, int quantity){
        this.inventory = inventory;
        this.changeQuantity = quantity;
        this.actionType = InventoryActionType.IMPORT;
        this.orderId = null;
        this.actionDate = LocalDateTime.now();
        this.note = "Import quantity product for inventory";
    }

}
