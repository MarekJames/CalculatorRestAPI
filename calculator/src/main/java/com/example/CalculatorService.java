package com.example;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for processing calculation requests.
 * It listens for incoming calculation requests via Kafka and performs the required operation.
 * It sends the results back to Kafka or logs errors if any occur.
 */
@Service
public class CalculatorService {
    private final KafkaTemplate<String, String> kafkaTemplate;                                    
    private static final Logger logger = LoggerFactory.getLogger(CalculatorService.class);

    /**
     * Constructor to inject KafkaTemplate dependency.
     *
     * @param kafkaTemplate the KafkaTemplate to send messages to Kafka
     */
    public CalculatorService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Kafka listener method that processes incoming calculation requests.
     * It parses the request, performs the calculation, and sends the result back to Kafka.
     *
     * @param message the incoming message in the format: <requestId>,<operation>,<operand1>,<operand2>
     */
    @KafkaListener(topics = "calculation-requests", groupId = "calculator-group")
    public void processRequest(String message) {
        try {
            logger.info("Received calculation request: {}", message);

            String[] parts = message.split(",");
            if (parts.length < 4) {
                throw new IllegalArgumentException("Invalid message format: " + message);
            }

            // Extract the parts of the message
            String requestId = parts[0];
            String operation = parts[1];
            BigDecimal a = new BigDecimal(parts[2]);
            BigDecimal b = new BigDecimal(parts[3]);

            // Perform the requested operation
            BigDecimal result;
            switch (operation) {
                case "sum" -> result = a.add(b);  // Addition
                case "subtract" -> result = a.subtract(b);  // Subtraction
                case "multiply" -> result = a.multiply(b);  // Multiplication
                case "divide" -> result = a.divide(b, RoundingMode.HALF_UP);  // Division with precision
                default -> {
                    kafkaTemplate.send("calculation-results", requestId + ",Invalid operation");
                    logger.error("Invalid operation for requestId: {}", requestId);
                    return;
                }
            }

            // Check if the result is a whole number (i.e., no decimal part)
            if (result.stripTrailingZeros().scale() <= 0) {
                kafkaTemplate.send("calculation-results", requestId + "," + result.toBigInteger().toString());
            } else {
                kafkaTemplate.send("calculation-results", requestId + "," + result.toString());
            }
            logger.info("Sent result for requestId {}: {}", requestId, result);

        } catch (Exception e) {
            kafkaTemplate.send("calculation-results", "Error processing Kafka message: " + message);
            logger.error("Error processing Kafka message: {}", message, e);
        }
    }
}
