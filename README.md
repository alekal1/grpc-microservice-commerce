## WIP: gRPC Microservice Commerce Platform


A modular commerce platform built with Spring Boot,
gRPC and Protocol Buffers


## System Architecture

The system is composed of five core microservices:

* Management - Administrative and onboarding operations.
* Inventory - Stock levels and item availability.
* Client - Order start and top-up balance
* Order - Coordinates multi-step order workflow
* Payment - Processes and validates payments.

#### Helper modules

1. common-proto - For storing common proto files, that are in use across all application
2. coverage-report - One file module that collect test code coverage

## Module Structure
Each module consist of two sub-modules:

* The gRPC API definitions (-api suffix)
* The service implementation (-impl suffix)

## Service communication

#### Administrative flow
```mermaid
flowchart TD
    Management[Management]
    Inventory[Inventory]
    Client[Client]

    Management -->|Registers| Client
    Management -->|Adds| Inventory
```

#### Order Processing flow
```mermaid
flowchart TD
    Client[Client]
    Inventory[Inventory]
    Order[Order]
    Payment[Payment]

    Client -->|1. Makes Request| Order
    
    Order -->|2. Check Availability| Inventory
    Inventory -->|3. Availability Response| Order

    Order -->|4. Inventory Available| Payment
    Payment -->|5. Payment Response| Order
    Order -->|6. Respond to| Client

    Order -.->|No stock or insufficient money| Client
```

## Getting started

### Prerequisites
* Java 17
* Maven
* Docker + Docker compose

### Build the project 
```aiignore
mvn clean install
```

### Run the System
```aiignore
docker compose --env-file .env.dev up --build -d
```
