package com.korshak.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.korshak.InventoryManagementApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
@Component
public class InventoryHandler implements RequestHandler<Map<String, Object>, String> {
    private static ApplicationContext context;
    private static KafkaTemplate<String, String> kafkaTemplate;
    private static ObjectMapper objectMapper;


    // Constructor for testing
    public InventoryHandler(ApplicationContext applicationContext) {
        if (applicationContext != null) {
            context = applicationContext;
        }
        if (context == null) {
            context = new AnnotationConfigApplicationContext(InventoryManagementApplication.class);
        }
        this.kafkaTemplate = context.getBean(KafkaTemplate.class);
        this.objectMapper = context.getBean(ObjectMapper.class);
    }

    @Override
    public String handleRequest(Map<String, Object> input, Context context) {
        initializeSpringContext();

        try {
            // Convert input to JSON string
            String inventoryUpdateJson = objectMapper.writeValueAsString(input);

            // Send to Kafka topic
            kafkaTemplate.send("inventory-updates", inventoryUpdateJson);

            return "Inventory update sent to Kafka successfully";
        } catch (Exception e) {
            return "Error processing inventory update: " + e.getMessage();
        }
    }

    private void initializeSpringContext() {
        if (context == null) {
            context = new AnnotationConfigApplicationContext(InventoryManagementApplication.class);
            kafkaTemplate = context.getBean(KafkaTemplate.class);
            objectMapper = context.getBean(ObjectMapper.class);
        }
    }
}