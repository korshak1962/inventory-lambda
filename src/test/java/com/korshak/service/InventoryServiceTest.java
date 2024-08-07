package com.korshak.service;

import com.korshak.model.InventoryItem;
import com.korshak.repository.InventoryRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter counter;

    private InventoryService inventoryService;

    @BeforeEach
    public void setup() {
        inventoryService = new InventoryService(inventoryRepository, meterRegistry);
    }

    @Test
    public void testUpdateInventory() {
        // Arrange
        InventoryItem item = new InventoryItem();
        item.setProductId("test123");
        item.setName("Test Product");
        item.setQuantity(5);

        when(meterRegistry.counter(anyString(), any(String[].class))).thenReturn(counter);

        // Act
        inventoryService.updateInventory(item);

        // Assert
        verify(inventoryRepository).save(item);
        verify(meterRegistry).counter("inventory.updates", "product", "test123");
        verify(counter).increment();
    }
}