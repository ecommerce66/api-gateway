#!/bin/bash

# Flowable Workflow Management System - API Examples
# Make sure the application is running on http://localhost:8080

BASE_URL="http://localhost:8080/api"

echo "=== Flowable Workflow Management System - API Examples ==="
echo ""

# 1. Create a workflow
echo "1. Creating a workflow..."
WORKFLOW_RESPONSE=$(curl -s -X POST "$BASE_URL/workflows" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Document Approval Process",
    "description": "A workflow for document approval",
    "key": "document-approval",
    "version": "1.0",
    "category": "approval",
    "tenantId": "company-a",
    "businessKey": "doc-123",
    "startUserId": "initiator",
    "tasks": [
      {
        "name": "Review Document",
        "description": "Review the submitted document",
        "taskDefinitionKey": "review-task",
        "assignee": "john.doe",
        "priority": 50,
        "category": "review"
      },
      {
        "name": "Approve Document",
        "description": "Approve the reviewed document",
        "taskDefinitionKey": "approve-task",
        "assignee": "jane.smith",
        "priority": 75,
        "category": "approval"
      }
    ],
    "variables": {
      "documentType": "contract",
      "urgency": "high"
    }
  }')

echo "Workflow created: $WORKFLOW_RESPONSE"
echo ""

# Extract process definition ID from response (you might need to adjust this based on actual response format)
PROCESS_DEFINITION_ID=$(echo $WORKFLOW_RESPONSE | grep -o '"processDefinitionId":"[^"]*"' | cut -d'"' -f4)

if [ -n "$PROCESS_DEFINITION_ID" ]; then
    echo "Process Definition ID: $PROCESS_DEFINITION_ID"
    echo ""

    # 2. Start the workflow
    echo "2. Starting the workflow..."
    START_RESPONSE=$(curl -s -X POST "$BASE_URL/workflows/$PROCESS_DEFINITION_ID/start?businessKey=doc-123" \
      -H "Content-Type: application/json" \
      -d '{
        "documentId": "doc-123",
        "initiator": "john.doe"
      }')

    echo "Workflow started: $START_RESPONSE"
    echo ""

    # Extract process instance ID
    PROCESS_INSTANCE_ID=$(echo $START_RESPONSE | grep -o '"processInstanceId":"[^"]*"' | cut -d'"' -f4)

    if [ -n "$PROCESS_INSTANCE_ID" ]; then
        echo "Process Instance ID: $PROCESS_INSTANCE_ID"
        echo ""

        # 3. Get tasks for the process instance
        echo "3. Getting tasks for the process instance..."
        TASKS_RESPONSE=$(curl -s -X GET "$BASE_URL/tasks/process-instance/$PROCESS_INSTANCE_ID")
        echo "Tasks: $TASKS_RESPONSE"
        echo ""

        # Extract task ID (assuming first task)
        TASK_ID=$(echo $TASKS_RESPONSE | grep -o '"taskId":"[^"]*"' | head -1 | cut -d'"' -f4)

        if [ -n "$TASK_ID" ]; then
            echo "Task ID: $TASK_ID"
            echo ""

            # 4. Start the task
            echo "4. Starting the task..."
            START_TASK_RESPONSE=$(curl -s -X POST "$BASE_URL/tasks/$TASK_ID/start")
            echo "Task started: $START_TASK_RESPONSE"
            echo ""

            # 5. Complete the task
            echo "5. Completing the task..."
            COMPLETE_TASK_RESPONSE=$(curl -s -X POST "$BASE_URL/tasks/$TASK_ID/complete" \
              -H "Content-Type: application/json" \
              -d '{
                "approved": true,
                "comments": "Document looks good"
              }')
            echo "Task completed: $COMPLETE_TASK_RESPONSE"
            echo ""

            # 6. Complete the workflow
            echo "6. Completing the workflow..."
            COMPLETE_WORKFLOW_RESPONSE=$(curl -s -X POST "$BASE_URL/workflows/$PROCESS_INSTANCE_ID/complete")
            echo "Workflow completed: $COMPLETE_WORKFLOW_RESPONSE"
            echo ""
        fi
    fi
fi

# 7. Get all workflows
echo "7. Getting all workflows..."
ALL_WORKFLOWS=$(curl -s -X GET "$BASE_URL/workflows")
echo "All workflows: $ALL_WORKFLOWS"
echo ""

# 8. Get all tasks
echo "8. Getting all tasks..."
ALL_TASKS=$(curl -s -X GET "$BASE_URL/tasks")
echo "All tasks: $ALL_TASKS"
echo ""

echo "=== API Examples completed ==="