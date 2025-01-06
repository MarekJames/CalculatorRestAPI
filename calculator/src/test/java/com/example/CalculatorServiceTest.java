package com.example;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ContextConfiguration;

/**
 * This test class verifies the functionality of the CalculatorService.
 * It mocks the KafkaTemplate and uses Embedded Kafka for testing Kafka interactions.
 * Each test method checks the processing of different operations by the CalculatorService.
 */
@SpringBootTest
@ContextConfiguration(classes = com.example.CalculatorApplication.class)  // Explicitly specify the main application class for context configuration
@EmbeddedKafka(partitions = 1, topics = {"calculation-requests", "calculation-results"})  // Set up embedded Kafka with the specified topics for testing
public class CalculatorServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate; // Mock the KafkaTemplate to simulate interactions with Kafka

    @InjectMocks
    private CalculatorService calculatorService; // Inject CalculatorService with the mocked KafkaTemplate

    /**
     * Test case for processing a sum operation.
     * Verifies that the Kafka message sent contains the correct result and requestId.
     */
    @Test
    public void testProcessRequest_sum() {
        
        // Given a sum operation with requestId and message
        String requestId = "12345";
        String message = requestId + ",sum,10,5"; // Message format: <requestId>,<operation>,<operand1>,<operand2>

        // Execute the processRequest method
        calculatorService.processRequest(message);

        // Verify KafkaTemplate interaction - checking that it sends the correct message once
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);  // Capture the Kafka message sent
        verify(kafkaTemplate, times(1)).send(eq("calculation-results"), captor.capture()); // Verify that send was called once with the expected topic

        // Assert the Kafka message contains the requestId and the correct result
        String sentMessage = captor.getValue();
        assertTrue(sentMessage.contains(requestId));  // Check if requestId is in the message
        assertTrue(sentMessage.contains("15")); // Ensure the result of the sum operation (10 + 5 = 15) is included in the message
    }

    /**
     * Test case for processing a subtraction operation.
     * Verifies that the Kafka message sent contains the correct result and requestId.
     */
    @Test
    public void testProcessRequest_subtract() {
        
        // Given a subtraction operation with requestId and message
        String requestId = "12346";
        String message = requestId + ",subtract,10,5"; // Message format: <requestId>,<operation>,<operand1>,<operand2>

        // Execute the processRequest method
        calculatorService.processRequest(message);

        // Verify KafkaTemplate interaction - checking that it sends the correct message once
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);  // Capture the Kafka message sent
        verify(kafkaTemplate, times(1)).send(eq("calculation-results"), captor.capture()); // Verify that send was called once with the expected topic

        // Assert the Kafka message contains the requestId and the correct result
        String sentMessage = captor.getValue();
        assertTrue(sentMessage.contains(requestId));  // Check if requestId is in the message
        assertTrue(sentMessage.contains("5")); // Ensure the result of the subtraction operation (10 - 5 = 5) is included in the message
    }

    /**
     * Test case for processing a multiplication operation.
     * Verifies that the Kafka message sent contains the correct result and requestId.
     */
    @Test
    public void testProcessRequest_multiply() {
        
        // Given a multiplication operation with requestId and message
        String requestId = "12347";
        String message = requestId + ",multiply,10,5"; // Message format: <requestId>,<operation>,<operand1>,<operand2>

        // Execute the processRequest method
        calculatorService.processRequest(message);

        // Verify KafkaTemplate interaction - checking that it sends the correct message once
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);  // Capture the Kafka message sent
        verify(kafkaTemplate, times(1)).send(eq("calculation-results"), captor.capture()); // Verify that send was called once with the expected topic

        // Assert the Kafka message contains the requestId and the correct result
        String sentMessage = captor.getValue();
        assertTrue(sentMessage.contains(requestId));  // Check if requestId is in the message
        assertTrue(sentMessage.contains("50")); // Ensure the result of the multiplication operation (10 * 5 = 50) is included in the message
    }

    /**
     * Test case for processing a division operation.
     * Verifies that the Kafka message sent contains the correct result and requestId.
     */
    @Test
    public void testProcessRequest_divide() {
        
        // Given a division operation with requestId and message
        String requestId = "12348";
        String message = requestId + ",divide,10.6,5"; // Message format: <requestId>,<operation>,<operand1>,<operand2>

        // Execute the processRequest method
        calculatorService.processRequest(message);

        // Verify KafkaTemplate interaction - checking that it sends the correct message once
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);  // Capture the Kafka message sent
        verify(kafkaTemplate, times(1)).send(eq("calculation-results"), captor.capture()); // Verify that send was called once with the expected topic

        // Assert the Kafka message contains the requestId and the correct result
        String sentMessage = captor.getValue();
        assertTrue(sentMessage.contains(requestId));  // Check if requestId is in the message
        assertTrue(sentMessage.contains("2")); // Ensure the result of the division operation (10.6 / 5 = 2) is included in the message
    }

    /**
     * Test case for processing an invalid operation.
     * Verifies that the Kafka message sent contains the correct error message and requestId.
     */
    @Test
    public void testProcessRequest_invalid_operation() {
        
        // Given an invalid operation with requestId and message
        String requestId = "12350";
        String message = requestId + ",invalid,10,5"; // Invalid operation

        // Execute the processRequest method
        calculatorService.processRequest(message);

        // Verify KafkaTemplate interaction - checking that it sends the correct error message once
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);  // Capture the Kafka message sent
        verify(kafkaTemplate, times(1)).send(eq("calculation-results"), captor.capture()); // Verify that send was called once with the expected topic

        // Assert the Kafka message contains the requestId and the error message
        String sentMessage = captor.getValue();
        assertTrue(sentMessage.contains(requestId));  // Check if requestId is in the message
        assertTrue(sentMessage.contains("Invalid operation")); // Ensure the error message is included in the message
    }

    /**
     * Test case for processing a message with an incorrect format (less than expected parts).
     * Verifies that the Kafka message sent contains the correct error message and requestId.
     */
    @Test
    public void testProcessRequest_three_parts() {
        
        // Given an invalid message format with missing operand
        String requestId = "123530";
        String message = requestId + ",sum,10"; // Missing second operand

        // Execute the processRequest method
        calculatorService.processRequest(message);

        // Verify KafkaTemplate interaction - checking that it sends the correct error message once
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);  // Capture the Kafka message sent
        verify(kafkaTemplate, times(1)).send(eq("calculation-results"), captor.capture()); // Verify that send was called once with the expected topic

        // Assert the Kafka message contains the requestId and error message
        String sentMessage = captor.getValue();
        assertTrue(sentMessage.contains(requestId));  // Check if requestId is in the message
        assertTrue(sentMessage.contains("Error processing Kafka message")); // Ensure the error message for invalid format is included in the message
    }
}
