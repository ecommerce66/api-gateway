# Quick Start Guide

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- Docker and Docker Compose (optional)

## Option 1: Run Locally

### 1. Build the Application
```bash
./build.sh
```

### 2. Run the Application
```bash
java -jar target/flowable-workflow-1.0.0.jar
```

The application will start on `http://localhost:8080`

### 3. Access Points
- **Application**: http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:workflowdb`
  - Username: `sa`
  - Password: (empty)

## Option 2: Run with Docker

### 1. Build and Run with Docker Compose
```bash
docker-compose up --build
```

This will start:
- PostgreSQL database on port 5432
- Workflow application on port 8080

### 2. Access the Application
- **Application**: http://localhost:8080

## Testing the API

### 1. Create a Workflow
```bash
curl -X POST http://localhost:8080/api/workflows \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Document Approval",
    "description": "Simple document approval workflow",
    "key": "doc-approval",
    "version": "1.0",
    "category": "approval",
    "tenantId": "company-a",
    "startUserId": "initiator",
    "tasks": [
      {
        "name": "Review Document",
        "description": "Review the document",
        "taskDefinitionKey": "review",
        "assignee": "john.doe",
        "priority": 50
      },
      {
        "name": "Approve Document",
        "description": "Approve the document",
        "taskDefinitionKey": "approve",
        "assignee": "jane.smith",
        "priority": 75
      }
    ]
  }'
```

### 2. Run the Example Script
```bash
./examples/api-examples.sh
```

## API Endpoints

### Workflows
- `POST /api/workflows` - Create workflow
- `POST /api/workflows/{processDefinitionId}/start` - Start workflow
- `POST /api/workflows/{processInstanceId}/complete` - Complete workflow
- `GET /api/workflows` - Get all workflows
- `GET /api/workflows/{id}` - Get workflow by ID

### Tasks
- `POST /api/tasks/{taskId}/start` - Start task
- `POST /api/tasks/{taskId}/complete` - Complete task
- `POST /api/tasks/{taskId}/assign?assignee=user` - Assign task
- `GET /api/tasks` - Get all tasks
- `GET /api/tasks/assignee/{assignee}` - Get tasks by assignee

## Troubleshooting

### Common Issues

1. **Port 8080 already in use**
   - Change the port in `application.yml`
   - Or kill the process using port 8080

2. **Database connection issues**
   - Check H2 console at http://localhost:8080/h2-console
   - Verify database configuration in `application.yml`

3. **Build failures**
   - Ensure Java 11 is installed: `java -version`
   - Ensure Maven is installed: `mvn -version`
   - Clean and rebuild: `mvn clean install`

### Logs

Check application logs for detailed information:
```bash
tail -f logs/application.log
```

## Next Steps

1. **Explore the API**: Use the provided examples to test the API
2. **Customize workflows**: Modify the BPMN generation logic in `WorkflowService`
3. **Add authentication**: Implement security with Spring Security
4. **Add UI**: Create a web interface using React, Angular, or Vue.js
5. **Scale**: Deploy to Kubernetes or cloud platforms

## Support

For issues and questions:
1. Check the logs for error messages
2. Review the README.md for detailed documentation
3. Check the API examples in `examples/api-examples.sh`