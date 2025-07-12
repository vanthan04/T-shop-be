package com.productservice.services;

import com.productservice.exception.AppException;
import com.productservice.models.Product;
import com.productservice.models.ProductSpecAttribute;
import com.productservice.models.ProductSpecValue;
import com.productservice.repositories.ProductRepository;
import com.productservice.repositories.ProductSpecAttributeRepository;
import com.productservice.repositories.ProductSpecValueRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductSpecValueService {

    private final ProductSpecValueRepository valueRepository;
    private final ProductRepository productRepository;
    private final ProductSpecAttributeRepository attributeRepository;

    public ProductSpecValueService(
            ProductSpecValueRepository valueRepository,
            ProductRepository productRepository,
            ProductSpecAttributeRepository attributeRepository
    ) {
        this.valueRepository = valueRepository;
        this.productRepository = productRepository;
        this.attributeRepository = attributeRepository;
    }

    public ProductSpecValue createValue(UUID productId, UUID attributeId, String value) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(404, "Không tìm thấy sản phẩm"));

        ProductSpecAttribute attribute = attributeRepository.findById(attributeId)
                .orElseThrow(() -> new AppException(404, "Không tìm thấy thuộc tính"));

        ProductSpecValue val = new ProductSpecValue();
        val.setValueId(UUID.randomUUID());
        val.setProduct(product);
        val.setAttribute(attribute);
        val.setValue(value);

        return valueRepository.save(val);
    }

    public ProductSpecValue updateValue(UUID valueId, String newValue) {
        ProductSpecValue existing = valueRepository.findById(valueId)
                .orElseThrow(() -> new AppException(404, "Không tìm thấy giá trị thuộc tính"));

        existing.setValue(newValue);
        return valueRepository.save(existing);
    }

    public void deleteValue(UUID valueId) {
        if (!valueRepository.existsById(valueId)) {
            throw new AppException(404, "Không tìm thấy giá trị thuộc tính để xóa");
        }
        valueRepository.deleteById(valueId);
    }

    public List<ProductSpecValue> getAllValues() {
        return valueRepository.findAll();
    }

    public ProductSpecValue getValue(UUID valueId) {
        return valueRepository.findById(valueId)
                .orElseThrow(() -> new AppException(404, "Không tìm thấy giá trị thuộc tính"));
    }
}
