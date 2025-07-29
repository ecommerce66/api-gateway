package com.workflow.service;

import com.workflow.dto.WorkflowRequest;
import com.workflow.dto.WorkflowResponse;
import com.workflow.entity.Workflow;
import com.workflow.repository.WorkflowRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class WorkflowServiceTest {

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private WorkflowRepository workflowRepository;

    @Test
    void testCreateWorkflow() {
        // Create a workflow request
        WorkflowRequest.TaskRequest task1 = WorkflowRequest.TaskRequest.builder()
                .name("Review Document")
                .description("Review the submitted document")
                .taskDefinitionKey("review-task")
                .assignee("john.doe")
                .priority(50)
                .category("review")
                .build();

        WorkflowRequest.TaskRequest task2 = WorkflowRequest.TaskRequest.builder()
                .name("Approve Document")
                .description("Approve the reviewed document")
                .taskDefinitionKey("approve-task")
                .assignee("jane.smith")
                .priority(75)
                .category("approval")
                .build();

        WorkflowRequest request = WorkflowRequest.builder()
                .name("Document Approval Process")
                .description("A workflow for document approval")
                .key("document-approval")
                .version("1.0")
                .category("approval")
                .tenantId("company-a")
                .businessKey("doc-123")
                .startUserId("initiator")
                .tasks(Arrays.asList(task1, task2))
                .variables(new HashMap<>())
                .build();

        // Create the workflow
        WorkflowResponse response = workflowService.createWorkflow(request);

        // Verify the response
        assertNotNull(response);
        assertEquals("Document Approval Process", response.getName());
        assertEquals("document-approval", response.getKey());
        assertEquals("1.0", response.getVersion());
        assertEquals("approval", response.getCategory());
        assertEquals("company-a", response.getTenantId());
        assertEquals(Workflow.WorkflowStatus.ACTIVE, response.getStatus());
        assertNotNull(response.getProcessDefinitionId());

        // Verify the workflow was saved in the database
        assertTrue(workflowRepository.findByProcessDefinitionId(response.getProcessDefinitionId()).isPresent());
    }

    @Test
    void testStartWorkflow() {
        // First create a workflow
        WorkflowRequest.TaskRequest task = WorkflowRequest.TaskRequest.builder()
                .name("Test Task")
                .description("A test task")
                .taskDefinitionKey("test-task")
                .assignee("test.user")
                .priority(50)
                .build();

        WorkflowRequest request = WorkflowRequest.builder()
                .name("Test Workflow")
                .description("A test workflow")
                .key("test-workflow")
                .version("1.0")
                .category("test")
                .tenantId("test-tenant")
                .startUserId("test-user")
                .tasks(Arrays.asList(task))
                .variables(new HashMap<>())
                .build();

        WorkflowResponse createdWorkflow = workflowService.createWorkflow(request);

        // Start the workflow
        Map<String, Object> variables = new HashMap<>();
        variables.put("testVariable", "testValue");
        
        WorkflowResponse startedWorkflow = workflowService.startWorkflow(
                createdWorkflow.getProcessDefinitionId(), 
                "test-business-key", 
                variables
        );

        // Verify the workflow was started
        assertNotNull(startedWorkflow);
        assertNotNull(startedWorkflow.getProcessInstanceId());
        assertEquals(Workflow.WorkflowStatus.ACTIVE, startedWorkflow.getStatus());
        assertNotNull(startedWorkflow.getStartTime());
    }

    @Test
    void testGetAllWorkflows() {
        // Create multiple workflows
        for (int i = 1; i <= 3; i++) {
            WorkflowRequest.TaskRequest task = WorkflowRequest.TaskRequest.builder()
                    .name("Task " + i)
                    .description("Task description " + i)
                    .taskDefinitionKey("task-" + i)
                    .assignee("user" + i)
                    .priority(50)
                    .build();

            WorkflowRequest request = WorkflowRequest.builder()
                    .name("Workflow " + i)
                    .description("Workflow description " + i)
                    .key("workflow-" + i)
                    .version("1.0")
                    .category("test")
                    .tenantId("test-tenant")
                    .startUserId("test-user")
                    .tasks(Arrays.asList(task))
                    .variables(new HashMap<>())
                    .build();

            workflowService.createWorkflow(request);
        }

        // Get all workflows
        var workflows = workflowService.getAllWorkflows();

        // Verify we have at least 3 workflows
        assertTrue(workflows.size() >= 3);
    }
}