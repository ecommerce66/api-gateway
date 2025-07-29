package com.workflow.controller;

import com.workflow.dto.WorkflowRequest;
import com.workflow.dto.WorkflowResponse;
import com.workflow.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workflows")
@RequiredArgsConstructor
@Slf4j
public class WorkflowController {
    
    private final WorkflowService workflowService;
    
    @PostMapping
    public ResponseEntity<WorkflowResponse> createWorkflow(@Valid @RequestBody WorkflowRequest request) {
        log.info("Creating workflow: {}", request.getName());
        WorkflowResponse response = workflowService.createWorkflow(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/{processDefinitionId}/start")
    public ResponseEntity<WorkflowResponse> startWorkflow(
            @PathVariable String processDefinitionId,
            @RequestParam(required = false) String businessKey,
            @RequestBody(required = false) Map<String, Object> variables) {
        
        log.info("Starting workflow with process definition ID: {}", processDefinitionId);
        WorkflowResponse response = workflowService.startWorkflow(processDefinitionId, businessKey, variables);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{processInstanceId}/complete")
    public ResponseEntity<WorkflowResponse> completeWorkflow(@PathVariable String processInstanceId) {
        log.info("Completing workflow with process instance ID: {}", processInstanceId);
        WorkflowResponse response = workflowService.completeWorkflow(processInstanceId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{processInstanceId}/suspend")
    public ResponseEntity<WorkflowResponse> suspendWorkflow(@PathVariable String processInstanceId) {
        log.info("Suspending workflow with process instance ID: {}", processInstanceId);
        WorkflowResponse response = workflowService.suspendWorkflow(processInstanceId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{processInstanceId}/activate")
    public ResponseEntity<WorkflowResponse> activateWorkflow(@PathVariable String processInstanceId) {
        log.info("Activating workflow with process instance ID: {}", processInstanceId);
        WorkflowResponse response = workflowService.activateWorkflow(processInstanceId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<WorkflowResponse>> getAllWorkflows() {
        log.info("Getting all workflows");
        List<WorkflowResponse> workflows = workflowService.getAllWorkflows();
        return ResponseEntity.ok(workflows);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<WorkflowResponse> getWorkflowById(@PathVariable Long id) {
        log.info("Getting workflow by ID: {}", id);
        return workflowService.getWorkflowById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/process-instance/{processInstanceId}")
    public ResponseEntity<WorkflowResponse> getWorkflowByProcessInstanceId(@PathVariable String processInstanceId) {
        log.info("Getting workflow by process instance ID: {}", processInstanceId);
        return workflowService.getWorkflowByProcessInstanceId(processInstanceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}