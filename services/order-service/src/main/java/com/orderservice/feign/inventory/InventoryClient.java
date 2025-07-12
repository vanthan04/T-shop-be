package com.orderservice.feign.inventory;

import com.orderservice.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "inventory-service", path = "/api/v1/inventory")
public interface InventoryClient {

    @PostMapping("/reserve")
    ApiResponse<Void> reserve(@RequestBody InventoryRequest request);

    @PostMapping("/{orderId}/confirm")
    ApiResponse<Void> confirmInventory(@PathVariable UUID orderId);

    @PostMapping("/{orderId}/cancel")
    ApiResponse<Void> cancelReserveInventory(@PathVariable UUID orderId);
}
