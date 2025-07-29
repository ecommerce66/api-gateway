package com.example.flowableworkflow.service;

import com.example.flowableworkflow.entity.TaskInstance;
import com.example.flowableworkflow.entity.TaskInstanceStatus;
import com.example.flowableworkflow.entity.WorkflowInstance;
import com.example.flowableworkflow.repository.TaskInstanceRepository;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class TaskInstanceService {
    
    @Autowired
    private TaskInstanceRepository taskInstanceRepository;
    
    @Autowired
    private TaskService taskService;
    
    public void createTaskInstancesForWorkflow(WorkflowInstance workflowInstance) {
        // Query Flowable tasks for this process instance
        List<Task> flowableTasks = taskService.createTaskQuery()
                .processInstanceId(workflowInstance.getProcessInstanceId())
                .list();
        
        for (Task flowableTask : flowableTasks) {
            TaskInstance taskInstance = new TaskInstance(workflowInstance, flowableTask.getName());
            taskInstance.setFlowableTaskId(flowableTask.getId());
            taskInstance.setDescription(flowableTask.getDescription());
            taskInstance.setAssignee(flowableTask.getAssignee());
            taskInstance.setOwner(flowableTask.getOwner());
            taskInstance.setPriority(flowableTask.getPriority());
            taskInstance.setFormKey(flowableTask.getFormKey());
            taskInstance.setTenantId(flowableTask.getTenantId());
            taskInstance.setStatus(TaskInstanceStatus.CREATED);
            
            if (flowableTask.getDueDate() != null) {
                taskInstance.setDueDate(flowableTask.getDueDate().toInstant()
                        .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
            }
            
            taskInstanceRepository.save(taskInstance);
        }
    }
    
    public TaskInstance claimTask(Long taskInstanceId, String assignee) {
        TaskInstance taskInstance = taskInstanceRepository.findById(taskInstanceId)
                .orElseThrow(() -> new IllegalArgumentException("Task instance not found with id: " + taskInstanceId));
        
        // Claim in Flowable
        taskService.claim(taskInstance.getFlowableTaskId(), assignee);
        
        taskInstance.setAssignee(assignee);
        taskInstance.setStatus(TaskInstanceStatus.RESERVED);
        taskInstance.setClaimTime(LocalDateTime.now());
        
        return taskInstanceRepository.save(taskInstance);
    }
    
    public TaskInstance startTask(Long taskInstanceId) {
        TaskInstance taskInstance = taskInstanceRepository.findById(taskInstanceId)
                .orElseThrow(() -> new IllegalArgumentException("Task instance not found with id: " + taskInstanceId));
        
        taskInstance.setStatus(TaskInstanceStatus.IN_PROGRESS);
        return taskInstanceRepository.save(taskInstance);
    }
    
    public TaskInstance completeTask(Long taskInstanceId, Map<String, Object> variables) {
        TaskInstance taskInstance = taskInstanceRepository.findById(taskInstanceId)
                .orElseThrow(() -> new IllegalArgumentException("Task instance not found with id: " + taskInstanceId));
        
        // Complete in Flowable
        taskService.complete(taskInstance.getFlowableTaskId(), variables);
        
        taskInstance.setStatus(TaskInstanceStatus.COMPLETED);
        taskInstance.setEndTime(LocalDateTime.now());
        
        // Store local variables
        if (variables != null) {
            variables.forEach((key, value) -> taskInstance.addLocalVariable(key, value.toString()));
        }
        
        return taskInstanceRepository.save(taskInstance);
    }
    
    public TaskInstance suspendTask(Long taskInstanceId) {
        TaskInstance taskInstance = taskInstanceRepository.findById(taskInstanceId)
                .orElseThrow(() -> new IllegalArgumentException("Task instance not found with id: " + taskInstanceId));
        
        taskInstance.setStatus(TaskInstanceStatus.SUSPENDED);
        return taskInstanceRepository.save(taskInstance);
    }
    
    public TaskInstance resumeTask(Long taskInstanceId) {
        TaskInstance taskInstance = taskInstanceRepository.findById(taskInstanceId)
                .orElseThrow(() -> new IllegalArgumentException("Task instance not found with id: " + taskInstanceId));
        
        taskInstance.setStatus(TaskInstanceStatus.IN_PROGRESS);
        return taskInstanceRepository.save(taskInstance);
    }
    
    public TaskInstance delegateTask(Long taskInstanceId, String delegateAssignee) {
        TaskInstance taskInstance = taskInstanceRepository.findById(taskInstanceId)
                .orElseThrow(() -> new IllegalArgumentException("Task instance not found with id: " + taskInstanceId));
        
        // Delegate in Flowable
        taskService.delegateTask(taskInstance.getFlowableTaskId(), delegateAssignee);
        
        taskInstance.setOwner(taskInstance.getAssignee());
        taskInstance.setAssignee(delegateAssignee);
        
        return taskInstanceRepository.save(taskInstance);
    }
    
    public TaskInstance assignTask(Long taskInstanceId, String assignee) {
        TaskInstance taskInstance = taskInstanceRepository.findById(taskInstanceId)
                .orElseThrow(() -> new IllegalArgumentException("Task instance not found with id: " + taskInstanceId));
        
        // Assign in Flowable
        taskService.setAssignee(taskInstance.getFlowableTaskId(), assignee);
        
        taskInstance.setAssignee(assignee);
        taskInstance.setStatus(TaskInstanceStatus.RESERVED);
        
        return taskInstanceRepository.save(taskInstance);
    }
    
    public void setTaskInstanceVariable(Long taskInstanceId, String variableName, Object value) {
        TaskInstance taskInstance = taskInstanceRepository.findById(taskInstanceId)
                .orElseThrow(() -> new IllegalArgumentException("Task instance not found with id: " + taskInstanceId));
        
        // Set variable in Flowable
        taskService.setVariableLocal(taskInstance.getFlowableTaskId(), variableName, value);
        
        // Update local entity
        taskInstance.addLocalVariable(variableName, value.toString());
        taskInstanceRepository.save(taskInstance);
    }
    
    public void setTaskInstanceVariables(Long taskInstanceId, Map<String, Object> variables) {
        TaskInstance taskInstance = taskInstanceRepository.findById(taskInstanceId)
                .orElseThrow(() -> new IllegalArgumentException("Task instance not found with id: " + taskInstanceId));
        
        // Set variables in Flowable
        taskService.setVariablesLocal(taskInstance.getFlowableTaskId(), variables);
        
        // Update local entity
        variables.forEach((key, value) -> taskInstance.addLocalVariable(key, value.toString()));
        taskInstanceRepository.save(taskInstance);
    }
    
    @Transactional(readOnly = true)
    public Optional<TaskInstance> getTaskInstanceById(Long id) {
        return taskInstanceRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<TaskInstance> getTaskInstanceByFlowableTaskId(String flowableTaskId) {
        return taskInstanceRepository.findByFlowableTaskId(flowableTaskId);
    }
    
    @Transactional(readOnly = true)
    public List<TaskInstance> getTaskInstancesByStatus(TaskInstanceStatus status) {
        return taskInstanceRepository.findByStatus(status);
    }
    
    @Transactional(readOnly = true)
    public List<TaskInstance> getTaskInstancesByAssignee(String assignee) {
        return taskInstanceRepository.findByAssignee(assignee);
    }
    
    @Transactional(readOnly = true)
    public List<TaskInstance> getTaskInstancesByWorkflowInstance(Long workflowInstanceId) {
        return taskInstanceRepository.findByWorkflowInstanceId(workflowInstanceId);
    }
    
    @Transactional(readOnly = true)
    public List<TaskInstance> getTaskInstancesByCandidateUsersOrGroups(String user, String group) {
        return taskInstanceRepository.findByCandidateUsersOrGroups(user, group);
    }
    
    @Transactional(readOnly = true)
    public List<TaskInstance> getOverdueTasks() {
        return taskInstanceRepository.findOverdueTasks(LocalDateTime.now());
    }
    
    @Transactional(readOnly = true)
    public List<TaskInstance> getHighPriorityTasks(Integer minimumPriority) {
        return taskInstanceRepository.findByPriorityGreaterThanEqualOrderByPriorityDesc(minimumPriority);
    }
    
    @Transactional(readOnly = true)
    public List<TaskInstance> searchTaskInstancesByName(String name) {
        return taskInstanceRepository.findByNameContaining(name);
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> getTaskInstanceVariables(Long taskInstanceId) {
        TaskInstance taskInstance = taskInstanceRepository.findById(taskInstanceId)
                .orElseThrow(() -> new IllegalArgumentException("Task instance not found with id: " + taskInstanceId));
        
        return taskService.getVariablesLocal(taskInstance.getFlowableTaskId());
    }
    
    @Transactional(readOnly = true)
    public Long countTaskInstancesByStatus(TaskInstanceStatus status) {
        return taskInstanceRepository.countByStatus(status);
    }
}