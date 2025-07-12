package com.productservice.services;

import com.productservice.dto.response.product_type.ProductTypeResponse;
import com.productservice.exception.AppException;
import com.productservice.models.ProductType;
import com.productservice.repositories.ProductTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductTypeService {
    private final ProductTypeRepository productTypeRepository;

    public ProductTypeService(ProductTypeRepository productTypeRepository) {
        this.productTypeRepository = productTypeRepository;
    }

    public List<ProductTypeResponse> getProductType() {
        List<ProductType> entities = productTypeRepository.findAll();
        return entities.stream()
                .map(pt -> new ProductTypeResponse(pt.getTypeId(), pt.getTypeName()))
                .toList();
    }

    public ProductType createProductType(String typeName) {
        if (typeName == null || typeName.trim().isEmpty()) {
            throw new AppException(400, "Tên loại sản phẩm không được để trống");
        }

        // Check tên đã tồn tại chưa
        boolean exists = productTypeRepository.existsByTypeNameIgnoreCase(typeName.trim());
        if (exists) {
            throw new AppException(409, "Tên loại sản phẩm đã tồn tại");
        }

        UUID typeId = UUID.randomUUID();
        ProductType productType = new ProductType(typeId, typeName.trim());
        return productTypeRepository.save(productType);
    }


    public ProductType updateProductType(UUID typeId, String newTypeName) {
        if (newTypeName == null || newTypeName.trim().isEmpty()) {
            throw new AppException(400, "Tên loại sản phẩm không được để trống");
        }

        ProductType existing = productTypeRepository.findById(typeId)
                .orElseThrow(() -> new AppException(404, "Không tìm thấy loại sản phẩm"));

        existing.setTypeName(newTypeName.trim());
        return productTypeRepository.save(existing);
    }

    public void deleteProductType(UUID productTypeId) {
        ProductType existing = productTypeRepository.findById(productTypeId)
                .orElseThrow(() -> new AppException(404, "Không tìm thấy loại sản phẩm"));

        productTypeRepository.delete(existing);
    }
}
