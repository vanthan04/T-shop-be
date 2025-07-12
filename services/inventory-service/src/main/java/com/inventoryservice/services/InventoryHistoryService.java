package com.inventoryservice.services;

import com.inventoryservice.models.InventoryActionType;
import com.inventoryservice.models.InventoryHistoryModel;
import com.inventoryservice.models.InventoryModel;
import com.inventoryservice.repositories.InventoryHistoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class InventoryHistoryService {
    private final InventoryHistoryRepository inventoryHistoryRepository;

    public void createHistory(InventoryModel inventory, int qty, InventoryActionType actionType, String note, UUID orderId) {
        InventoryHistoryModel history = new InventoryHistoryModel();
        history.setHistoryId(UUID.randomUUID());
        history.setInventory(inventory);
        history.setChangeQuantity(qty);
        history.setActionType(actionType);
        history.setOrderId(orderId);
        history.setActionDate(LocalDateTime.now());
        history.setNote(note);
        inventoryHistoryRepository.save(history);
    }



    public List<InventoryHistoryModel> getHistoryByProductId(UUID productId) {
        return inventoryHistoryRepository.findByInventory_ProductIdOrderByActionDateDesc(productId);
    }

    public boolean isOrderExisted(UUID orderId){
        List<InventoryHistoryModel> historyModelList = inventoryHistoryRepository.findAllByOrderId(orderId);
        return !historyModelList.isEmpty();
    }

    // Trong service
    public List<InventoryHistoryModel> getReserveHistoriesByOrderId(UUID orderId) {
        return inventoryHistoryRepository.findByOrderIdAndActionType(orderId, InventoryActionType.RESERVE);
    }


}
