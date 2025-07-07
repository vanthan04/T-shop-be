package com.productservice.dto.response.product_type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductTypeResponse {
    private UUID typeId;
    private String typeName;
}
