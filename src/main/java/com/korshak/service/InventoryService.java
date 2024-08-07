package com.korshak.service;

import com.korshak.model.InventoryItem;
import com.korshak.repository.InventoryRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {
    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryRepository inventoryRepository;
    private final MeterRegistry meterRegistry;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository, MeterRegistry meterRegistry) {
        this.inventoryRepository = inventoryRepository;
        this.meterRegistry = meterRegistry;
    }

    public void updateInventory(InventoryItem item) {
        try {
            logger.debug("Updating inventory for product: {}", item.getProductId());
            inventoryRepository.save(item);
            meterRegistry.counter("inventory.updates", "product", item.getProductId()).increment();
            logger.info("Inventory updated successfully for product: {}", item.getProductId());
        } catch (Exception e) {
            logger.error("Error updating inventory for product: {}", item.getProductId(), e);
            meterRegistry.counter("inventory.update.errors", "product", item.getProductId()).increment();
            throw new RuntimeException("Failed to update inventory", e);
        }
    }
}