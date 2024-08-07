package com.korshak.service;

import com.korshak.model.InventoryItem;
import com.korshak.repository.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class InventoryServiceTestSpring {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Test
    public void testUpdateInventory() {
        InventoryItem item = new InventoryItem();
        item.setProductId("test123");
        item.setName("Test Product");
        item.setQuantity(5);

        inventoryService.updateInventory(item);

        Optional<InventoryItem> savedItem = inventoryRepository.findById("test123");
        assertTrue(savedItem.isPresent());
        assertEquals("Test Product", savedItem.get().getName());
        assertEquals(5, savedItem.get().getQuantity());
    }
}
