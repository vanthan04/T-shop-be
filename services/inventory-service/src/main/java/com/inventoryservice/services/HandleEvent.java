package com.inventoryservice.services;

import com.inventoryservice.event.consumer.product.ProductCreatedEvent;
import com.inventoryservice.models.InventoryActionType;
import com.inventoryservice.models.InventoryHistoryModel;
import com.inventoryservice.models.InventoryModel;
import com.inventoryservice.repositories.InventoryHistoryRepository;
import com.inventoryservice.repositories.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class HandleEvent {

    private final InventoryRepository inventoryRepository;
    private final InventoryHistoryRepository inventoryHistoryModel;
    private final InventoryHistoryRepository inventoryHistoryRepository;


    @Transactional
    @KafkaListener(topics = "productCreated", groupId = "productGroup")
    public void createRecordInventory(ProductCreatedEvent productCreatedEvent){

        System.out.println(productCreatedEvent);
        InventoryModel inventoryModel = new InventoryModel(
                UUID.randomUUID(),
                productCreatedEvent.getProductId(),
                0,
                0,
                null
        );

        inventoryRepository.save(inventoryModel);

        InventoryHistoryModel inventoryHistoryModel = new InventoryHistoryModel(
                UUID.randomUUID(),
                inventoryModel,
                0,
                InventoryActionType.INIT,
                null,
                LocalDateTime.now(),
                "Khởi tạo kho khi tạo sản phẩm!"
        );

        inventoryHistoryRepository.save(inventoryHistoryModel);
    }
}
