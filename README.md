# Elevator Management System (Kafka-based) - Demo

This is a runnable Spring Boot project implementing an Elevator Management System with:
- REST API (Spring Boot)
- H2 Database (dev)
- Redis caching
- Kafka for asynchronous simulation
- JWT-based auth (demo hardcoded users)
- Resilience4j on classpath (example ready)
- Swagger/OpenAPI UI

## Quick start (dev, without Docker)
1. Build: `./mvnw clean package -DskipTests` (or `mvn clean package`)
2. Run: `mvn spring-boot:run`
3. Open Swagger UI: `http://localhost:8080/swagger-ui.html` (springdoc)
4. Auth:
   - admin: `admin` / `adminpass`
   - passenger: `user` / `userpass`
   - Call `POST /api/auth/login` to get JWT token.

## With Docker Compose (recommended)
1. `docker-compose up --build`
2. Wait for services to start (Kafka, Redis).
3. App available at `http://localhost:8080`

## Endpoints
- POST /api/elevators/request
- GET /api/elevators/status
- PUT /api/elevators/{id}/assign  (ADMIN)
- POST /api/elevators/simulate
- GET /api/elevators/logs?page=0&size=10
- PUT /api/elevators/{id}/repair   (ADMIN)
- GET /api/elevators/optimize

## Postman
A sample Postman collection is included at `postman_collection.json`.

