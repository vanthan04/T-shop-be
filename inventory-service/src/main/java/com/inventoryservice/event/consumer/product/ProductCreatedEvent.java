package com.inventoryservice.event.consumer.product;

import lombok.Data;

import java.util.UUID;

@Data
public class ProductCreatedEvent {
    private UUID productId;
}
