package com.korshak.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.korshak.model.InventoryItem;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;
    private final MeterRegistry meterRegistry;

    @Autowired
    public KafkaConsumerService(InventoryService inventoryService, ObjectMapper objectMapper, MeterRegistry meterRegistry) {
        this.inventoryService = inventoryService;
        this.objectMapper = objectMapper;
        this.meterRegistry = meterRegistry;
    }

    @KafkaListener(topics = "inventory-updates", groupId = "inventory-group")
    public void consume(String message) {
        try {
            logger.debug("Received message: {}", message);
            InventoryItem item = objectMapper.readValue(message, InventoryItem.class);
            inventoryService.updateInventory(item);
            meterRegistry.counter("kafka.messages.processed").increment();
        } catch (Exception e) {
            logger.error("Error processing Kafka message: {}", message, e);
            meterRegistry.counter("kafka.messages.errors").increment();
            // Depending on your error handling strategy, you might want to:
            // 1. Retry the message
            // 2. Send to a dead-letter queue
            // 3. Simply log the error and continue
        }
    }
}