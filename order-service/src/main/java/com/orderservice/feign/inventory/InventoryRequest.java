package com.orderservice.feign.inventory;

import com.orderservice.dto.request.OrderItemRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryRequest {
    private UUID orderId;
    private List<OrderItemRequest> items;

}
