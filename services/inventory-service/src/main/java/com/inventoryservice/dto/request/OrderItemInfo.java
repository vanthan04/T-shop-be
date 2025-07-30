package com.inventoryservice.dto.request;

import com.inventoryservice.models.Inventory;
import com.inventoryservice.models.InventoryActionType;
import lombok.Getter;

@Getter
public class OrderItemInfo {
    private Inventory inventory;
    private int changeQuantity;
    private InventoryActionType actionType;
}
