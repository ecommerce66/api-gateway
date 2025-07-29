package com.workflow.service;

import com.workflow.dto.TaskResponse;
import com.workflow.entity.Task;
import com.workflow.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowTaskService {
    
    private final TaskRepository taskRepository;
    private final TaskService flowableTaskService;
    
    @Transactional
    public TaskResponse startTask(String taskId) {
        log.info("Starting task with ID: {}", taskId);
        
        Optional<Task> taskOpt = taskRepository.findByTaskId(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setStatus(Task.TaskStatus.IN_PROGRESS);
            task.setStartTime(LocalDateTime.now());
            
            Task savedTask = taskRepository.save(task);
            return TaskResponse.fromEntity(savedTask);
        }
        
        throw new RuntimeException("Task not found with ID: " + taskId);
    }
    
    @Transactional
    public TaskResponse completeTask(String taskId, Map<String, Object> variables) {
        log.info("Completing task with ID: {}", taskId);
        
        // Complete the task in Flowable
        flowableTaskService.complete(taskId, variables);
        
        // Update our task entity
        Optional<Task> taskOpt = taskRepository.findByTaskId(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setStatus(Task.TaskStatus.COMPLETED);
            task.setEndTime(LocalDateTime.now());
            if (task.getStartTime() != null) {
                task.setDuration(java.time.Duration.between(task.getStartTime(), task.getEndTime()).toMillis());
            }
            
            Task savedTask = taskRepository.save(task);
            return TaskResponse.fromEntity(savedTask);
        }
        
        throw new RuntimeException("Task not found with ID: " + taskId);
    }
    
    @Transactional
    public TaskResponse assignTask(String taskId, String assignee) {
        log.info("Assigning task {} to user: {}", taskId, assignee);
        
        // Assign the task in Flowable
        flowableTaskService.setAssignee(taskId, assignee);
        
        // Update our task entity
        Optional<Task> taskOpt = taskRepository.findByTaskId(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setAssignee(assignee);
            task.setStatus(Task.TaskStatus.ASSIGNED);
            
            Task savedTask = taskRepository.save(task);
            return TaskResponse.fromEntity(savedTask);
        }
        
        throw new RuntimeException("Task not found with ID: " + taskId);
    }
    
    @Transactional
    public TaskResponse claimTask(String taskId, String userId) {
        log.info("Claiming task {} by user: {}", taskId, userId);
        
        // Claim the task in Flowable
        flowableTaskService.claim(taskId, userId);
        
        // Update our task entity
        Optional<Task> taskOpt = taskRepository.findByTaskId(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setAssignee(userId);
            task.setClaimTime(LocalDateTime.now());
            task.setStatus(Task.TaskStatus.ASSIGNED);
            
            Task savedTask = taskRepository.save(task);
            return TaskResponse.fromEntity(savedTask);
        }
        
        throw new RuntimeException("Task not found with ID: " + taskId);
    }
    
    @Transactional
    public TaskResponse unclaimTask(String taskId) {
        log.info("Unclaiming task: {}", taskId);
        
        // Unclaim the task in Flowable
        flowableTaskService.unclaim(taskId);
        
        // Update our task entity
        Optional<Task> taskOpt = taskRepository.findByTaskId(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setAssignee(null);
            task.setClaimTime(null);
            task.setStatus(Task.TaskStatus.CREATED);
            
            Task savedTask = taskRepository.save(task);
            return TaskResponse.fromEntity(savedTask);
        }
        
        throw new RuntimeException("Task not found with ID: " + taskId);
    }
    
    @Transactional
    public TaskResponse updateTaskVariables(String taskId, Map<String, Object> variables) {
        log.info("Updating variables for task: {}", taskId);
        
        // Update variables in Flowable
        flowableTaskService.setVariables(taskId, variables);
        
        // Update our task entity
        Optional<Task> taskOpt = taskRepository.findByTaskId(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            // Convert variables to string map for storage
            Map<String, String> stringVariables = variables.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> entry.getValue() != null ? entry.getValue().toString() : null
                    ));
            task.setVariables(stringVariables);
            
            Task savedTask = taskRepository.save(task);
            return TaskResponse.fromEntity(savedTask);
        }
        
        throw new RuntimeException("Task not found with ID: " + taskId);
    }
    
    @Transactional
    public TaskResponse setTaskPriority(String taskId, Integer priority) {
        log.info("Setting priority {} for task: {}", priority, taskId);
        
        // Set priority in Flowable
        flowableTaskService.setPriority(taskId, priority);
        
        // Update our task entity
        Optional<Task> taskOpt = taskRepository.findByTaskId(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setPriority(priority);
            
            Task savedTask = taskRepository.save(task);
            return TaskResponse.fromEntity(savedTask);
        }
        
        throw new RuntimeException("Task not found with ID: " + taskId);
    }
    
    @Transactional
    public TaskResponse setTaskDueDate(String taskId, LocalDateTime dueDate) {
        log.info("Setting due date {} for task: {}", dueDate, taskId);
        
        // Set due date in Flowable
        flowableTaskService.setDueDate(taskId, dueDate);
        
        // Update our task entity
        Optional<Task> taskOpt = taskRepository.findByTaskId(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setDueDate(dueDate);
            
            Task savedTask = taskRepository.save(task);
            return TaskResponse.fromEntity(savedTask);
        }
        
        throw new RuntimeException("Task not found with ID: " + taskId);
    }
    
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    public Optional<TaskResponse> getTaskById(Long id) {
        return taskRepository.findById(id)
                .map(TaskResponse::fromEntity);
    }
    
    public Optional<TaskResponse> getTaskByTaskId(String taskId) {
        return taskRepository.findByTaskId(taskId)
                .map(TaskResponse::fromEntity);
    }
    
    public List<TaskResponse> getTasksByProcessInstanceId(String processInstanceId) {
        return taskRepository.findByProcessInstanceId(processInstanceId).stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<TaskResponse> getTasksByAssignee(String assignee) {
        return taskRepository.findByAssignee(assignee).stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<TaskResponse> getTasksByStatus(Task.TaskStatus status) {
        return taskRepository.findByStatus(status).stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<TaskResponse> getTasksByAssigneeAndStatus(String assignee, Task.TaskStatus status) {
        return taskRepository.findByAssigneeAndStatus(assignee, status).stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
    }
}