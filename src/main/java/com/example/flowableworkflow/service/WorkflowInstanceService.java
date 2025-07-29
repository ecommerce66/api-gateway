package com.example.flowableworkflow.service;

import com.example.flowableworkflow.entity.WorkflowDefinition;
import com.example.flowableworkflow.entity.WorkflowInstance;
import com.example.flowableworkflow.entity.WorkflowInstanceStatus;
import com.example.flowableworkflow.repository.WorkflowDefinitionRepository;
import com.example.flowableworkflow.repository.WorkflowInstanceRepository;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class WorkflowInstanceService {
    
    @Autowired
    private WorkflowInstanceRepository workflowInstanceRepository;
    
    @Autowired
    private WorkflowDefinitionRepository workflowDefinitionRepository;
    
    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private TaskInstanceService taskInstanceService;
    
    public WorkflowInstance startWorkflowInstance(Long workflowDefinitionId, String startedBy, 
                                                 String businessKey, Map<String, Object> variables) {
        WorkflowDefinition workflowDefinition = workflowDefinitionRepository.findById(workflowDefinitionId)
                .orElseThrow(() -> new IllegalArgumentException("Workflow definition not found with id: " + workflowDefinitionId));
        
        // Create workflow instance entity
        WorkflowInstance workflowInstance = new WorkflowInstance(workflowDefinition, startedBy);
        workflowInstance.setBusinessKey(businessKey);
        workflowInstance.setStatus(WorkflowInstanceStatus.RUNNING);
        
        // Add variables if provided
        if (variables != null) {
            final WorkflowInstance finalWorkflowInstance = workflowInstance;
            variables.forEach((key, value) -> finalWorkflowInstance.addVariable(key, value.toString()));
        }
        
        workflowInstance = workflowInstanceRepository.save(workflowInstance);
        
        // Start process instance in Flowable
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                workflowDefinition.getProcessDefinitionKey(),
                businessKey,
                variables);
        
        // Update with Flowable process instance ID
        workflowInstance.setProcessInstanceId(processInstance.getId());
        workflowInstance = workflowInstanceRepository.save(workflowInstance);
        
        // Create task instances
        taskInstanceService.createTaskInstancesForWorkflow(workflowInstance);
        
        return workflowInstance;
    }
    
    public WorkflowInstance suspendWorkflowInstance(Long workflowInstanceId) {
        WorkflowInstance workflowInstance = workflowInstanceRepository.findById(workflowInstanceId)
                .orElseThrow(() -> new IllegalArgumentException("Workflow instance not found with id: " + workflowInstanceId));
        
        // Suspend in Flowable
        runtimeService.suspendProcessInstanceById(workflowInstance.getProcessInstanceId());
        
        workflowInstance.setStatus(WorkflowInstanceStatus.SUSPENDED);
        return workflowInstanceRepository.save(workflowInstance);
    }
    
    public WorkflowInstance resumeWorkflowInstance(Long workflowInstanceId) {
        WorkflowInstance workflowInstance = workflowInstanceRepository.findById(workflowInstanceId)
                .orElseThrow(() -> new IllegalArgumentException("Workflow instance not found with id: " + workflowInstanceId));
        
        // Resume in Flowable
        runtimeService.activateProcessInstanceById(workflowInstance.getProcessInstanceId());
        
        workflowInstance.setStatus(WorkflowInstanceStatus.RUNNING);
        return workflowInstanceRepository.save(workflowInstance);
    }
    
    public WorkflowInstance terminateWorkflowInstance(Long workflowInstanceId, String reason) {
        WorkflowInstance workflowInstance = workflowInstanceRepository.findById(workflowInstanceId)
                .orElseThrow(() -> new IllegalArgumentException("Workflow instance not found with id: " + workflowInstanceId));
        
        // Terminate in Flowable
        runtimeService.deleteProcessInstance(workflowInstance.getProcessInstanceId(), reason);
        
        workflowInstance.setStatus(WorkflowInstanceStatus.TERMINATED);
        workflowInstance.setEndTime(LocalDateTime.now());
        return workflowInstanceRepository.save(workflowInstance);
    }
    
    public WorkflowInstance completeWorkflowInstance(Long workflowInstanceId) {
        WorkflowInstance workflowInstance = workflowInstanceRepository.findById(workflowInstanceId)
                .orElseThrow(() -> new IllegalArgumentException("Workflow instance not found with id: " + workflowInstanceId));
        
        workflowInstance.setStatus(WorkflowInstanceStatus.COMPLETED);
        workflowInstance.setEndTime(LocalDateTime.now());
        return workflowInstanceRepository.save(workflowInstance);
    }
    
    public void setWorkflowInstanceVariable(Long workflowInstanceId, String variableName, Object value) {
        WorkflowInstance workflowInstance = workflowInstanceRepository.findById(workflowInstanceId)
                .orElseThrow(() -> new IllegalArgumentException("Workflow instance not found with id: " + workflowInstanceId));
        
        // Set variable in Flowable
        runtimeService.setVariable(workflowInstance.getProcessInstanceId(), variableName, value);
        
        // Update local entity
        workflowInstance.addVariable(variableName, value.toString());
        workflowInstanceRepository.save(workflowInstance);
    }
    
    public void setWorkflowInstanceVariables(Long workflowInstanceId, Map<String, Object> variables) {
        WorkflowInstance workflowInstance = workflowInstanceRepository.findById(workflowInstanceId)
                .orElseThrow(() -> new IllegalArgumentException("Workflow instance not found with id: " + workflowInstanceId));
        
        // Set variables in Flowable
        runtimeService.setVariables(workflowInstance.getProcessInstanceId(), variables);
        
        // Update local entity
        variables.forEach((key, value) -> workflowInstance.addVariable(key, value.toString()));
        workflowInstanceRepository.save(workflowInstance);
    }
    
    @Transactional(readOnly = true)
    public Optional<WorkflowInstance> getWorkflowInstanceById(Long id) {
        return workflowInstanceRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<WorkflowInstance> getWorkflowInstanceByProcessInstanceId(String processInstanceId) {
        return workflowInstanceRepository.findByProcessInstanceId(processInstanceId);
    }
    
    @Transactional(readOnly = true)
    public List<WorkflowInstance> getAllWorkflowInstances() {
        return workflowInstanceRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<WorkflowInstance> getWorkflowInstancesByStatus(WorkflowInstanceStatus status) {
        return workflowInstanceRepository.findByStatus(status);
    }
    
    @Transactional(readOnly = true)
    public List<WorkflowInstance> getWorkflowInstancesByStartedBy(String startedBy) {
        return workflowInstanceRepository.findByStartedBy(startedBy);
    }
    
    @Transactional(readOnly = true)
    public List<WorkflowInstance> getWorkflowInstancesByDefinition(Long workflowDefinitionId) {
        return workflowInstanceRepository.findByWorkflowDefinitionId(workflowDefinitionId);
    }
    
    @Transactional(readOnly = true)
    public List<WorkflowInstance> searchWorkflowInstancesByName(String name) {
        return workflowInstanceRepository.findByNameContaining(name);
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> getWorkflowInstanceVariables(Long workflowInstanceId) {
        WorkflowInstance workflowInstance = workflowInstanceRepository.findById(workflowInstanceId)
                .orElseThrow(() -> new IllegalArgumentException("Workflow instance not found with id: " + workflowInstanceId));
        
        return runtimeService.getVariables(workflowInstance.getProcessInstanceId());
    }
    
    @Transactional(readOnly = true)
    public Long countWorkflowInstancesByStatus(WorkflowInstanceStatus status) {
        return workflowInstanceRepository.countByStatus(status);
    }
}