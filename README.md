# Spring Boot Flowable Workflow Management Application

A comprehensive Spring Boot application with Flowable for managing workflows and tasks with full BPMN 2.0 support.

## Features

- **Workflow Definition Management**: Create, update, activate, suspend, and delete workflow definitions
- **Task Definition Management**: Define tasks with all BPMN 2.0 properties including user tasks, service tasks, gateways, etc.
- **Workflow Instance Management**: Start, suspend, resume, terminate workflow instances
- **Task Instance Management**: Claim, start, complete, delegate, and assign tasks
- **BPMN 2.0 Compliance**: Full support for BPMN 2.0 elements and properties
- **Variable Management**: Support for workflow and task variables
- **RESTful APIs**: Complete REST API for all operations
- **Database Integration**: JPA/Hibernate with H2 database (easily configurable for other databases)

## Technology Stack

- **Java 11**
- **Spring Boot 2.7.14**
- **Flowable 6.8.0**
- **Spring Data JPA**
- **H2 Database** (in-memory)
- **Maven**

## Project Structure

```
src/main/java/com/example/flowableworkflow/
├── entity/                     # JPA entities
│   ├── WorkflowDefinition.java # Workflow definition entity
│   ├── TaskDefinition.java     # Task definition entity
│   ├── WorkflowInstance.java   # Workflow instance entity
│   ├── TaskInstance.java       # Task instance entity
│   └── *Status.java           # Status enums
├── repository/                 # JPA repositories
├── service/                   # Business services
├── controller/                # REST controllers
└── FlowableWorkflowApplication.java # Main application

src/main/resources/
├── application.yml            # Application configuration
└── processes/                 # BPMN process definitions
    └── sample-workflow.bpmn20.xml
```

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

### Running the Application

1. **Clone the repository** (if applicable) or ensure all files are in place

2. **Build the application**:
   ```bash
   mvn clean compile
   ```

3. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**:
   - API Base URL: `http://localhost:8080/api`
   - H2 Console: `http://localhost:8080/api/h2-console`
   - Actuator Health: `http://localhost:8080/api/actuator/health`

## API Documentation

### Workflow Definitions

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/workflows` | Create a new workflow definition |
| GET | `/workflows` | Get all workflow definitions |
| GET | `/workflows/{id}` | Get workflow definition by ID |
| PUT | `/workflows/{id}` | Update workflow definition |
| DELETE | `/workflows/{id}` | Delete workflow definition |
| POST | `/workflows/{id}/activate` | Activate workflow definition |
| POST | `/workflows/{id}/suspend` | Suspend workflow definition |

### Task Definitions

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/task-definitions` | Create a new task definition |
| GET | `/task-definitions/{id}` | Get task definition by ID |
| PUT | `/task-definitions/{id}` | Update task definition |
| DELETE | `/task-definitions/{id}` | Delete task definition |
| GET | `/task-definitions/workflow/{workflowId}` | Get tasks for a workflow |

### Workflow Instances

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/workflow-instances/start` | Start a new workflow instance |
| GET | `/workflow-instances` | Get all workflow instances |
| GET | `/workflow-instances/{id}` | Get workflow instance by ID |
| POST | `/workflow-instances/{id}/suspend` | Suspend workflow instance |
| POST | `/workflow-instances/{id}/resume` | Resume workflow instance |
| POST | `/workflow-instances/{id}/terminate` | Terminate workflow instance |

### Task Instances

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/task-instances/{id}` | Get task instance by ID |
| POST | `/task-instances/{id}/claim` | Claim a task |
| POST | `/task-instances/{id}/start` | Start a task |
| POST | `/task-instances/{id}/complete` | Complete a task |
| POST | `/task-instances/{id}/delegate` | Delegate a task |
| GET | `/task-instances/assignee/{assignee}` | Get tasks by assignee |

## BPMN 2.0 Support

The application supports all major BPMN 2.0 elements:

### Task Types
- **User Task**: Human tasks requiring user interaction
- **Service Task**: Automated tasks executed by the system
- **Script Task**: Tasks executing scripts
- **Manual Task**: Tasks performed outside the system
- **Business Rule Task**: Tasks executing business rules

### Gateways
- **Exclusive Gateway**: XOR decision points
- **Parallel Gateway**: AND split/join points
- **Inclusive Gateway**: OR decision points

### Events
- **Start Event**: Process initiation
- **End Event**: Process completion
- **Intermediate Events**: Events during process execution
- **Boundary Events**: Events attached to activities

### Task Properties
- Assignee
- Candidate Users
- Candidate Groups
- Priority
- Due Date
- Form Key
- Documentation
- Execution Listeners
- Task Listeners
- Input/Output Parameters

## Sample Usage

### 1. Create a Workflow Definition

```bash
curl -X POST http://localhost:8080/api/workflows \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Document Approval",
    "description": "Document approval workflow",
    "processDefinitionKey": "documentApproval",
    "category": "HR",
    "createdBy": "admin"
  }'
```

### 2. Add Task Definitions

```bash
curl -X POST http://localhost:8080/api/task-definitions \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Review Document",
    "taskDefinitionKey": "reviewDocument",
    "taskType": "USER_TASK",
    "assignee": "reviewer",
    "priority": 50,
    "sequenceNumber": 1,
    "workflowDefinition": {"id": 1}
  }'
```

### 3. Activate Workflow

```bash
curl -X POST http://localhost:8080/api/workflows/1/activate
```

### 4. Start Workflow Instance

```bash
curl -X POST "http://localhost:8080/api/workflow-instances/start?workflowDefinitionId=1&startedBy=user1" \
  -H "Content-Type: application/json" \
  -d '{
    "documentId": "DOC-001",
    "requestedBy": "john.doe"
  }'
```

### 5. Complete Tasks

```bash
# Claim task
curl -X POST "http://localhost:8080/api/task-instances/1/claim?assignee=reviewer1"

# Start task
curl -X POST http://localhost:8080/api/task-instances/1/start

# Complete task
curl -X POST http://localhost:8080/api/task-instances/1/complete \
  -H "Content-Type: application/json" \
  -d '{
    "approved": true,
    "comments": "Document approved"
  }'
```

## Configuration

### Database Configuration

The application uses H2 in-memory database by default. To use a different database, update `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/flowable
    driver-class-name: org.postgresql.Driver
    username: flowable
    password: flowable
```

### Flowable Configuration

Key Flowable properties in `application.yml`:

```yaml
flowable:
  async-executor-activate: false    # Disable async executor for development
  database-schema-update: true      # Auto-update database schema
  check-process-definitions: false  # Don't auto-deploy processes
```

## Development

### Adding Custom Service Tasks

Create a Java delegate class:

```java
@Component
public class CustomServiceDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        // Custom logic here
        execution.setVariable("result", "completed");
    }
}
```

### Adding Process Variables

Variables can be set at workflow and task levels:

```java
// Workflow level
workflowInstanceService.setWorkflowInstanceVariable(instanceId, "status", "processing");

// Task level
taskInstanceService.setTaskInstanceVariable(taskId, "decision", "approved");
```

## Testing

The application includes comprehensive service and repository layers that can be tested using:

```bash
mvn test
```

## Production Considerations

1. **Database**: Replace H2 with a production database (PostgreSQL, MySQL, etc.)
2. **Security**: Add authentication and authorization
3. **Logging**: Configure appropriate logging levels
4. **Monitoring**: Enable additional actuator endpoints
5. **Performance**: Tune Flowable async executor settings
6. **Clustering**: Configure for multi-instance deployment

## Contributing

1. Follow Java coding standards
2. Add tests for new features
3. Update documentation
4. Use meaningful commit messages

## License

This project is available under the MIT License.