package com.productservice.dto.request.product_type;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateProductTypeRequest {
    private UUID typeId;
    private String newTypeName;
}
