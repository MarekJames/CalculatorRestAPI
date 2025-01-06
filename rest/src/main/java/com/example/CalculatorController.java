package com.example;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class that handles incoming HTTP requests for calculation operations (sum, subtract, multiply, divide).
 * It communicates with Kafka to process the calculations and returns the results to the clients with a unique request ID in the response header.
 */
@RestController
public class CalculatorController {

    private static final String INPUT_TOPIC = "calculation-requests";                                           
    private static final String OUTPUT_TOPIC = "calculation-results";                                          
    private static final Logger logger = LoggerFactory.getLogger(CalculatorController.class);             
    final ConcurrentHashMap<String, CompletableFuture<String>> responseFutures = new ConcurrentHashMap<>(); 

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;  // Kafka template for sending messages to Kafka

    /**
     * Endpoint to handle sum requests.
     *
     * @param a the first operand
     * @param b the second operand
     * @return the result of the sum operation
     */
    @GetMapping("/sum")
    public ResponseEntity<CalculationResponse> sum(@RequestParam("a") double a, @RequestParam("b") double b) {
        return processRequest(a, b, "sum");
    }

    /**
     * Endpoint to handle subtraction requests.
     *
     * @param a the first operand
     * @param b the second operand
     * @return the result of the subtraction operation
     */
    @GetMapping("/subtract")
    public ResponseEntity<CalculationResponse> subtract(@RequestParam("a") double a, @RequestParam("b") double b) {
        return processRequest(a, b, "subtract");
    }

    /**
     * Endpoint to handle multiplication requests.
     *
     * @param a the first operand
     * @param b the second operand
     * @return the result of the multiplication operation
     */
    @GetMapping("/multiply")
    public ResponseEntity<CalculationResponse> multiply(@RequestParam("a") double a, @RequestParam("b") double b) {
        return processRequest(a, b, "multiply");
    }

    /**
     * Endpoint to handle division requests. It checks if the divisor is zero and returns an error message if so.
     *
     * @param a the numerator
     * @param b the denominator
     * @return the result of the division operation or an error if division by zero occurs
     */
    @GetMapping("/divide")
    public ResponseEntity<CalculationResponse> divide(@RequestParam("a") double a, @RequestParam("b") double b) {
        if (b == 0) {
            logger.error("Division by zero error for a: {}, b: {}", a, b);
            return ResponseEntity.badRequest().body(new CalculationResponse("Error: Division by zero"));
        }
        return processRequest(a, b, "divide");
    }

    /**
     * Method to process any calculation request (sum, subtract, multiply, or divide).
     * It sends the calculation request to Kafka and waits for the result.
     *
     * @param a the first operand
     * @param b the second operand
     * @param operation the operation to be performed (sum, subtract, multiply, or divide)
     * @return a ResponseEntity containing the result and a RequestId in the header
     */
    private ResponseEntity<CalculationResponse> processRequest(double a, double b, String operation) {
        String requestId = UUID.randomUUID().toString();  // Generate a unique request ID
        String message = String.join(",", requestId, operation, String.valueOf(a), String.valueOf(b));

        CompletableFuture<String> responseFuture = new CompletableFuture<>();
        responseFutures.put(requestId, responseFuture);

        kafkaTemplate.send(INPUT_TOPIC, requestId, message);

        try {
            logger.info("Received {} request for a: {} and b: {}", operation, a, b);

            String result = responseFuture.get(30, TimeUnit.SECONDS);
            CalculationResponse response = new CalculationResponse(result);

            return ResponseEntity.ok()
                    .header("RequestId", requestId)
                    .body(response);

        } catch (Exception e) {
            logger.error("Error processing request for {}: {}", requestId, e.getMessage(), e);  
            throw new RuntimeException("Failed to get response from Kafka", e);
        } finally {
            responseFutures.remove(requestId);
        }
    }

    /**
     * Kafka listener that listens for the results of the calculations from Kafka and completes the pending request.
     *
     * @param message the result message containing the requestId and the result
     */
    @KafkaListener(topics = OUTPUT_TOPIC, groupId = "calculator-group")
    public void listenToResults(String message) {
        try {
            String[] parts = message.split(",");
            if (parts.length < 2) throw new IllegalArgumentException("Invalid message format: " + message);

            CompletableFuture<String> responseFuture = responseFutures.get(parts[0]);
            if (responseFuture != null) {
                responseFuture.complete(parts[1]);
            } else {
                logger.error("No pending request for requestId: {}", parts[0]);
            }
        } catch (Exception e) {
            logger.error("Error processing Kafka message: {}", message, e);
        }
    }
}
