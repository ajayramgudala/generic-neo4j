# Generic Neo4j Document Graph (Spring Boot)

This project is a Spring Boot application using Neo4j, where:
- Each node is a document with flexible fields.
- Each relationship (edge) can have its own properties (also document-like).

## Features
- Create, read, update, delete nodes with arbitrary fields.
- Create relationships between nodes with arbitrary properties.
- REST API for managing nodes and relationships.

## Requirements
- Java 21
- Maven
- Neo4j (running locally or remotely)

## Setup
1. **Configure Neo4j connection:**
   Edit `src/main/resources/application.properties` with your Neo4j URI, username, and password.

2. **Build and run:**
   ```sh
   mvn spring-boot:run
   ```

## API Usage

### Nodes
- `POST /nodes` — Create a node
- `GET /nodes/{id}` — Get node by ID
- `GET /nodes` — List all nodes
- `PUT /nodes/{id}` — Update node
- `DELETE /nodes/{id}` — Delete node

Example node JSON:
```json
{
  "fields": {
    "title": "Document 1",
    "type": "invoice",
    "amount": 100
  }
}
```

### Relationships
- `POST /relationships?fromId={fromId}&toId={toId}` — Create relationship with properties
- `GET /relationships` — List all relationships

Example relationship JSON:
```json
{
  "status": "active",
  "created": "2024-06-01"
}
```

## Notes
- This is a generic/flexible model. You can add any fields to nodes or relationships.
- For production, secure the API and handle errors more robustly.