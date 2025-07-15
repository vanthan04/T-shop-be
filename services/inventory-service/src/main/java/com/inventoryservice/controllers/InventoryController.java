package com.inventoryservice.controllers;

import com.inventoryservice.dto.request.InventoryCreatedRequest;
import com.inventoryservice.dto.request.InventoryManualUpdatedRequest;
import com.inventoryservice.dto.request.InventoryReservedRequest;
import com.inventoryservice.dto.response.ApiResponse;
import com.inventoryservice.dto.response.EnumCode;
import com.inventoryservice.models.InventoryHistoryModel;
import com.inventoryservice.models.InventoryModel;
import com.inventoryservice.services.InventoryHistoryService;
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
    private final InventoryHistoryService inventoryHistoryService;

    @PostMapping
    public ApiResponse<InventoryModel> createOrImport(@RequestBody InventoryCreatedRequest request) {
        InventoryModel inventory = inventoryService.createNewInventory(request);
        return new ApiResponse<>(EnumCode.INVENTORY_CREATED, inventory);
    }

    @GetMapping("/{productId}")
    public ApiResponse<InventoryModel> getByProductId(@PathVariable UUID productId) {
        InventoryModel inventory = inventoryService.getInventoryByProductId(productId);
        return new ApiResponse<>(EnumCode.INVENTORY_FETCHED, inventory);
    }

    @PutMapping("/manual-update")
    public ApiResponse<InventoryModel> manualUpdate(@RequestBody InventoryManualUpdatedRequest request) {
        InventoryModel updated = inventoryService.updateQuantity(
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
    public ApiResponse<List<InventoryHistoryModel>> getLogs(@PathVariable UUID productId) {
        List<InventoryHistoryModel> logs = inventoryHistoryService.getHistoryByProductId(productId);
        return new ApiResponse<>(EnumCode.INVENTORY_LOG_FETCHED, logs);
    }

    @PostMapping("/reserve")
    public ApiResponse<Boolean> reserveInventory(@RequestBody InventoryReservedRequest request) {
        inventoryService.reserveInventory(request);
        return new ApiResponse<>(EnumCode.INVENTORY_RESERVED, null);
    }

    @PostMapping("/{orderId}/confirm")
    public ApiResponse<Void> confirmInventory(@PathVariable UUID orderId) {
        inventoryService.confirmInventory(orderId);
        return new ApiResponse<>(EnumCode.INVENTORY_CONFIRMED, null);
    }

    @PostMapping("/{orderId}/cancel")
    public ApiResponse<Void> cancelReserveInventory(@PathVariable UUID orderId) {
        inventoryService.cancelReserveInventory(orderId);
        return new ApiResponse<>(EnumCode.INVENTORY_CANCELLED, null);
    }
}
