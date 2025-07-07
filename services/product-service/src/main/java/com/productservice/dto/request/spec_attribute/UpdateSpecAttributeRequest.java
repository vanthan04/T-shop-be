package com.productservice.dto.request.spec_attribute;

import lombok.Data;

@Data
public class UpdateSpecAttributeRequest {
    private String attributeName;
    private String dataType; // string / number / boolean

}
