package com.inventoryservice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventories")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryModel {

    @Id
    @Column(name = "inventory_id")
    private UUID inventoryId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "reserved_quantity", nullable = false)
    private int reservedQuantity;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    public void createNewInventory(UUID productId, int changeQuantity){
        this.inventoryId = UUID.randomUUID();
        this.productId = productId;
        this.quantity += changeQuantity;
        this.reservedQuantity = 0;
        this.updatedAt = LocalDateTime.now();
    }

    public void importQuantity(int changeQuantity){
        this.quantity += changeQuantity;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean canReserve(UUID productId, int quantityReserve) {
        return this.productId.equals(productId)
                && quantityReserve > 0
                && this.quantity >= quantityReserve;
    }

    public void reserveInventory(int reservedQuantity){
        this.quantity -= reservedQuantity;
        this.reservedQuantity += reservedQuantity;
        this.updatedAt = LocalDateTime.now();
    }
    public boolean canConfirm(UUID orderId, UUID productId, int quantityConfirm){
        return this.productId.equals(productId)
                && quantityConfirm > 0
                && this.reservedQuantity >= quantityConfirm;
    }

    public void confirmInventory(int confirmQuantity){
        this.reservedQuantity -= confirmQuantity;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean canCancel(UUID orderId, UUID productId, int quantityCancel){
        return this.productId.equals(productId)
                && quantityCancel > 0
                && this.reservedQuantity >= quantityCancel;
    }

    public void cancelInventory(int cancelQuantity){
        this.quantity += cancelQuantity;
        this.reservedQuantity -= cancelQuantity;
        this.updatedAt = LocalDateTime.now();
    }
}
