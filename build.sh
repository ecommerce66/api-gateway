#!/bin/bash

echo "=== Building Flowable Workflow Management System ==="

# Clean and compile
echo "Cleaning and compiling..."
mvn clean compile

# Run tests
echo "Running tests..."
mvn test

# Package the application
echo "Packaging the application..."
mvn package -DskipTests

echo "=== Build completed ==="
echo "JAR file created at: target/flowable-workflow-1.0.0.jar"
echo ""
echo "To run the application:"
echo "  java -jar target/flowable-workflow-1.0.0.jar"
echo ""
echo "To run with Docker:"
echo "  docker-compose up --build"