## WIP: gRPC Microservices with Spring Boot


A multi-service application where services communicate using gRPC.

Services:
1. Management
2. Inventory
3. Client
4. Order
5. Payment


## Work flow

```mermaid
flowchart TD
    Management[Management]
    Inventory[Inventory]
    Client[Client]

    Management -->|Registers| Client
    Management -->|Adds| Inventory
```

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
