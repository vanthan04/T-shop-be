package com.cartservice.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class AddNewProductToCart {
    private UUID userId;
    private UUID productId;
    private int quantity;
}
