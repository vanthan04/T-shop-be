package com.productservice.dto.response.product;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class ProductResponse {
    private UUID productId;
    private String productName;
    private String productDescription;
    private BigDecimal productPrice;
    private List<String> imageUrls;
    private Map<String, Object> productAttributes;
    private UUID typeId;
    private String typeName;
    // các trường khác nếu cần
}
