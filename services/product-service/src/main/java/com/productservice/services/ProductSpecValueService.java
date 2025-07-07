package com.productservice.services;

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
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        ProductSpecAttribute attribute = attributeRepository.findById(attributeId)
                .orElseThrow(() -> new IllegalArgumentException("Attribute not found"));

        ProductSpecValue val = new ProductSpecValue();
        val.setValueId(UUID.randomUUID());
        val.setProduct(product);
        val.setAttribute(attribute);
        val.setValue(value);

        return valueRepository.save(val);
    }

    public ProductSpecValue updateValue(UUID valueId, String newValue) {
        ProductSpecValue existing = valueRepository.findById(valueId)
                .orElseThrow(() -> new IllegalArgumentException("Attribute value not found"));

        existing.setValue(newValue);
        return valueRepository.save(existing);
    }

    public void deleteValue(UUID valueId) {
        valueRepository.deleteById(valueId);
    }

    public List<ProductSpecValue> getAllValues() {
        return valueRepository.findAll();
    }

    public ProductSpecValue getValue(UUID valueId) {
        return valueRepository.findById(valueId)
                .orElseThrow(() -> new IllegalArgumentException("Attribute value not found"));
    }
}
