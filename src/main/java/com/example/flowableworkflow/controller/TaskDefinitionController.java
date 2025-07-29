package com.example.flowableworkflow.controller;

import com.example.flowableworkflow.entity.TaskDefinition;
import com.example.flowableworkflow.entity.TaskType;
import com.example.flowableworkflow.service.TaskDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/task-definitions")
@CrossOrigin(origins = "*")
public class TaskDefinitionController {
    
    @Autowired
    private TaskDefinitionService taskDefinitionService;
    
    @PostMapping
    public ResponseEntity<TaskDefinition> createTaskDefinition(@Valid @RequestBody TaskDefinition taskDefinition) {
        try {
            TaskDefinition created = taskDefinitionService.createTaskDefinition(taskDefinition);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TaskDefinition> getTaskDefinitionById(@PathVariable Long id) {
        Optional<TaskDefinition> taskDefinition = taskDefinitionService.getTaskDefinitionById(id);
        return taskDefinition.map(t -> new ResponseEntity<>(t, HttpStatus.OK))
                           .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/key/{taskDefinitionKey}")
    public ResponseEntity<TaskDefinition> getTaskDefinitionByKey(@PathVariable String taskDefinitionKey) {
        Optional<TaskDefinition> taskDefinition = taskDefinitionService.getTaskDefinitionByKey(taskDefinitionKey);
        return taskDefinition.map(t -> new ResponseEntity<>(t, HttpStatus.OK))
                           .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TaskDefinition> updateTaskDefinition(@PathVariable Long id, 
                                                              @Valid @RequestBody TaskDefinition taskDefinition) {
        try {
            TaskDefinition updated = taskDefinitionService.updateTaskDefinition(id, taskDefinition);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskDefinition(@PathVariable Long id) {
        try {
            taskDefinitionService.deleteTaskDefinition(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/workflow/{workflowDefinitionId}")
    public ResponseEntity<List<TaskDefinition>> getTaskDefinitionsByWorkflowId(@PathVariable Long workflowDefinitionId) {
        List<TaskDefinition> taskDefinitions = taskDefinitionService.getTaskDefinitionsByWorkflowId(workflowDefinitionId);
        return new ResponseEntity<>(taskDefinitions, HttpStatus.OK);
    }
    
    @GetMapping("/type/{taskType}")
    public ResponseEntity<List<TaskDefinition>> getTaskDefinitionsByType(@PathVariable TaskType taskType) {
        List<TaskDefinition> taskDefinitions = taskDefinitionService.getTaskDefinitionsByType(taskType);
        return new ResponseEntity<>(taskDefinitions, HttpStatus.OK);
    }
    
    @GetMapping("/assignee/{assignee}")
    public ResponseEntity<List<TaskDefinition>> getTaskDefinitionsByAssignee(@PathVariable String assignee) {
        List<TaskDefinition> taskDefinitions = taskDefinitionService.getTaskDefinitionsByAssignee(assignee);
        return new ResponseEntity<>(taskDefinitions, HttpStatus.OK);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<TaskDefinition>> searchTaskDefinitionsByName(@RequestParam String name) {
        List<TaskDefinition> taskDefinitions = taskDefinitionService.searchTaskDefinitionsByName(name);
        return new ResponseEntity<>(taskDefinitions, HttpStatus.OK);
    }
    
    @GetMapping("/candidate")
    public ResponseEntity<List<TaskDefinition>> getTaskDefinitionsByCandidateUsersOrGroups(
            @RequestParam(required = false) String user, 
            @RequestParam(required = false) String group) {
        List<TaskDefinition> taskDefinitions = taskDefinitionService.getTaskDefinitionsByCandidateUsersOrGroups(user, group);
        return new ResponseEntity<>(taskDefinitions, HttpStatus.OK);
    }
    
    @PutMapping("/{id}/reorder")
    public ResponseEntity<TaskDefinition> reorderTaskDefinition(@PathVariable Long id, 
                                                               @RequestParam Integer newSequenceNumber) {
        try {
            TaskDefinition reordered = taskDefinitionService.reorderTaskDefinition(id, newSequenceNumber);
            return new ResponseEntity<>(reordered, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    @PostMapping("/duplicate")
    public ResponseEntity<List<TaskDefinition>> duplicateTaskDefinitionsToWorkflow(
            @RequestParam Long sourceWorkflowId, 
            @RequestParam Long targetWorkflowId) {
        try {
            List<TaskDefinition> duplicated = taskDefinitionService.duplicateTaskDefinitionsToWorkflow(sourceWorkflowId, targetWorkflowId);
            return new ResponseEntity<>(duplicated, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}