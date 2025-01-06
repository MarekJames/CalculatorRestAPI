// Couldn't finish the test class
// When mocking the Rest call, the CalculatorController is waiting for the kafka message
// but it is never received since the Test class is waiting for the Rest response and the Main class waiting for the kafka message
// I get a deadlock

// package com.example;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.kafka.core.KafkaTemplate;
// import org.springframework.kafka.test.context.EmbeddedKafka;
// import org.springframework.test.web.servlet.MockMvc;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

// @SpringBootTest
// @AutoConfigureMockMvc
// @EmbeddedKafka(partitions = 1, topics = {"calculation-requests", "calculation-results"})
// public class CalculatorControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @Mock
//     private KafkaTemplate<String, String> kafkaTemplate;

//     @InjectMocks
//     private CalculatorController calculatorController;

//     @BeforeEach
//     public void setUp() {
//         // This can be used to initialize the necessary test setups
//     }

//     @Test
//     public void testSum() throws Exception {
//         // Arrange
//         double a = 5.0;
//         double b = 3.0;
//         String result = "8";
        
//         mockMvc.perform(get("/sum")
//                 .param("a", String.valueOf(a))
//                 .param("b", String.valueOf(b)));
        
//         // After the request completes, send the Kafka message.
//         String requestId2 = "1231231";
//         String message2 = String.join(",", requestId2, result);

//         // Send Kafka message using the mock Kafka template
//         kafkaTemplate.send("calculation-results", requestId2, message2);
//     }
// }
