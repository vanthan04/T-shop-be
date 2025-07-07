package com.productservice.controllers;

import com.productservice.dto.request.product_type.CreateProductTypeRequest;
import com.productservice.dto.request.product_type.UpdateProductTypeRequest;
import com.productservice.dto.response.ApiResponse;
import com.productservice.dto.response.product_type.ProductTypeResponse;
import com.productservice.models.ProductType;
import com.productservice.services.ProductTypeService;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyRole('client_admin', 'client_user')")
    public ApiResponse<List<ProductTypeResponse>> getProductType() {
        List<ProductTypeResponse> productTypes = productTypeService.getProductType();
        return new ApiResponse<>(200, "Lấy danh sách thành công!", productTypes);
    }

    @PostMapping()
    @PreAuthorize("hasRole('client_admin')")
    public ApiResponse<ProductType> createProductType(@RequestBody CreateProductTypeRequest request) {
        ProductType created = productTypeService.createProductType(request.getTypeName());
        return new ApiResponse<>(200, "Tạo loại sản phẩm thành công!", created);
    }

    @PutMapping("")
    @PreAuthorize("hasRole('client_admin')")
    public ApiResponse<ProductType> updateProductType(@RequestBody UpdateProductTypeRequest request){
        ProductType updated = productTypeService.updateProductType(request.getTypeId(), request.getNewTypeName());
        return new ApiResponse<>(200, "Cập nhật loại sản phẩm thành công", updated);
    }

    @DeleteMapping("/{productTypeId}")
    @PreAuthorize("hasRole('client_admin')")
    public ApiResponse<Void> deleteProductType(@PathVariable UUID productTypeId) {
        productTypeService.deleteProductType(productTypeId);
        return new ApiResponse<>(200, "Xóa loại sản phẩm thành công", null);
    }
}
