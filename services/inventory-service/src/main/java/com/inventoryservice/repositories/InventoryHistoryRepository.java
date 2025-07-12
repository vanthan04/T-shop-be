package com.inventoryservice.repositories;

import com.inventoryservice.models.InventoryActionType;
import com.inventoryservice.models.InventoryHistoryModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryHistoryRepository extends JpaRepository<InventoryHistoryModel, UUID> {

    List<InventoryHistoryModel> findByInventory_ProductIdOrderByActionDateDesc(UUID productId);

    List<InventoryHistoryModel> findAllByOrderId(UUID orderId);
    // Trong InventoryHistoryRepository
    List<InventoryHistoryModel> findByOrderIdAndActionType(UUID orderId, InventoryActionType actionType);

}
