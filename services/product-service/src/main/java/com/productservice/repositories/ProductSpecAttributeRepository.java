package com.productservice.repositories;

import com.productservice.models.ProductSpecAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductSpecAttributeRepository extends JpaRepository<ProductSpecAttribute, UUID> {
}
