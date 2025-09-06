package com.productservice.repositories;

import com.productservice.models.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductTypeRepository extends JpaRepository<ProductType, UUID> {
    boolean existsByTypeNameIgnoreCase(String typeName);

}
