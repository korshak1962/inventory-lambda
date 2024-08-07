package com.korshak;

import com.korshak.lambda.InventoryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.annotation.EnableKafka;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableKafka
public class InventoryManagementApplication {
    private static final Logger logger = LoggerFactory.getLogger(InventoryManagementApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = null;
        try {
            context = SpringApplication.run(InventoryManagementApplication.class, args);
        } catch (Exception e) {
            logger.error("Failed to start the application", e);
        }
        // Create an instance of your handler
        InventoryHandler handler = context.getBean(InventoryHandler.class);

        // Create a sample input
        Map<String, Object> input = new HashMap<>();
        input.put("productId", "123");
        input.put("name", "Test Product");
        input.put("quantity", 10);

        // Invoke the handler
        String result = handler.handleRequest(input, null);

        System.out.println("Handler result: " + result);
    }
}