# Spring Boot Workflow Engine with Flowable

A comprehensive Spring Boot application that implements a workflow engine using Flowable, supporting BPMN 2.0 compliant workflows and tasks.

## Features

- **Workflow Management**: Create, deploy, start, and end workflows
- **Task Management**: Create tasks with any number of fields as per BPMN 2.0
- **BPMN 2.0 Compliance**: Full support for BPMN 2.0 specifications
- **REST API**: Complete REST API for workflow and task operations
- **Database Integration**: JPA/Hibernate with H2 database (configurable for production)
- **Java 11**: Built with Java 11 as requested

## Technology Stack

- **Java**: 11
- **Spring Boot**: 3.4.0
- **Flowable**: 7.0.0
- **Spring Data JPA**: For database operations
- **H2 Database**: In-memory database (can be changed to PostgreSQL, MySQL, etc.)
- **Maven**: Build tool

## Project Structure

```
src/main/java/com/om/workflow/
├── WorkflowApplication.java          # Main application class
├── config/
│   └── FlowableConfig.java          # Flowable engine configuration
├── controller/
│   └── WorkflowController.java      # REST API endpoints
├── dto/
│   ├── WorkflowRequest.java         # DTO for workflow creation
│   └── TaskRequest.java            # DTO for task creation
├── model/
│   ├── Workflow.java               # Workflow entity
│   ├── Task.java                   # Task entity
│   ├── WorkflowStatus.java         # Workflow status enum
│   ├── TaskStatus.java             # Task status enum
│   └── DelegationState.java        # Delegation state enum
├── repository/
│   ├── WorkflowRepository.java     # Workflow data access
│   └── TaskRepository.java         # Task data access
└── service/
    └── WorkflowService.java        # Business logic and Flowable integration
```

## BPMN 2.0 Fields Supported

### Workflow Fields
- Process Definition Key
- Process Definition Name
- Process Definition Version
- Process Definition ID
- Deployment ID
- Resource Name
- Diagram Resource Name
- Description
- Category
- Tenant ID
- Version Tag
- History Level
- Suspension State
- Identity Links Initialized
- Created Date
- Last Modified Date
- Status

### Task Fields
- Name
- Description
- Task Definition Key
- Process Instance ID
- Execution ID
- Process Definition ID
- Task ID
- Parent Task ID
- Form Key
- Priority
- Due Date
- Category
- Tenant ID
- Assignee
- Owner
- Delegation State
- Candidate Users
- Candidate Groups
- Claim Time
- Create Time
- Last Updated
- Suspension State
- Status
- Variables (Map)

## API Endpoints

### Workflow Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/workflows` | Create a new workflow |
| GET | `/api/workflows` | Get all workflows |
| GET | `/api/workflows/{id}` | Get workflow by ID |
| GET | `/api/workflows/status/{status}` | Get workflows by status |
| POST | `/api/workflows/{id}/deploy` | Deploy workflow to Flowable engine |
| POST | `/api/workflows/{id}/start` | Start workflow instance |
| DELETE | `/api/workflows/instances/{processInstanceId}` | End workflow instance |

### Task Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/workflows/{workflowId}/tasks` | Get tasks by workflow ID |
| GET | `/api/workflows/tasks/assignee/{assignee}` | Get tasks by assignee |
| GET | `/api/workflows/tasks/overdue` | Get overdue tasks |
| POST | `/api/workflows/tasks/{taskId}/complete` | Complete a task |

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

### Running the Application

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd workflow-engine
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**
   - Application: http://localhost:8080
   - H2 Console: http://localhost:8080/h2-console
   - API Documentation: Available through Swagger UI

### Sample Usage

#### 1. Create a Workflow

```bash
curl -X POST http://localhost:8080/api/workflows \
  -H "Content-Type: application/json" \
  -d '{
    "processDefinitionKey": "sample-process",
    "processDefinitionName": "Sample Process",
    "description": "A sample workflow process",
    "category": "sample",
    "tasks": [
      {
        "name": "First Task",
        "description": "Complete the first task",
        "taskDefinitionKey": "task1",
        "assignee": "user1",
        "priority": 50
      },
      {
        "name": "Second Task",
        "description": "Complete the second task",
        "taskDefinitionKey": "task2",
        "assignee": "user2",
        "priority": 30
      }
    ]
  }'
```

#### 2. Deploy the Workflow

```bash
curl -X POST http://localhost:8080/api/workflows/1/deploy
```

#### 3. Start the Workflow

```bash
curl -X POST http://localhost:8080/api/workflows/1/start
```

#### 4. Complete a Task

```bash
curl -X POST http://localhost:8080/api/workflows/tasks/{taskId}/complete
```

#### 5. Get All Workflows

```bash
curl -X GET http://localhost:8080/api/workflows
```

## Configuration

The application uses the following configuration in `application.yml`:

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:mem:workflowdb
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true

flowable:
  database-schema-update: true
  async-executor-activate: true
  history-level: full
  db-identity-used: true
  check-process-definitions: true
  deployment-mode: never-fail
  enable-safe-xml: true
```

## Database Configuration

The application uses H2 in-memory database by default. For production, you can configure other databases:

### PostgreSQL
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/workflowdb
    driver-class-name: org.postgresql.Driver
    username: postgres
    password: password
```

### MySQL
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/workflowdb
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: password
```

## BPMN Process Files

BPMN process definitions should be placed in `src/main/resources/processes/` directory. The application includes a sample process file `sample-process.bpmn20.xml`.

## Workflow Lifecycle

1. **Draft**: Workflow is created but not deployed
2. **Active**: Workflow is deployed and ready to start
3. **Running**: Workflow instance is started and executing
4. **Completed**: Workflow instance has finished
5. **Suspended**: Workflow is temporarily suspended
6. **Terminated**: Workflow instance was terminated

## Task Lifecycle

1. **Created**: Task is created but not assigned
2. **Assigned**: Task is assigned to a user
3. **In Progress**: Task is being worked on
4. **Completed**: Task is finished
5. **Cancelled**: Task was cancelled
6. **Suspended**: Task is temporarily suspended

## Error Handling

The application includes comprehensive error handling:
- Validation errors for required fields
- Business logic validation
- Flowable engine integration errors
- Database operation errors

## Monitoring and Logging

- Application logs are configured to show Flowable engine operations
- H2 console available for database inspection
- Actuator endpoints for health checks and metrics

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License.