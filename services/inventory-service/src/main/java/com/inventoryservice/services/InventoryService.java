package com.inventoryservice.services;

import com.inventoryservice.dto.request.InventoryCreatedRequest;
import com.inventoryservice.dto.request.InventoryReservedRequest;
import com.inventoryservice.dto.request.OrderItem;
import com.inventoryservice.exception.AppException;
import com.inventoryservice.exception.ErrorCode;
import com.inventoryservice.models.InventoryActionType;
import com.inventoryservice.models.InventoryHistoryModel;
import com.inventoryservice.models.InventoryModel;
import com.inventoryservice.repositories.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryHistoryService inventoryHistoryService;

    // Tạo mới hoặc nhập thêm hàng
    public InventoryModel createNewInventory(InventoryCreatedRequest request) {
        Optional<InventoryModel> existed = inventoryRepository.findInventoryModelByProductId(request.getProductId());

        InventoryModel inventory;
        int changeQuantity = request.getQuantity();

        if (existed.isPresent()) {
            inventory = existed.get();
            inventory.importQuantity(changeQuantity);
        } else {
            inventory = new InventoryModel();
            inventory.createNewInventory(request.getProductId(), changeQuantity);
        }

        InventoryModel saved = inventoryRepository.save(inventory);

        inventoryHistoryService.createHistory(
                saved,
                changeQuantity,
                0,
                InventoryActionType.IMPORT,
                existed.isPresent() ? "Nhập thêm hàng vào kho" : "Tạo mới kho sản phẩm",
                null
        );

        return saved;
    }

    public InventoryModel getInventoryByProductId(UUID productId) {
        return inventoryRepository.findInventoryModelByProductId(productId)
                .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_PRODUCT_NOT_FOUND));
    }

    public InventoryModel updateQuantity(UUID productId, int newQuantity, String reason) {
        InventoryModel inventory = getInventoryByProductId(productId);

        int diff = newQuantity - inventory.getQuantity();
        inventory.setQuantity(newQuantity);
        inventory.setUpdatedAt(LocalDateTime.now());
        InventoryModel saved = inventoryRepository.save(inventory);

        inventoryHistoryService.createHistory(
                saved,
                diff,
                0,
                InventoryActionType.ADJUST,
                reason != null ? reason : "Điều chỉnh tồn kho thủ công",
                null
        );

        return saved;
    }

    public void deleteInventory(UUID productId) {
        InventoryModel inventory = inventoryRepository.findInventoryModelByProductId(productId)
                .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_PRODUCT_NOT_FOUND));

        inventoryRepository.delete(inventory);

        inventoryHistoryService.createHistory(
                inventory,
                -inventory.getQuantity(),
                0,
                InventoryActionType.EXPORT,
                "Xóa sản phẩm khỏi kho",
                null
        );
    }


    /**
     * Đặt giữ hàng: quantity -> reservedQuantity
     */
    @Transactional
    public void reserveInventory(InventoryReservedRequest request) {
        if (inventoryHistoryService.isOrderExisted(request.getOrderId())) {
            throw new AppException(ErrorCode.INVENTORY_ORDER_EXISTED);
        }

        Map<UUID, InventoryModel> inventoryMap = new HashMap<>();

        // Lock & kiểm tra tồn kho
        for (OrderItem item : request.getItems()) {
            InventoryModel inventory = inventoryRepository.findByProductIdForUpdate(item.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_PRODUCT_NOT_FOUND));

            if (!inventory.canReserve(item.getProductId(), item.getQuantity())) {
                throw new AppException(ErrorCode.INSUFFICIENT_INVENTORY);
            }

            inventoryMap.put(item.getProductId(), inventory);
        }

        // Cập nhật và ghi log
        for (OrderItem item : request.getItems()) {
            InventoryModel inventory = inventoryMap.get(item.getProductId());
            int qty = item.getQuantity();

            inventory.reserveInventory(qty);

            inventoryRepository.save(inventory);

            inventoryHistoryService.createHistory(
                    inventory,
                    -qty,
                    qty,
                    InventoryActionType.RESERVE,
                    "Đặt giữ hàng",
                    request.getOrderId()
            );
        }
    }

    /**
     * Xác nhận giữ hàng: reservedQuantity -> giảm vĩnh viễn
     */
    @Transactional
    public void confirmInventory(UUID orderId) {
        List<InventoryHistoryModel> items = inventoryHistoryService.getReserveHistoriesByOrderId(orderId);
        if(items.isEmpty()){
            throw new AppException(ErrorCode.INVENTORY_ORDER_NOT_FOUND);
        }

        for (InventoryHistoryModel item : items) {
            InventoryModel inventory = inventoryRepository.findByProductIdForUpdate(
                    item.getInventory().getProductId()
            ).orElseThrow(() -> new AppException(ErrorCode.INVENTORY_PRODUCT_NOT_FOUND));

            if (!inventory.canConfirm(orderId, item.getInventory().getProductId(), item.getInventory().getQuantity())) {
                throw new AppException(ErrorCode.INSUFFICIENT_INVENTORY);
            }

            inventory.confirmInventory(Math.abs(item.getQuantityChange()));
            inventoryRepository.save(inventory);

            inventoryHistoryService.createHistory(
                    inventory,
                    0,
                    item.getQuantityChange(),
                    InventoryActionType.CONFIRM,
                    "Xác nhận giữ hàng (trừ kho)",
                    orderId
            );
        }
    }

    /**
     * Hủy giữ hàng: trả lại quantity, trừ reservedQuantity
     */
    @Transactional
    public void cancelReserveInventory(UUID orderId) {
        List<InventoryHistoryModel> items = inventoryHistoryService.getReserveHistoriesByOrderId(orderId);
        if(items.isEmpty()){
            throw new AppException(ErrorCode.INVENTORY_ORDER_NOT_FOUND);
        }
        for (InventoryHistoryModel item : items) {
            InventoryModel inventory = inventoryRepository.findByProductIdForUpdate(
                    item.getInventory().getProductId()
            ).orElseThrow(() -> new AppException(ErrorCode.INVENTORY_PRODUCT_NOT_FOUND));

            if (!inventory.canCancel(orderId, item.getInventory().getProductId(), item.getInventory().getQuantity())) {
                throw new AppException(ErrorCode.INSUFFICIENT_INVENTORY);
            }

            inventory.cancelInventory(item.getReserveChange());
            inventoryRepository.save(inventory);

            inventoryHistoryService.createHistory(
                    inventory,
                    item.getReserveChange(),
                    item.getQuantityChange(),
                    InventoryActionType.CANCEL_RESERVE,
                    "Hủy giữ hàng (trả lại kho)",
                    orderId
            );
        }
    }
}
