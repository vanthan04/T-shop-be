package com.inventoryservice.repositories;

import com.inventoryservice.dto.request.OrderItemInfo;
import com.inventoryservice.models.InventoryHistory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryHistoryRepository extends JpaRepository<InventoryHistory, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT ",nativeQuery = true)
    InventoryHistory findByOrderId(UUID orderId);

    @Query(value = """
            SELECT i.inventory, i.changeQuantity, i.actionType
            FROM InventoryHistory i
            WHERE i.orderId = :orderId
           """
    )
    List<OrderItemInfo> getQuantityReserveAndInventoryByOrderId(
            @Param("orderId") UUID orderId
    );

    @Query(value = """
            SELECT COUNT(i) > 0
            FROM InventoryHistory i
            WHERE i.orderId = :orderId
           """
    )
    Boolean isExistedOrderInInventoryHistory(
            @Param("orderId") UUID orderId
    );

}
