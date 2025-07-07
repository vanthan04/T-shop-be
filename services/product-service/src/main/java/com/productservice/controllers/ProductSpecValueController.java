package com.productservice.controllers;

import com.productservice.dto.request.spec_value.CreateSpecValueRequest;
import com.productservice.dto.response.ApiResponse;
import com.productservice.models.ProductSpecValue;
import com.productservice.services.ProductSpecValueService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products/spec-values")
public class ProductSpecValueController {

    private final ProductSpecValueService valueService;

    public ProductSpecValueController(ProductSpecValueService valueService) {
        this.valueService = valueService;
    }

    @GetMapping
    public ApiResponse<List<ProductSpecValue>> getAll() {
        return new ApiResponse<>(200, "Lấy danh sách thành công", valueService.getAllValues());
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductSpecValue> getById(@PathVariable UUID id) {
        return new ApiResponse<>(200, "Lấy thành công", valueService.getValue(id));
    }

    @PostMapping
    public ApiResponse<ProductSpecValue> create(@RequestBody CreateSpecValueRequest request) {
        return new ApiResponse<>(
                200,
                "Tạo thành công",
                valueService.createValue(request.getProductId(), request.getAttributeId(), request.getValue())
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<ProductSpecValue> update(@PathVariable UUID id,
                                                @RequestBody String newValue) {
        return new ApiResponse<>(200, "Cập nhật thành công", valueService.updateValue(id, newValue));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        valueService.deleteValue(id);
        return new ApiResponse<>(200, "Xoá thành công", null);
    }
}
