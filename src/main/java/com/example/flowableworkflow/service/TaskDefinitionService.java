package com.example.flowableworkflow.service;

import com.example.flowableworkflow.entity.TaskDefinition;
import com.example.flowableworkflow.entity.TaskType;
import com.example.flowableworkflow.entity.WorkflowDefinition;
import com.example.flowableworkflow.repository.TaskDefinitionRepository;
import com.example.flowableworkflow.repository.WorkflowDefinitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TaskDefinitionService {
    
    @Autowired
    private TaskDefinitionRepository taskDefinitionRepository;
    
    @Autowired
    private WorkflowDefinitionRepository workflowDefinitionRepository;
    
    public TaskDefinition createTaskDefinition(TaskDefinition taskDefinition) {
        // Validate unique task definition key
        if (taskDefinitionRepository.existsByTaskDefinitionKey(taskDefinition.getTaskDefinitionKey())) {
            throw new IllegalArgumentException("Task definition key already exists: " + taskDefinition.getTaskDefinitionKey());
        }
        
        // Validate workflow definition exists
        if (taskDefinition.getWorkflowDefinition() == null || taskDefinition.getWorkflowDefinition().getId() == null) {
            throw new IllegalArgumentException("Workflow definition is required");
        }
        
        WorkflowDefinition workflowDefinition = workflowDefinitionRepository.findById(taskDefinition.getWorkflowDefinition().getId())
                .orElseThrow(() -> new IllegalArgumentException("Workflow definition not found"));
        
        taskDefinition.setWorkflowDefinition(workflowDefinition);
        taskDefinition.setCreatedDate(LocalDateTime.now());
        
        // Set sequence number if not provided
        if (taskDefinition.getSequenceNumber() == null) {
            List<TaskDefinition> existingTasks = taskDefinitionRepository.findByWorkflowDefinitionId(workflowDefinition.getId());
            int nextSequence = existingTasks.stream()
                    .mapToInt(t -> t.getSequenceNumber() != null ? t.getSequenceNumber() : 0)
                    .max()
                    .orElse(0) + 1;
            taskDefinition.setSequenceNumber(nextSequence);
        }
        
        return taskDefinitionRepository.save(taskDefinition);
    }
    
    public TaskDefinition updateTaskDefinition(Long id, TaskDefinition taskDefinition) {
        TaskDefinition existingTask = taskDefinitionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task definition not found with id: " + id));
        
        existingTask.setName(taskDefinition.getName());
        existingTask.setDescription(taskDefinition.getDescription());
        existingTask.setTaskType(taskDefinition.getTaskType());
        existingTask.setAssignee(taskDefinition.getAssignee());
        existingTask.setCandidateUsers(taskDefinition.getCandidateUsers());
        existingTask.setCandidateGroups(taskDefinition.getCandidateGroups());
        existingTask.setPriority(taskDefinition.getPriority());
        existingTask.setDueDateExpression(taskDefinition.getDueDateExpression());
        existingTask.setFormKey(taskDefinition.getFormKey());
        existingTask.setDocumentation(taskDefinition.getDocumentation());
        existingTask.setExecutionListeners(taskDefinition.getExecutionListeners());
        existingTask.setTaskListeners(taskDefinition.getTaskListeners());
        existingTask.setInputParameters(taskDefinition.getInputParameters());
        existingTask.setOutputParameters(taskDefinition.getOutputParameters());
        existingTask.setSequenceNumber(taskDefinition.getSequenceNumber());
        existingTask.setIsParallel(taskDefinition.getIsParallel());
        existingTask.setIsMandatory(taskDefinition.getIsMandatory());
        
        return taskDefinitionRepository.save(existingTask);
    }
    
    public void deleteTaskDefinition(Long id) {
        TaskDefinition taskDefinition = taskDefinitionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task definition not found with id: " + id));
        
        taskDefinitionRepository.delete(taskDefinition);
    }
    
    public TaskDefinition reorderTaskDefinition(Long id, Integer newSequenceNumber) {
        TaskDefinition taskDefinition = taskDefinitionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task definition not found with id: " + id));
        
        List<TaskDefinition> workflowTasks = taskDefinitionRepository
                .findByWorkflowDefinitionIdOrderBySequenceNumber(taskDefinition.getWorkflowDefinition().getId());
        
        // Remove current task from list
        workflowTasks.removeIf(t -> t.getId().equals(id));
        
        // Reorder sequence numbers
        for (int i = 0; i < workflowTasks.size(); i++) {
            TaskDefinition task = workflowTasks.get(i);
            if (i + 1 >= newSequenceNumber) {
                task.setSequenceNumber(i + 2);
            } else {
                task.setSequenceNumber(i + 1);
            }
            taskDefinitionRepository.save(task);
        }
        
        // Set new sequence number for the moved task
        taskDefinition.setSequenceNumber(newSequenceNumber);
        return taskDefinitionRepository.save(taskDefinition);
    }
    
    @Transactional(readOnly = true)
    public Optional<TaskDefinition> getTaskDefinitionById(Long id) {
        return taskDefinitionRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<TaskDefinition> getTaskDefinitionByKey(String taskDefinitionKey) {
        return taskDefinitionRepository.findByTaskDefinitionKey(taskDefinitionKey);
    }
    
    @Transactional(readOnly = true)
    public List<TaskDefinition> getTaskDefinitionsByWorkflowId(Long workflowDefinitionId) {
        return taskDefinitionRepository.findByWorkflowDefinitionIdOrderBySequenceNumber(workflowDefinitionId);
    }
    
    @Transactional(readOnly = true)
    public List<TaskDefinition> getTaskDefinitionsByType(TaskType taskType) {
        return taskDefinitionRepository.findByTaskType(taskType);
    }
    
    @Transactional(readOnly = true)
    public List<TaskDefinition> getTaskDefinitionsByAssignee(String assignee) {
        return taskDefinitionRepository.findByAssignee(assignee);
    }
    
    @Transactional(readOnly = true)
    public List<TaskDefinition> searchTaskDefinitionsByName(String name) {
        return taskDefinitionRepository.findByNameContaining(name);
    }
    
    @Transactional(readOnly = true)
    public List<TaskDefinition> getTaskDefinitionsByCandidateUsersOrGroups(String user, String group) {
        return taskDefinitionRepository.findByCandidateUsersOrGroups(user, group);
    }
    
    public List<TaskDefinition> duplicateTaskDefinitionsToWorkflow(Long sourceWorkflowId, Long targetWorkflowId) {
        WorkflowDefinition targetWorkflow = workflowDefinitionRepository.findById(targetWorkflowId)
                .orElseThrow(() -> new IllegalArgumentException("Target workflow definition not found"));
        
        List<TaskDefinition> sourceTasks = taskDefinitionRepository.findByWorkflowDefinitionIdOrderBySequenceNumber(sourceWorkflowId);
        
        return sourceTasks.stream()
                .map(sourceTask -> {
                    TaskDefinition newTask = new TaskDefinition();
                    newTask.setName(sourceTask.getName());
                    newTask.setDescription(sourceTask.getDescription());
                    newTask.setTaskDefinitionKey(sourceTask.getTaskDefinitionKey() + "_copy_" + System.currentTimeMillis());
                    newTask.setTaskType(sourceTask.getTaskType());
                    newTask.setAssignee(sourceTask.getAssignee());
                    newTask.setCandidateUsers(sourceTask.getCandidateUsers());
                    newTask.setCandidateGroups(sourceTask.getCandidateGroups());
                    newTask.setPriority(sourceTask.getPriority());
                    newTask.setDueDateExpression(sourceTask.getDueDateExpression());
                    newTask.setFormKey(sourceTask.getFormKey());
                    newTask.setDocumentation(sourceTask.getDocumentation());
                    newTask.setSequenceNumber(sourceTask.getSequenceNumber());
                    newTask.setIsParallel(sourceTask.getIsParallel());
                    newTask.setIsMandatory(sourceTask.getIsMandatory());
                    newTask.setWorkflowDefinition(targetWorkflow);
                    newTask.setCreatedDate(LocalDateTime.now());
                    
                    return taskDefinitionRepository.save(newTask);
                })
                .toList();
    }
}