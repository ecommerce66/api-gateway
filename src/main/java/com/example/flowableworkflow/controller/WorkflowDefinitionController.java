package com.example.flowableworkflow.controller;

import com.example.flowableworkflow.entity.WorkflowDefinition;
import com.example.flowableworkflow.entity.WorkflowStatus;
import com.example.flowableworkflow.service.WorkflowDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/workflows")
@CrossOrigin(origins = "*")
public class WorkflowDefinitionController {
    
    @Autowired
    private WorkflowDefinitionService workflowDefinitionService;
    
    @PostMapping
    public ResponseEntity<WorkflowDefinition> createWorkflowDefinition(@Valid @RequestBody WorkflowDefinition workflowDefinition) {
        try {
            WorkflowDefinition created = workflowDefinitionService.createWorkflowDefinition(workflowDefinition);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping
    public ResponseEntity<List<WorkflowDefinition>> getAllWorkflowDefinitions() {
        List<WorkflowDefinition> workflows = workflowDefinitionService.getAllWorkflowDefinitions();
        return new ResponseEntity<>(workflows, HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<WorkflowDefinition> getWorkflowDefinitionById(@PathVariable Long id) {
        Optional<WorkflowDefinition> workflow = workflowDefinitionService.getWorkflowDefinitionById(id);
        return workflow.map(w -> new ResponseEntity<>(w, HttpStatus.OK))
                      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/key/{processDefinitionKey}")
    public ResponseEntity<WorkflowDefinition> getWorkflowDefinitionByKey(@PathVariable String processDefinitionKey) {
        Optional<WorkflowDefinition> workflow = workflowDefinitionService.getWorkflowDefinitionByKey(processDefinitionKey);
        return workflow.map(w -> new ResponseEntity<>(w, HttpStatus.OK))
                      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<WorkflowDefinition> updateWorkflowDefinition(@PathVariable Long id, 
                                                                      @Valid @RequestBody WorkflowDefinition workflowDefinition) {
        try {
            WorkflowDefinition updated = workflowDefinitionService.updateWorkflowDefinition(id, workflowDefinition);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    @PostMapping("/{id}/activate")
    public ResponseEntity<WorkflowDefinition> activateWorkflowDefinition(@PathVariable Long id) {
        try {
            WorkflowDefinition activated = workflowDefinitionService.activateWorkflowDefinition(id);
            return new ResponseEntity<>(activated, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/{id}/suspend")
    public ResponseEntity<WorkflowDefinition> suspendWorkflowDefinition(@PathVariable Long id) {
        try {
            WorkflowDefinition suspended = workflowDefinitionService.suspendWorkflowDefinition(id);
            return new ResponseEntity<>(suspended, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkflowDefinition(@PathVariable Long id) {
        try {
            workflowDefinitionService.deleteWorkflowDefinition(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<WorkflowDefinition>> getWorkflowDefinitionsByStatus(@PathVariable WorkflowStatus status) {
        List<WorkflowDefinition> workflows = workflowDefinitionService.getWorkflowDefinitionsByStatus(status);
        return new ResponseEntity<>(workflows, HttpStatus.OK);
    }
    
    @GetMapping("/created-by/{createdBy}")
    public ResponseEntity<List<WorkflowDefinition>> getWorkflowDefinitionsByCreatedBy(@PathVariable String createdBy) {
        List<WorkflowDefinition> workflows = workflowDefinitionService.getWorkflowDefinitionsByCreatedBy(createdBy);
        return new ResponseEntity<>(workflows, HttpStatus.OK);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<WorkflowDefinition>> searchWorkflowDefinitionsByName(@RequestParam String name) {
        List<WorkflowDefinition> workflows = workflowDefinitionService.searchWorkflowDefinitionsByName(name);
        return new ResponseEntity<>(workflows, HttpStatus.OK);
    }
    
    @PostMapping("/{id}/new-version")
    public ResponseEntity<WorkflowDefinition> createNewVersion(@PathVariable Long id) {
        try {
            WorkflowDefinition newVersion = workflowDefinitionService.createNewVersion(id);
            return new ResponseEntity<>(newVersion, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}