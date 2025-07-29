package com.om.workflow.controller;

import com.om.workflow.dto.WorkflowRequest;
import com.om.workflow.model.Workflow;
import com.om.workflow.model.Task;
import com.om.workflow.model.WorkflowStatus;
import com.om.workflow.service.WorkflowService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/workflows")
@CrossOrigin(origins = "*")
public class WorkflowController {
    
    @Autowired
    private WorkflowService workflowService;
    
    @PostMapping
    public ResponseEntity<Workflow> createWorkflow(@Valid @RequestBody WorkflowRequest request) {
        try {
            Workflow workflow = workflowService.createWorkflow(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(workflow);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{workflowId}/deploy")
    public ResponseEntity<Workflow> deployWorkflow(@PathVariable Long workflowId) {
        try {
            Workflow workflow = workflowService.deployWorkflow(workflowId);
            return ResponseEntity.ok(workflow);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{workflowId}/start")
    public ResponseEntity<ProcessInstance> startWorkflow(@PathVariable Long workflowId) {
        try {
            ProcessInstance processInstance = workflowService.startWorkflow(workflowId);
            return ResponseEntity.ok(processInstance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/tasks/{taskId}/complete")
    public ResponseEntity<Void> completeTask(@PathVariable String taskId) {
        try {
            workflowService.completeTask(taskId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/instances/{processInstanceId}")
    public ResponseEntity<Void> endWorkflow(@PathVariable String processInstanceId) {
        try {
            workflowService.endWorkflow(processInstanceId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<Workflow>> getAllWorkflows() {
        List<Workflow> workflows = workflowService.getAllWorkflows();
        return ResponseEntity.ok(workflows);
    }
    
    @GetMapping("/{workflowId}")
    public ResponseEntity<Workflow> getWorkflowById(@PathVariable Long workflowId) {
        Optional<Workflow> workflow = workflowService.getWorkflowById(workflowId);
        return workflow.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Workflow>> getWorkflowsByStatus(@PathVariable WorkflowStatus status) {
        List<Workflow> workflows = workflowService.getWorkflowsByStatus(status);
        return ResponseEntity.ok(workflows);
    }
    
    @GetMapping("/{workflowId}/tasks")
    public ResponseEntity<List<Task>> getTasksByWorkflowId(@PathVariable Long workflowId) {
        List<Task> tasks = workflowService.getTasksByWorkflowId(workflowId);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/tasks/assignee/{assignee}")
    public ResponseEntity<List<Task>> getTasksByAssignee(@PathVariable String assignee) {
        List<Task> tasks = workflowService.getTasksByAssignee(assignee);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/tasks/overdue")
    public ResponseEntity<List<Task>> getOverdueTasks() {
        List<Task> tasks = workflowService.getOverdueTasks();
        return ResponseEntity.ok(tasks);
    }
}