package com.productservice.event.producer.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ProductCreatedEvent {
    private UUID productId;
}
