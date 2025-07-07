package com.productservice.controllers;

import com.productservice.dto.request.spec_attribute.CreateSpecAttributeRequest;
import com.productservice.dto.request.spec_attribute.UpdateSpecAttributeRequest;
import com.productservice.models.ProductSpecAttribute;
import com.productservice.services.ProductSpecAttributeService;
import com.productservice.dto.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products/spec-attributes")
public class ProductSpecAttributeController {

    private final ProductSpecAttributeService attributeService;

    public ProductSpecAttributeController(ProductSpecAttributeService attributeService) {
        this.attributeService = attributeService;
    }

    @GetMapping
    public ApiResponse<List<ProductSpecAttribute>> getAll() {
        return new ApiResponse<>(200, "Lấy danh sách thành công", attributeService.getAllAttributes());
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductSpecAttribute> getById(@PathVariable UUID id) {
        return new ApiResponse<>(200, "Lấy thành công", attributeService.getAttributeById(id));
    }

    @PostMapping
    public ApiResponse<ProductSpecAttribute> create(@RequestBody CreateSpecAttributeRequest request) {
        return new ApiResponse<>(
                200,
                "Tạo thành công",
                attributeService.createAttribute(
                        request.getTypeId(),
                        request.getAttributeName(),
                        request.getDataType()
                )
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<ProductSpecAttribute> update(@PathVariable UUID id,
                                                    @RequestBody UpdateSpecAttributeRequest request) {
        return new ApiResponse<>(
                200,
                "Cập nhật thành công",
                attributeService.updateAttribute(
                        id,
                        request.getAttributeName(),
                        request.getDataType()
                )
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        attributeService.deleteAttribute(id);
        return new ApiResponse<>(200, "Xoá thành công", null);
    }
}
