# saga-pattern-spring-boot-demo

## Overview
This project demonstrates the **SAGA Orchestration Design Pattern** implemented with **Spring Boot** and **Kafka**.  
It showcases how distributed transactions can be coordinated across multiple microservices using the saga pattern, ensuring data consistency without relying on a single distributed transaction manager.

The project also implements **compensating transactions** to handle failures gracefully and maintain system consistency.

---

## Key Concepts
- **Saga Pattern**: A sequence of local transactions where each transaction updates data within a single service and publishes events or messages to trigger the next transaction.
- **Orchestration**: A central coordinator (or orchestrator) controls the sequence of transactions and handles failures.
- **Compensating Transactions**: Special transactions that undo the effects of a previous transaction if something goes wrong.

---

## Features
- Demonstrates **SAGA orchestration** with Kafka events.
- Implements **compensating transactions** for rollback.
- Uses **Spring Boot** for service orchestration.
- Event-driven architecture.

---

## Architecture
The application involves:
1. Services publishing events to Kafka topics.
2. Saga Orchestrator consuming events and invoking subsequent services.
3. Compensating actions triggered on failure.

---

## Getting Started

### Prerequisites
- Kafka cluster
- Java 17+
- Maven
- Spring Boot

### Running the Application
1. Clone the repository:
   ```bash
   git clone <repo-url>
   cd saga-pattern-spring-boot-demo
