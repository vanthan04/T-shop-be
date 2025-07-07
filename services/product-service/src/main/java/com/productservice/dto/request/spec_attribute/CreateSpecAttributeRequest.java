package com.productservice.dto.request.spec_attribute;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateSpecAttributeRequest {
    private UUID typeId;            // id của ProductType
    private String attributeName;   // tên thuộc tính
    private String dataType;        // string / number / boolean
}
