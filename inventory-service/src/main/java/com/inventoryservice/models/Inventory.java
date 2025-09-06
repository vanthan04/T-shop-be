package com.inventoryservice.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.inventoryservice.exception.AppException;
import com.inventoryservice.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "inventories")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Inventory {

    @Id
    private UUID inventoryId;
    private UUID productId;

    private int quantity;
    private int reservedQuantity;

    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<InventoryHistory> histories = new ArrayList<>();

    private LocalDateTime updatedAt;

    public void initInventory(UUID productId){
        this.inventoryId = UUID.randomUUID();
        this.productId = productId;
        this.quantity = 0;
        this.reservedQuantity = 0;

        this.histories = new ArrayList<>();
        InventoryHistory inventoryHistory = new InventoryHistory();
        inventoryHistory.initInventoryHistory(this);
        this.histories.add(inventoryHistory);

        this.updatedAt = LocalDateTime.now();
    }


    public void createOrImportInventory(UUID productId, int quantity){
        this.productId = productId;
        if (quantity <= 0){
            throw new AppException(ErrorCode.INVENTORY_OUT_OF_STOCK);
        }
        this.quantity += quantity;
        InventoryHistory inventoryHistory = new InventoryHistory();
        inventoryHistory.createOrImportInventoryHistory(this, quantity);

        this.histories.add(inventoryHistory);
    }

    public boolean canReserve(UUID productId, int quantityReserve) {
        return this.productId.equals(productId)
                && quantityReserve > 0
                && this.quantity >= quantityReserve;
    }

    public boolean canConfirm(UUID orderId, UUID productId, int quantityConfirm){
        boolean checkInventory = this.productId.equals(productId)
                && quantityConfirm > 0
                && this.reservedQuantity >= quantityConfirm;

        if(!checkInventory) return false;
        for (InventoryHistory history : histories){
            if (!history.isConfirmableReserve(orderId)){
                return false;
            }
        }
        return true;
    }

    public boolean canCancel(UUID orderId, UUID productId, int quantityCancel){
        boolean checkInventory = this.productId.equals(productId)
                && quantityCancel > 0
                && this.reservedQuantity >= quantityCancel;

        if(!checkInventory) return false;
        for (InventoryHistory history : histories){
            if (!history.isCancelableReserve(orderId)){
                return false;
            }
        }
        return true;
    }

    public void reserveInventory(UUID orderId, int quantityReserve){
        if (!canReserve(this.productId, quantityReserve)){
            throw new RuntimeException("Khong du hang");
        }
        this.reservedQuantity += quantityReserve;
        this.quantity -= quantityReserve;

        this.updatedAt = LocalDateTime.now();

        InventoryHistory inventoryHistory = new InventoryHistory();
        inventoryHistory.reserveInventoryHistory(this, orderId, quantityReserve);

        this.histories.add(inventoryHistory);

    }

    public void confirmInventory(UUID orderId, int quantityConfirm){
        if (!canConfirm(orderId, this.productId, quantityConfirm)){
            throw new RuntimeException("Qua so hang da giu de xac nhan");
        }
        this.reservedQuantity -= quantityConfirm;
        this.updatedAt = LocalDateTime.now();

        InventoryHistory inventoryHistory = new InventoryHistory();
        inventoryHistory.confirmInventoryHistory(this, orderId, quantity);

        this.histories.add(inventoryHistory);
    }

    public void cancelInventory(UUID orderId, int quantityCancel){
        if (!canCancel(orderId, this.productId, quantityCancel)){
            throw new RuntimeException("Qua so hang da giu de huy");
        }
        this.reservedQuantity -= quantityCancel;
        this.quantity += quantityCancel;

        InventoryHistory inventoryHistory = new InventoryHistory();
        inventoryHistory.cancelInventoryHistory(this, orderId, quantityCancel);

        this.histories.add(inventoryHistory);
    }


}
