package com.example.flowableworkflow.controller;

import com.example.flowableworkflow.entity.WorkflowInstance;
import com.example.flowableworkflow.entity.WorkflowInstanceStatus;
import com.example.flowableworkflow.service.WorkflowInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/workflow-instances")
@CrossOrigin(origins = "*")
public class WorkflowInstanceController {
    
    @Autowired
    private WorkflowInstanceService workflowInstanceService;
    
    @PostMapping("/start")
    public ResponseEntity<WorkflowInstance> startWorkflowInstance(
            @RequestParam Long workflowDefinitionId,
            @RequestParam String startedBy,
            @RequestParam(required = false) String businessKey,
            @RequestBody(required = false) Map<String, Object> variables) {
        try {
            WorkflowInstance started = workflowInstanceService.startWorkflowInstance(
                    workflowDefinitionId, startedBy, businessKey, variables);
            return new ResponseEntity<>(started, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping
    public ResponseEntity<List<WorkflowInstance>> getAllWorkflowInstances() {
        List<WorkflowInstance> instances = workflowInstanceService.getAllWorkflowInstances();
        return new ResponseEntity<>(instances, HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<WorkflowInstance> getWorkflowInstanceById(@PathVariable Long id) {
        Optional<WorkflowInstance> instance = workflowInstanceService.getWorkflowInstanceById(id);
        return instance.map(i -> new ResponseEntity<>(i, HttpStatus.OK))
                      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/process-instance/{processInstanceId}")
    public ResponseEntity<WorkflowInstance> getWorkflowInstanceByProcessInstanceId(@PathVariable String processInstanceId) {
        Optional<WorkflowInstance> instance = workflowInstanceService.getWorkflowInstanceByProcessInstanceId(processInstanceId);
        return instance.map(i -> new ResponseEntity<>(i, HttpStatus.OK))
                      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @PostMapping("/{id}/suspend")
    public ResponseEntity<WorkflowInstance> suspendWorkflowInstance(@PathVariable Long id) {
        try {
            WorkflowInstance suspended = workflowInstanceService.suspendWorkflowInstance(id);
            return new ResponseEntity<>(suspended, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    @PostMapping("/{id}/resume")
    public ResponseEntity<WorkflowInstance> resumeWorkflowInstance(@PathVariable Long id) {
        try {
            WorkflowInstance resumed = workflowInstanceService.resumeWorkflowInstance(id);
            return new ResponseEntity<>(resumed, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    @PostMapping("/{id}/terminate")
    public ResponseEntity<WorkflowInstance> terminateWorkflowInstance(@PathVariable Long id, 
                                                                     @RequestParam(required = false) String reason) {
        try {
            WorkflowInstance terminated = workflowInstanceService.terminateWorkflowInstance(id, reason);
            return new ResponseEntity<>(terminated, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    @PostMapping("/{id}/complete")
    public ResponseEntity<WorkflowInstance> completeWorkflowInstance(@PathVariable Long id) {
        try {
            WorkflowInstance completed = workflowInstanceService.completeWorkflowInstance(id);
            return new ResponseEntity<>(completed, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<WorkflowInstance>> getWorkflowInstancesByStatus(@PathVariable WorkflowInstanceStatus status) {
        List<WorkflowInstance> instances = workflowInstanceService.getWorkflowInstancesByStatus(status);
        return new ResponseEntity<>(instances, HttpStatus.OK);
    }
    
    @GetMapping("/started-by/{startedBy}")
    public ResponseEntity<List<WorkflowInstance>> getWorkflowInstancesByStartedBy(@PathVariable String startedBy) {
        List<WorkflowInstance> instances = workflowInstanceService.getWorkflowInstancesByStartedBy(startedBy);
        return new ResponseEntity<>(instances, HttpStatus.OK);
    }
    
    @GetMapping("/definition/{workflowDefinitionId}")
    public ResponseEntity<List<WorkflowInstance>> getWorkflowInstancesByDefinition(@PathVariable Long workflowDefinitionId) {
        List<WorkflowInstance> instances = workflowInstanceService.getWorkflowInstancesByDefinition(workflowDefinitionId);
        return new ResponseEntity<>(instances, HttpStatus.OK);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<WorkflowInstance>> searchWorkflowInstancesByName(@RequestParam String name) {
        List<WorkflowInstance> instances = workflowInstanceService.searchWorkflowInstancesByName(name);
        return new ResponseEntity<>(instances, HttpStatus.OK);
    }
    
    @PostMapping("/{id}/variables/{variableName}")
    public ResponseEntity<Void> setWorkflowInstanceVariable(@PathVariable Long id, 
                                                           @PathVariable String variableName, 
                                                           @RequestBody Object value) {
        try {
            workflowInstanceService.setWorkflowInstanceVariable(id, variableName, value);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @PostMapping("/{id}/variables")
    public ResponseEntity<Void> setWorkflowInstanceVariables(@PathVariable Long id, 
                                                            @RequestBody Map<String, Object> variables) {
        try {
            workflowInstanceService.setWorkflowInstanceVariables(id, variables);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/{id}/variables")
    public ResponseEntity<Map<String, Object>> getWorkflowInstanceVariables(@PathVariable Long id) {
        try {
            Map<String, Object> variables = workflowInstanceService.getWorkflowInstanceVariables(id);
            return new ResponseEntity<>(variables, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/count/status/{status}")
    public ResponseEntity<Long> countWorkflowInstancesByStatus(@PathVariable WorkflowInstanceStatus status) {
        Long count = workflowInstanceService.countWorkflowInstancesByStatus(status);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }
}