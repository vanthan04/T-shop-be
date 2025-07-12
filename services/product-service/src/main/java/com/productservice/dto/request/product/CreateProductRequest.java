package com.productservice.dto.request.product;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateProductRequest {
    private UUID typeId;
    private String productName;
    private String productDescription;
    private BigDecimal productPrice;

}
