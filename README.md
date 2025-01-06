# CalculatorRestAPI
This project is a RESTful Calculator API that provides endpoints for performing basic mathematical operations: addition, subtraction, multiplication, and division. It uses Spring Boot to manage the REST and Calculator modules, which communicate with each other through Apache Kafka. The project supports operations on arbitrary precision decimal numbers, ensuring accurate calculations.

# Prerequisites
- Java (version 17 or higher)
- Maven
- Docker and Docker Compose
- Git

# Clone Repository
- git clone https://github.com/MarekJames/CalculatorRestAPI.git
- cd CalculatorRestAPI

# Build Project
- mvn clean install

# Start Docker and Kafka
- docker-compose up -d

# Run Rest Module
- cd rest
- mvn spring-boot:run

# Run Calculator Module
- cd calculator
- mvn spring-boot:run

# Test Sum
- curl "http://localhost:8081/sum?a=5&b=10.6" 

# Test Subtraction
- curl "http://localhost:8081/subtract?a=10&b=20"

# Test Multiplication
- curl "http://localhost:8081/multiply?a=1.3&b=20"

# Test Division
- curl "http://localhost:8081/divide?a=50&b=20"

