package com.inventoryservice.services;

import com.inventoryservice.dto.request.InventoryCreatedRequest;
import com.inventoryservice.dto.request.InventoryReservedRequest;
import com.inventoryservice.dto.request.OrderItem;
import com.inventoryservice.exception.AppException;
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
            inventory.setQuantity(inventory.getQuantity() + changeQuantity);
        } else {
            inventory = new InventoryModel();
            inventory.setInventoryId(UUID.randomUUID());
            inventory.setProductId(request.getProductId());
            inventory.setQuantity(changeQuantity);
            inventory.setReservedQuantity(0);
        }

        inventory.setUpdatedAt(LocalDateTime.now());
        InventoryModel saved = inventoryRepository.save(inventory);

        inventoryHistoryService.createHistory(
                saved,
                changeQuantity,
                InventoryActionType.IMPORT,
                existed.isPresent() ? "Nhập thêm hàng vào kho" : "Tạo mới kho sản phẩm",
                null
        );

        return saved;
    }

    public InventoryModel getInventoryByProductId(UUID productId) {
        return inventoryRepository.findInventoryModelByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + productId));
    }

    public InventoryModel updateQuantity(UUID productId, int newQuantity, String reason) {
        InventoryModel inventory = inventoryRepository.findInventoryModelByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + productId));

        int diff = newQuantity - inventory.getQuantity();
        inventory.setQuantity(newQuantity);
        inventory.setUpdatedAt(LocalDateTime.now());
        InventoryModel saved = inventoryRepository.save(inventory);

        inventoryHistoryService.createHistory(
                saved,
                diff,
                InventoryActionType.ADJUST,
                reason != null ? reason : "Điều chỉnh tồn kho thủ công",
                null
        );

        return saved;
    }

    public void deleteInventory(UUID productId) {
        InventoryModel inventory = inventoryRepository.findInventoryModelByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm để xóa"));

        inventoryRepository.delete(inventory);

        inventoryHistoryService.createHistory(
                inventory,
                -inventory.getQuantity(),
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
            throw new AppException(400, "Đơn hàng đã tồn tại!");
        }

        Map<UUID, InventoryModel> inventoryMap = new HashMap<>();

        // Lock & kiểm tra tồn kho
        for (OrderItem item : request.getItems()) {
            InventoryModel inventory = inventoryRepository.findByProductIdForUpdate(item.getProductId())
                    .orElseThrow(() -> new AppException(400, "Không tìm thấy sản phẩm: " + item.getProductId()));

            if (inventory.getQuantity() < item.getQuantity()) {
                throw new AppException(400, "Không đủ hàng cho sản phẩm: " + item.getProductId());
            }

            inventoryMap.put(item.getProductId(), inventory);
        }

        // Cập nhật và ghi log
        for (OrderItem item : request.getItems()) {
            InventoryModel inventory = inventoryMap.get(item.getProductId());
            int qty = item.getQuantity();

            inventory.setQuantity(inventory.getQuantity() - qty);
            inventory.setReservedQuantity(inventory.getReservedQuantity() + qty);
            inventory.setUpdatedAt(LocalDateTime.now());
            inventoryRepository.save(inventory);

            inventoryHistoryService.createHistory(
                    inventory,
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
            throw new AppException(400, "Khong tim thay don hang de xac nhan");
        }

        for (InventoryHistoryModel item : items) {
            InventoryModel inventory = inventoryRepository.findByProductIdForUpdate(
                    item.getInventory().getProductId()
            ).orElseThrow(() -> new AppException(400, "Không tìm thấy sản phẩm: " + item.getInventory().getProductId()));

            if (inventory.getReservedQuantity() < item.getChangeQuantity()) {
                throw new AppException(400, "Không đủ hàng đã giữ để xác nhận cho sản phẩm: " + item.getInventory().getProductId());
            }

            inventory.setReservedQuantity(inventory.getReservedQuantity() - item.getChangeQuantity());
            inventory.setUpdatedAt(LocalDateTime.now());
            inventoryRepository.save(inventory);

            inventoryHistoryService.createHistory(
                    inventory,
                    item.getChangeQuantity(),
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
            throw new AppException(400, "Khong tim thay don hang de huy");
        }
        for (InventoryHistoryModel item : items) {
            InventoryModel inventory = inventoryRepository.findByProductIdForUpdate(
                    item.getInventory().getProductId()
            ).orElseThrow(() -> new AppException(400, "Không tìm thấy sản phẩm: " + item.getInventory().getProductId()));

            if (inventory.getReservedQuantity() < item.getChangeQuantity()) {
                throw new AppException(400, "Không đủ hàng đã giữ để hủy cho sản phẩm: " + item.getInventory().getProductId());
            }

            inventory.setReservedQuantity(inventory.getReservedQuantity() - item.getChangeQuantity());
            inventory.setQuantity(inventory.getQuantity() + item.getChangeQuantity());
            inventory.setUpdatedAt(LocalDateTime.now());
            inventoryRepository.save(inventory);

            inventoryHistoryService.createHistory(
                    inventory,
                    item.getChangeQuantity(),
                    InventoryActionType.CANCEL_RESERVE,
                    "Hủy giữ hàng (trả lại kho)",
                    orderId
            );
        }
    }
}
