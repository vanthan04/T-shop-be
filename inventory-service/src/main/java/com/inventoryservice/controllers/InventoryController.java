package com.inventoryservice.controllers;

import com.inventoryservice.dto.request.InventoryCreatedRequest;
import com.inventoryservice.dto.request.InventoryManualUpdatedRequest;
import com.inventoryservice.dto.request.InventoryReservedRequest;
import com.inventoryservice.dto.response.ApiResponse;
import com.inventoryservice.dto.response.EnumCode;
import com.inventoryservice.models.Inventory;
import com.inventoryservice.models.InventoryHistory;
import com.inventoryservice.services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ApiResponse<Inventory> createOrImport(@RequestBody InventoryCreatedRequest request) {
        Inventory inventory = inventoryService.createNewInventory(request);
        return new ApiResponse<>(EnumCode.INVENTORY_CREATED, inventory);
    }

    @GetMapping("/{productId}")
    public ApiResponse<Inventory> getByProductId(@PathVariable UUID productId) {
        Inventory inventory = inventoryService.getInventoryByProductId(productId);
        return new ApiResponse<>(EnumCode.INVENTORY_FETCHED, inventory);
    }

    @PutMapping("/manual-update")
    public ApiResponse<Inventory> manualUpdate(@RequestBody InventoryManualUpdatedRequest request) {
        Inventory updated = inventoryService.updateQuantity(
                request.getProductId(),
                request.getQuantity(),
                request.getReason()
        );
        return new ApiResponse<>(EnumCode.INVENTORY_UPDATED, updated);
    }

    @DeleteMapping("/{productId}")
    public ApiResponse<Void> deleteInventory(@PathVariable UUID productId) {
        inventoryService.deleteInventory(productId);
        return new ApiResponse<>(EnumCode.INVENTORY_DELETED, null);
    }

    @GetMapping("/logs/{productId}")
    public ApiResponse<List<InventoryHistory>> getLogs(@PathVariable UUID productId) {
        List<InventoryHistory> logs = inventoryService.getHistoryByProductId(productId);
        return new ApiResponse<>(EnumCode.INVENTORY_LOG_FETCHED, logs);
    }

    @PostMapping("/reserve")
    public ApiResponse<Void> reserveInventory(@RequestBody InventoryReservedRequest request) {
        inventoryService.reserveInventoryService(request);
        return new ApiResponse<>(EnumCode.INVENTORY_RESERVED, null);
    }

    @PostMapping("/{orderId}/confirm")
    public ApiResponse<Void> confirmInventory(@PathVariable UUID orderId) {
        inventoryService.confirmInventoryService(orderId);
        return new ApiResponse<>(EnumCode.INVENTORY_CONFIRMED, null);
    }

    @PostMapping("/{orderId}/cancel")
    public ApiResponse<Void> cancelReserveInventory(@PathVariable UUID orderId) {
        inventoryService.cancelInventoryService(orderId);
        return new ApiResponse<>(EnumCode.INVENTORY_CANCELLED, null);
    }
}
