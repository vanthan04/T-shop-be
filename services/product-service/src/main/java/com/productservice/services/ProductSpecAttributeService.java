package com.productservice.services;

import com.productservice.models.ProductSpecAttribute;
import com.productservice.models.ProductType;
import com.productservice.repositories.ProductSpecAttributeRepository;
import com.productservice.repositories.ProductTypeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProductSpecAttributeService {

    private final ProductSpecAttributeRepository attributeRepository;
    private final ProductTypeRepository productTypeRepository;

    public ProductSpecAttributeService(ProductSpecAttributeRepository attributeRepository,
                                       ProductTypeRepository productTypeRepository) {
        this.attributeRepository = attributeRepository;
        this.productTypeRepository = productTypeRepository;
    }

    /**
     * Lấy tất cả attribute
     */
    public List<ProductSpecAttribute> getAllAttributes() {
        return attributeRepository.findAll();
    }

    /**
     * Lấy attribute theo id
     */
    public ProductSpecAttribute getAttributeById(UUID attributeId) {
        return attributeRepository.findById(attributeId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy attribute với id " + attributeId));
    }

    /**
     * Tạo mới attribute
     */
    public ProductSpecAttribute createAttribute(UUID typeId, String attributeName, String dataType) {
        ProductType productType = productTypeRepository.findById(typeId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy productType với id " + typeId));

        ProductSpecAttribute attribute = new ProductSpecAttribute();
        attribute.setAttributeId(UUID.randomUUID());
        attribute.setProductType(productType);
        attribute.setAttributeName(attributeName);
        attribute.setDataType(dataType);

        return attributeRepository.save(attribute);
    }

    /**
     * Cập nhật attribute
     */
    public ProductSpecAttribute updateAttribute(UUID attributeId, String attributeName, String dataType) {
        ProductSpecAttribute attribute = attributeRepository.findById(attributeId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy attribute với id " + attributeId));

        attribute.setAttributeName(attributeName);
        attribute.setDataType(dataType);

        return attributeRepository.save(attribute);
    }

    /**
     * Xoá attribute
     */
    public void deleteAttribute(UUID attributeId) {
        if (!attributeRepository.existsById(attributeId)) {
            throw new IllegalArgumentException("Không tìm thấy attribute với id " + attributeId);
        }
        attributeRepository.deleteById(attributeId);
    }
}
