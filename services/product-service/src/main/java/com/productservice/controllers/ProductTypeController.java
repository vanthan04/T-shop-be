package com.productservice.controllers;

import com.productservice.dto.request.product_type.CreateProductTypeRequest;
import com.productservice.dto.request.product_type.UpdateProductTypeRequest;
import com.productservice.dto.response.ApiResponse;
import com.productservice.dto.response.product_type.ProductTypeResponse;
import com.productservice.models.ProductType;
import com.productservice.services.ProductTypeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products/product-type")
public class ProductTypeController {
    private final ProductTypeService productTypeService;

    public ProductTypeController(ProductTypeService productTypeService) {
        this.productTypeService = productTypeService;
    }

    @GetMapping()
    public ApiResponse<List<ProductTypeResponse>> getProductType() {
        List<ProductTypeResponse> productTypes = productTypeService.getProductType();
        return new ApiResponse<>(true, "Lấy danh sách thành công!", productTypes);
    }

    @PostMapping()
    public ApiResponse<ProductType> createProductType(@RequestBody CreateProductTypeRequest request) {
        ProductType created = productTypeService.createProductType(request.getTypeName());
        return new ApiResponse<>(true, "Tạo loại sản phẩm thành công!", created);
    }

    @PutMapping("")
    public ApiResponse<ProductType> updateProductType(@RequestBody UpdateProductTypeRequest request){
        ProductType updated = productTypeService.updateProductType(request.getTypeId(), request.getNewTypeName());
        return new ApiResponse<>(true, "Cập nhật loại sản phẩm thành công", updated);
    }

    @DeleteMapping("/{productTypeId}")
    public ApiResponse<Void> deleteProductType(@PathVariable UUID productTypeId) {
        productTypeService.deleteProductType(productTypeId);
        return new ApiResponse<>(true, "Xóa loại sản phẩm thành công", null);
    }
}
