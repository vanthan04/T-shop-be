package com.productservice.repositories;

import com.productservice.models.ProductSpecValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductSpecValueRepository extends JpaRepository<ProductSpecValue, UUID> {
}
