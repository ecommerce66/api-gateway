# Flowable Workflow Management System

A Spring Boot application with Flowable workflow engine that allows users to create, manage, and execute workflows with any number of tasks. The application is built with Java 11 and follows BPMN 2.0 specifications.

## Features

- **Dynamic Workflow Creation**: Create workflows with any number of tasks
- **BPMN 2.0 Compliance**: All workflows and tasks follow BPMN 2.0 specifications
- **Task Management**: Start, complete, assign, claim, and manage individual tasks
- **Workflow Lifecycle**: Start, suspend, activate, and complete workflows
- **RESTful API**: Complete REST API for all workflow and task operations
- **H2 Database**: In-memory database for development and testing

## Technology Stack

- **Java**: 11
- **Spring Boot**: 2.7.18
- **Flowable**: 6.7.2
- **Spring Data JPA**: For data persistence
- **H2 Database**: In-memory database
- **Lombok**: For reducing boilerplate code
- **Maven**: Build tool

## Project Structure

```
src/
├── main/
│   ├── java/com/workflow/
│   │   ├── WorkflowApplication.java
│   │   ├── controller/
│   │   │   ├── WorkflowController.java
│   │   │   └── TaskController.java
│   │   ├── service/
│   │   │   ├── WorkflowService.java
│   │   │   └── TaskService.java
│   │   ├── repository/
│   │   │   ├── WorkflowRepository.java
│   │   │   └── TaskRepository.java
│   │   ├── entity/
│   │   │   ├── Workflow.java
│   │   │   └── Task.java
│   │   └── dto/
│   │       ├── WorkflowRequest.java
│   │       ├── WorkflowResponse.java
│   │       └── TaskResponse.java
│   └── resources/
│       ├── application.yml
│       └── processes/
│           └── sample-process.bpmn20.xml
```

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

### Running the Application

1. **Clone the repository** (if applicable)
2. **Navigate to the project directory**
3. **Build the project**:
   ```bash
   mvn clean install
   ```
4. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

### Access Points

- **Application**: http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:workflowdb`
  - Username: `sa`
  - Password: (empty)

## API Documentation

### Workflow Management

#### Create Workflow
```http
POST /api/workflows
Content-Type: application/json

{
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
}
```

#### Start Workflow
```http
POST /api/workflows/{processDefinitionId}/start?businessKey=doc-123
Content-Type: application/json

{
  "documentId": "doc-123",
  "initiator": "john.doe"
}
```

#### Complete Workflow
```http
POST /api/workflows/{processInstanceId}/complete
```

#### Suspend Workflow
```http
POST /api/workflows/{processInstanceId}/suspend
```

#### Activate Workflow
```http
POST /api/workflows/{processInstanceId}/activate
```

#### Get All Workflows
```http
GET /api/workflows
```

#### Get Workflow by ID
```http
GET /api/workflows/{id}
```

### Task Management

#### Start Task
```http
POST /api/tasks/{taskId}/start
```

#### Complete Task
```http
POST /api/tasks/{taskId}/complete
Content-Type: application/json

{
  "approved": true,
  "comments": "Document looks good"
}
```

#### Assign Task
```http
POST /api/tasks/{taskId}/assign?assignee=jane.smith
```

#### Claim Task
```http
POST /api/tasks/{taskId}/claim?userId=jane.smith
```

#### Unclaim Task
```http
POST /api/tasks/{taskId}/unclaim
```

#### Update Task Variables
```http
POST /api/tasks/{taskId}/variables
Content-Type: application/json

{
  "priority": "high",
  "dueDate": "2024-01-15T10:00:00"
}
```

#### Set Task Priority
```http
POST /api/tasks/{taskId}/priority?priority=75
```

#### Set Task Due Date
```http
POST /api/tasks/{taskId}/due-date?dueDate=2024-01-15T10:00:00
```

#### Get All Tasks
```http
GET /api/tasks
```

#### Get Tasks by Process Instance
```http
GET /api/tasks/process-instance/{processInstanceId}
```

#### Get Tasks by Assignee
```http
GET /api/tasks/assignee/{assignee}
```

#### Get Tasks by Status
```http
GET /api/tasks/status/{status}
```

## BPMN 2.0 Fields

### Workflow Fields
- `processDefinitionId`: Unique identifier for the process definition
- `processInstanceId`: Unique identifier for the process instance
- `name`: Human-readable name of the workflow
- `description`: Detailed description of the workflow
- `version`: Version of the process definition
- `category`: Category classification
- `key`: Business key for the process
- `deploymentId`: ID of the deployment
- `resourceName`: Name of the BPMN resource file
- `diagramResourceName`: Name of the diagram resource file
- `tenantId`: Tenant identifier for multi-tenancy
- `status`: Current status (ACTIVE, SUSPENDED, COMPLETED, TERMINATED, DELETED)
- `startTime`: When the workflow was started
- `endTime`: When the workflow was completed
- `duration`: Duration in milliseconds
- `startUserId`: User who started the workflow
- `businessKey`: Business key for the process instance
- `parentProcessInstanceId`: Parent process instance ID
- `superProcessInstanceId`: Super process instance ID
- `rootProcessInstanceId`: Root process instance ID
- `deleteReason`: Reason for deletion

### Task Fields
- `taskId`: Unique identifier for the task
- `name`: Human-readable name of the task
- `description`: Detailed description of the task
- `taskDefinitionKey`: Key from the BPMN definition
- `processInstanceId`: Associated process instance ID
- `executionId`: Execution ID
- `processDefinitionId`: Process definition ID
- `assignee`: User assigned to the task
- `owner`: Owner of the task
- `priority`: Priority level
- `category`: Task category
- `formKey`: Form key for UI integration
- `parentTaskId`: Parent task ID
- `tenantId`: Tenant identifier
- `status`: Current status (CREATED, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED, SUSPENDED)
- `startTime`: When the task was started
- `endTime`: When the task was completed
- `duration`: Duration in milliseconds
- `created`: When the task was created
- `dueDate`: Due date for the task
- `claimTime`: When the task was claimed
- `workTimeInMillis`: Work time in milliseconds
- `variables`: Task variables

## Configuration

The application uses the following configuration in `application.yml`:

- **Database**: H2 in-memory database
- **Flowable**: Full history level, async executor enabled
- **Server**: Port 8080
- **Logging**: Debug level for Flowable and application packages

## Development

### Adding New Features

1. **New Entity**: Add entity class in `entity/` package
2. **Repository**: Add repository interface in `repository/` package
3. **Service**: Add service class in `service/` package
4. **Controller**: Add REST controller in `controller/` package
5. **DTOs**: Add request/response DTOs in `dto/` package

### Testing

The application includes comprehensive unit tests and integration tests. Run tests with:

```bash
mvn test
```

### Building for Production

```bash
mvn clean package
```

This creates a JAR file that can be deployed to any Java 11+ environment.

## Troubleshooting

### Common Issues

1. **Port Already in Use**: Change the port in `application.yml`
2. **Database Connection**: Check H2 console at http://localhost:8080/h2-console
3. **Flowable Issues**: Check logs for detailed error messages

### Logs

The application logs are available at different levels:
- `DEBUG`: Detailed Flowable and application logs
- `INFO`: General application information
- `ERROR`: Error messages

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License.