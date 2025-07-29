#!/bin/bash

echo "Testing Workflow API..."

# Wait for application to start
sleep 10

# Test health endpoint
echo "Testing health endpoint..."
curl -s http://localhost:8080/actuator/health

echo -e "\n\nTesting workflow endpoints..."

# Test GET workflows
echo "GET /api/workflows"
curl -s -X GET http://localhost:8080/api/workflows

echo -e "\n\nTesting workflow creation..."
# Test POST workflow
curl -s -X POST http://localhost:8080/api/workflows \
  -H "Content-Type: application/json" \
  -d '{
    "processDefinitionKey": "test-process",
    "processDefinitionName": "Test Process",
    "description": "A test workflow",
    "category": "test",
    "tenantId": "test-tenant",
    "versionTag": "1.0",
    "historyLevel": 3,
    "tasks": [
      {
        "name": "First Task",
        "description": "Complete the first task",
        "taskDefinitionKey": "task1",
        "formKey": "task1-form",
        "priority": 1,
        "dueDate": "2024-12-31T23:59:59",
        "category": "test",
        "tenantId": "test-tenant",
        "assignee": "user1",
        "owner": "user1",
        "candidateUsers": "user1,user2",
        "candidateGroups": "group1,group2",
        "variables": {
          "var1": "value1",
          "var2": "value2"
        }
      }
    ]
  }'

echo -e "\n\nTest completed!"