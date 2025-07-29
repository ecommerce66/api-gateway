package com.workflow.controller;

import com.workflow.dto.TaskResponse;
import com.workflow.entity.Task;
import com.workflow.service.WorkflowTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {
    
    private final WorkflowTaskService taskService;
    
    @PostMapping("/{taskId}/start")
    public ResponseEntity<TaskResponse> startTask(@PathVariable String taskId) {
        log.info("Starting task: {}", taskId);
        TaskResponse response = taskService.startTask(taskId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{taskId}/complete")
    public ResponseEntity<TaskResponse> completeTask(
            @PathVariable String taskId,
            @RequestBody(required = false) Map<String, Object> variables) {
        
        log.info("Completing task: {}", taskId);
        TaskResponse response = taskService.completeTask(taskId, variables);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{taskId}/assign")
    public ResponseEntity<TaskResponse> assignTask(
            @PathVariable String taskId,
            @RequestParam String assignee) {
        
        log.info("Assigning task {} to: {}", taskId, assignee);
        TaskResponse response = taskService.assignTask(taskId, assignee);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{taskId}/claim")
    public ResponseEntity<TaskResponse> claimTask(
            @PathVariable String taskId,
            @RequestParam String userId) {
        
        log.info("Claiming task {} by user: {}", taskId, userId);
        TaskResponse response = taskService.claimTask(taskId, userId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{taskId}/unclaim")
    public ResponseEntity<TaskResponse> unclaimTask(@PathVariable String taskId) {
        log.info("Unclaiming task: {}", taskId);
        TaskResponse response = taskService.unclaimTask(taskId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{taskId}/variables")
    public ResponseEntity<TaskResponse> updateTaskVariables(
            @PathVariable String taskId,
            @RequestBody Map<String, Object> variables) {
        
        log.info("Updating variables for task: {}", taskId);
        TaskResponse response = taskService.updateTaskVariables(taskId, variables);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{taskId}/priority")
    public ResponseEntity<TaskResponse> setTaskPriority(
            @PathVariable String taskId,
            @RequestParam Integer priority) {
        
        log.info("Setting priority {} for task: {}", priority, taskId);
        TaskResponse response = taskService.setTaskPriority(taskId, priority);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{taskId}/due-date")
    public ResponseEntity<TaskResponse> setTaskDueDate(
            @PathVariable String taskId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueDate) {
        
        log.info("Setting due date {} for task: {}", dueDate, taskId);
        TaskResponse response = taskService.setTaskDueDate(taskId, dueDate);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        log.info("Getting all tasks");
        List<TaskResponse> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        log.info("Getting task by ID: {}", id);
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/task-id/{taskId}")
    public ResponseEntity<TaskResponse> getTaskByTaskId(@PathVariable String taskId) {
        log.info("Getting task by task ID: {}", taskId);
        return taskService.getTaskByTaskId(taskId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/process-instance/{processInstanceId}")
    public ResponseEntity<List<TaskResponse>> getTasksByProcessInstanceId(@PathVariable String processInstanceId) {
        log.info("Getting tasks by process instance ID: {}", processInstanceId);
        List<TaskResponse> tasks = taskService.getTasksByProcessInstanceId(processInstanceId);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/assignee/{assignee}")
    public ResponseEntity<List<TaskResponse>> getTasksByAssignee(@PathVariable String assignee) {
        log.info("Getting tasks by assignee: {}", assignee);
        List<TaskResponse> tasks = taskService.getTasksByAssignee(assignee);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskResponse>> getTasksByStatus(@PathVariable Task.TaskStatus status) {
        log.info("Getting tasks by status: {}", status);
        List<TaskResponse> tasks = taskService.getTasksByStatus(status);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/assignee/{assignee}/status/{status}")
    public ResponseEntity<List<TaskResponse>> getTasksByAssigneeAndStatus(
            @PathVariable String assignee,
            @PathVariable Task.TaskStatus status) {
        
        log.info("Getting tasks by assignee {} and status: {}", assignee, status);
        List<TaskResponse> tasks = taskService.getTasksByAssigneeAndStatus(assignee, status);
        return ResponseEntity.ok(tasks);
    }
}