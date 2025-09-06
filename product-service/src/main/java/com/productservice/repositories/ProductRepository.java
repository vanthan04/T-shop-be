package com.productservice.repositories;

import com.productservice.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findByProductName(String name);

    @Query(value = """
    SELECT a.imageUrls 
    FROM Product a
    WHERE a.productId = :productId
""")
    List<String> getImgUrlByProductId(@Param("productId") UUID productId);
}
