# startup.sh - Development startup script
#!/bin/bash

echo "Starting Auth Backend Development Environment..."

# Start PostgreSQL with Docker Compose
echo "Starting PostgreSQL..."
docker-compose up -d postgres

# Wait for PostgreSQL to be ready
echo "Waiting for PostgreSQL to be ready..."
sleep 10

# Run the Spring Boot application
echo "Starting Spring Boot application..."
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev