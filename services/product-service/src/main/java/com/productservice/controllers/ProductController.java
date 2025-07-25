package com.productservice.controllers;

import com.productservice.dto.request.product.CreateProductRequest;
import com.productservice.dto.request.product.UpdateProductRequest;
import com.productservice.dto.response.ApiResponse;
import com.productservice.dto.response.product.ProductResponse;
import com.productservice.models.Product;
import com.productservice.services.ImageService;
import com.productservice.services.ProductService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;
    private final ImageService imageService;

    public ProductController(ProductService productService, ImageService imageService) {
        this.productService = productService;
        this.imageService = imageService;
    }

    @PostMapping
    public ApiResponse<Product> createNewProduct(@RequestPart("product") CreateProductRequest request,
                                                 @RequestPart("files") MultipartFile[] files) {

        List<String> urls = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            String url = imageService.upload(files[i], request.getProductName(), i);
            urls.add(url);
        }
        // gọi service
        Product saved = productService.createProduct(
                request.getTypeId(),
                request.getProductName(),
                request.getProductDescription(),
                request.getProductPrice(),
                urls
        );

        return new ApiResponse<>(true, "Thanh cong", saved);
    }

    @PutMapping("/{productId}")
    public ApiResponse<Product> updateProduct(
            @PathVariable UUID productId,
            @RequestBody UpdateProductRequest req
    ) {
        Product updated = productService.updateProduct(
                productId,
                req.getTypeId(),
                req.getProductName(),
                req.getProductDescription(),
                req.getProductPrice(),
                req.isActive()
        );
        return new ApiResponse<>(true, "Cập nhật thành công", updated);
    }

    @DeleteMapping("/{productId}")
    public ApiResponse<Void> deleteProduct(@PathVariable UUID productId) {
        productService.deleteProduct(productId);
        return new ApiResponse<>(true, "Xoá thành công", null);
    }

    @GetMapping("/{productId}")
    public ApiResponse<Product> getProductById(@PathVariable UUID productId) {
        Product product = productService.getProductById(productId);
        return new ApiResponse<>(true, "Lấy thành công", product);
    }

    @GetMapping
    public ApiResponse<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return new ApiResponse<>(true, "Lấy danh sách thành công", products);
    }

    /**
     * Xoá 1 số ảnh của product
     */
    @DeleteMapping("/{productId}/images")
    public ApiResponse<String> deleteProductImages(
            @PathVariable UUID productId,
            @RequestBody List<String> urlsToDelete
    ) {
        try {
            productService.deleteSomeImages(productId, urlsToDelete);
            return new ApiResponse<>(true, "Xoá ảnh thành công", null);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Lỗi khi xoá ảnh: " + e.getMessage(), null);
        }
    }


    /**
     * Thêm ảnh mới cho product
     */
    @PostMapping("/{productId}/images")
    public ApiResponse<String> addProductImages(
            @PathVariable UUID productId,
            @RequestPart("files") MultipartFile[] files
    ) {
        Product existed = productService.getProductById(productId);
        try {
            List<String> urls = new ArrayList<>();
            for (int i = 0; i < files.length; i++) {
                String url = imageService.upload(files[i], existed.getProductName() , i);
                urls.add(url);
            }
            productService.addImages(productId, urls);
            return new ApiResponse<>(true, "Thêm ảnh thành công", null);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Lỗi khi thêm ảnh: " + e.getMessage(), null);
        }
    }

}