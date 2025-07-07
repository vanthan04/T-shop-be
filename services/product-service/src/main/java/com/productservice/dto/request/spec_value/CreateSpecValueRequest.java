package com.productservice.dto.request.spec_value;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateSpecValueRequest {
    private UUID productId;
    private UUID attributeId;
    private String value;

}
