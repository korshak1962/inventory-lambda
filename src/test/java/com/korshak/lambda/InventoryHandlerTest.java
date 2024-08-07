package com.korshak.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.korshak.lambda.InventoryHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class InventoryHandlerTest {

    private InventoryHandler inventoryHandler;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Context context;

    @Mock
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(applicationContext.getBean(KafkaTemplate.class)).thenReturn(kafkaTemplate);
        when(applicationContext.getBean(ObjectMapper.class)).thenReturn(objectMapper);

        inventoryHandler = new InventoryHandler(applicationContext);
    }

    @Test
    void testHandleRequest_SuccessfulKafkaSend() throws Exception {
        // Arrange
        Map<String, Object> input = new HashMap<>();
        input.put("productId", "123");
        input.put("name", "Test Product");
        input.put("quantity", 10);

        String jsonInput = "{\"productId\":\"123\",\"name\":\"Test Product\",\"quantity\":10}";
        when(objectMapper.writeValueAsString(input)).thenReturn(jsonInput);
        when(kafkaTemplate.send(eq("inventory-updates"), anyString())).thenReturn(null);

        // Act
        String result = inventoryHandler.handleRequest(input, context);

        // Assert
        assertEquals("Inventory update sent to Kafka successfully", result);
        verify(kafkaTemplate).send("inventory-updates", jsonInput);
    }

    @Test
    void testHandleRequest_KafkaSendFailure() throws Exception {
        // Arrange
        Map<String, Object> input = new HashMap<>();
        input.put("productId", "123");
        input.put("name", "Test Product");
        input.put("quantity", 10);

        when(objectMapper.writeValueAsString(input)).thenReturn("{}");
        when(kafkaTemplate.send(eq("inventory-updates"), anyString())).thenThrow(new RuntimeException("Kafka send failed"));

        // Act
        String result = inventoryHandler.handleRequest(input, context);

        // Assert
        assertEquals("Error processing inventory update: Kafka send failed", result);
    }

    @Test
    void testHandleRequest_JsonSerializationFailure() throws Exception {
        // Arrange
        Map<String, Object> input = new HashMap<>();
        input.put("productId", "123");
        input.put("name", "Test Product");
        input.put("quantity", 10);

        when(objectMapper.writeValueAsString(input)).thenThrow(new RuntimeException("JSON serialization failed"));

        // Act
        String result = inventoryHandler.handleRequest(input, context);

        // Assert
        assertEquals("Error processing inventory update: JSON serialization failed", result);
    }
}