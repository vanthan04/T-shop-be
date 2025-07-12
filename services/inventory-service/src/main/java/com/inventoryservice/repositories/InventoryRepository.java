package com.inventoryservice.repositories;

import com.inventoryservice.models.InventoryModel;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryModel, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM InventoryModel i WHERE i.productId = :productId")
    Optional<InventoryModel> findByProductIdForUpdate(@Param("productId") UUID productId);

    Optional<InventoryModel> findInventoryModelByProductId(UUID productId);
}
