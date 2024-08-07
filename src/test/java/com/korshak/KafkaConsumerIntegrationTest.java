package com.korshak;

import com.korshak.model.InventoryItem;
import com.korshak.repository.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class KafkaConsumerIntegrationTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Test
    public void testKafkaConsumer() throws Exception {
        String message = "{\"productId\":\"integration123\",\"name\":\"Integration Test Product\",\"quantity\":3}";
        kafkaTemplate.send("inventory-updates", message);

        // Wait for the message to be processed
        Thread.sleep(5000);

        Optional<InventoryItem> savedItem = inventoryRepository.findById("integration123");
        assertTrue(savedItem.isPresent());
        assertEquals("Integration Test Product", savedItem.get().getName());
        assertEquals(3, savedItem.get().getQuantity());
    }
}
