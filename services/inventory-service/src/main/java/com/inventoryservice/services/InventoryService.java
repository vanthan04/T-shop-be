package com.inventoryservice.services;

import com.inventoryservice.dto.request.InventoryCreatedRequest;
import com.inventoryservice.dto.request.InventoryReservedRequest;
import com.inventoryservice.dto.request.OrderItem;
import com.inventoryservice.dto.request.OrderItemInfo;
import com.inventoryservice.exception.AppException;
import com.inventoryservice.exception.ErrorCode;
import com.inventoryservice.models.Inventory;
import com.inventoryservice.models.InventoryActionType;
import com.inventoryservice.models.InventoryHistory;
import com.inventoryservice.repositories.InventoryHistoryRepository;
import com.inventoryservice.repositories.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryHistoryRepository inventoryHistoryRepository;

    @Transactional
    // Tạo mới hoặc nhập thêm hàng
    public Inventory createNewInventory(InventoryCreatedRequest request) {
        Optional<Inventory> existed = inventoryRepository.findInventoryModelByProductId(request.getProductId());

        Inventory inventory;
        if (existed.isPresent()) {
            inventory = existed.get();
            inventory.createOrImportInventory(inventory.getProductId(), request.getQuantity());

        } else {
            inventory = new Inventory();
            inventory.createOrImportInventory(request.getProductId(), request.getQuantity());

        }
        return inventoryRepository.save(inventory);

    }

    public Inventory getInventoryByProductId(UUID productId) {
        return inventoryRepository.findInventoryModelByProductId(productId)
                .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_PRODUCT_NOT_FOUND));
    }

    public Inventory updateQuantity(UUID productId, int newQuantity, String reason) {
        Inventory inventory = getInventoryByProductId(productId);

        int diff = newQuantity - inventory.getQuantity();
        inventory.setQuantity(newQuantity);
        inventory.setUpdatedAt(LocalDateTime.now());

        return inventoryRepository.save(inventory);
    }

    public void deleteInventory(UUID productId) {
        Inventory inventory = inventoryRepository.findInventoryModelByProductId(productId)
                .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_PRODUCT_NOT_FOUND));

        inventoryRepository.delete(inventory);

    }


    @Transactional
    public void reserveInventoryService(InventoryReservedRequest request){
        if (inventoryHistoryRepository.isExistedOrderInInventoryHistory(request.getOrderId())) {
            throw new AppException(ErrorCode.INVENTORY_ORDER_EXISTED);
        }

        for (OrderItem item : request.getItems()){
            Optional<Inventory> findInventory = inventoryRepository.findByProductId(item.getProductId());
            if (findInventory.isEmpty()){
                throw new AppException(ErrorCode.INVENTORY_PRODUCT_NOT_FOUND);
            }
            Inventory inventory = findInventory.get();
            inventory.reserveInventory(request.getOrderId(), item.getQuantity());
            inventoryRepository.save(inventory);
        }
    }

    @Transactional
    public void confirmInventoryService(UUID orderId){
        List<OrderItemInfo> items = inventoryHistoryRepository.getQuantityReserveAndInventoryByOrderId(orderId);
        if(items.isEmpty()){
            throw new AppException(ErrorCode.INVENTORY_OUT_OF_STOCK);
        }
        Inventory inventory = items.get(0).getInventory();
        for (OrderItemInfo item : items){
            if (item.getActionType() != InventoryActionType.RESERVE){
                throw new AppException(ErrorCode.INVENTORY_OUT_OF_STOCK);
            }
            inventory.confirmInventory(orderId, item.getChangeQuantity());
        }
        inventoryRepository.save(inventory);
    }

    @Transactional
    public void cancelInventoryService(UUID orderId){
        List<OrderItemInfo> items = inventoryHistoryRepository.getQuantityReserveAndInventoryByOrderId(orderId);
        if(items.isEmpty()){
            throw new AppException(ErrorCode.INVENTORY_OUT_OF_STOCK);
        }
        Inventory inventory = items.get(0).getInventory();
        for (OrderItemInfo item : items){
            if (item.getActionType() != InventoryActionType.RESERVE){
                throw new AppException(ErrorCode.INVENTORY_OUT_OF_STOCK);
            }
            inventory.cancelInventory(orderId, inventory.getQuantity());
        }
        inventoryRepository.save(inventory);
    }

    public List<InventoryHistory> getHistoryByProductId(UUID productId) {
        Inventory inventory = getInventoryByProductId(productId);
        return inventory.getHistories();
    }


}
