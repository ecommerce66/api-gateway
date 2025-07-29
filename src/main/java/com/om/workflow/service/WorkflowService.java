package com.om.workflow.service;

import com.om.workflow.dto.WorkflowRequest;
import com.om.workflow.dto.TaskRequest;
import com.om.workflow.model.Workflow;
import com.om.workflow.model.Task;
import com.om.workflow.model.WorkflowStatus;
import com.om.workflow.model.TaskStatus;
import com.om.workflow.repository.WorkflowRepository;
import com.om.workflow.repository.TaskRepository;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class WorkflowService {
    
    @Autowired
    private WorkflowRepository workflowRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private ProcessEngine processEngine;
    
    @Autowired
    private RepositoryService repositoryService;
    
    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private TaskService taskService;
    
    public Workflow createWorkflow(WorkflowRequest request) {
        // Create workflow entity
        Workflow workflow = new Workflow();
        workflow.setProcessDefinitionKey(request.getProcessDefinitionKey());
        workflow.setProcessDefinitionName(request.getProcessDefinitionName());
        workflow.setDescription(request.getDescription());
        workflow.setCategory(request.getCategory());
        workflow.setTenantId(request.getTenantId());
        workflow.setVersionTag(request.getVersionTag());
        workflow.setHistoryLevel(request.getHistoryLevel());
        workflow.setStatus(WorkflowStatus.DRAFT);
        
        // Save workflow
        workflow = workflowRepository.save(workflow);
        
        // Create tasks
        for (TaskRequest taskRequest : request.getTasks()) {
            Task task = createTaskFromRequest(taskRequest);
            workflow.addTask(task);
        }
        
        // Save workflow with tasks
        return workflowRepository.save(workflow);
    }
    
    public Workflow deployWorkflow(Long workflowId) {
        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new RuntimeException("Workflow not found"));
        
        try {
            // Deploy to Flowable engine
            Deployment deployment = repositoryService.createDeployment()
                    .addClasspathResource("processes/" + workflow.getProcessDefinitionKey() + ".bpmn20.xml")
                    .name(workflow.getProcessDefinitionName())
                    .category(workflow.getCategory())
                    .tenantId(workflow.getTenantId())
                    .deploy();
            
            // Get process definition
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .deploymentId(deployment.getId())
                    .singleResult();
            
            // Update workflow with Flowable IDs
            workflow.setDeploymentId(deployment.getId());
            workflow.setProcessDefinitionId(processDefinition.getId());
            workflow.setProcessDefinitionVersion(processDefinition.getVersion());
            workflow.setStatus(WorkflowStatus.ACTIVE);
            
            return workflowRepository.save(workflow);
        } catch (Exception e) {
            workflow.setStatus(WorkflowStatus.DRAFT);
            workflowRepository.save(workflow);
            throw new RuntimeException("Failed to deploy workflow: " + e.getMessage(), e);
        }
    }
    
    public ProcessInstance startWorkflow(Long workflowId) {
        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new RuntimeException("Workflow not found"));
        
        if (workflow.getStatus() != WorkflowStatus.ACTIVE) {
            throw new RuntimeException("Workflow is not active");
        }
        
        // Start process instance
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                workflow.getProcessDefinitionKey());
        
        // Update workflow status
        workflow.setStatus(WorkflowStatus.ACTIVE);
        workflowRepository.save(workflow);
        
        return processInstance;
    }
    
    public void completeTask(String taskId) {
        org.flowable.task.api.Task flowableTask = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();
        
        if (flowableTask == null) {
            throw new RuntimeException("Task not found");
        }
        
        // Complete the task
        taskService.complete(taskId);
        
        // Update local task status
        Optional<Task> localTask = taskRepository.findByTaskId(taskId);
        if (localTask.isPresent()) {
            Task task = localTask.get();
            task.setStatus(TaskStatus.COMPLETED);
            taskRepository.save(task);
        }
    }
    
    public void endWorkflow(String processInstanceId) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        
        if (processInstance == null) {
            throw new RuntimeException("Process instance not found");
        }
        
        // Delete the process instance
        runtimeService.deleteProcessInstance(processInstanceId, "Workflow ended by user");
        
        // Update workflow status
        Optional<Workflow> workflow = workflowRepository.findByProcessDefinitionId(processInstance.getProcessDefinitionId());
        if (workflow.isPresent()) {
            Workflow wf = workflow.get();
            wf.setStatus(WorkflowStatus.COMPLETED);
            workflowRepository.save(wf);
        }
    }
    
    public List<Workflow> getAllWorkflows() {
        return workflowRepository.findAll();
    }
    
    public Optional<Workflow> getWorkflowById(Long id) {
        return workflowRepository.findById(id);
    }
    
    public List<Workflow> getWorkflowsByStatus(WorkflowStatus status) {
        return workflowRepository.findByStatus(status);
    }
    
    public List<Task> getTasksByWorkflowId(Long workflowId) {
        return taskRepository.findByWorkflowId(workflowId);
    }
    
    public List<Task> getTasksByAssignee(String assignee) {
        return taskRepository.findByAssignee(assignee);
    }
    
    public List<Task> getOverdueTasks() {
        return taskRepository.findOverdueTasks(java.time.LocalDateTime.now());
    }
    
    private Task createTaskFromRequest(TaskRequest request) {
        Task task = new Task();
        task.setName(request.getName());
        task.setDescription(request.getDescription());
        task.setTaskDefinitionKey(request.getTaskDefinitionKey());
        task.setFormKey(request.getFormKey());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setCategory(request.getCategory());
        task.setTenantId(request.getTenantId());
        task.setAssignee(request.getAssignee());
        task.setOwner(request.getOwner());
        task.setCandidateUsers(request.getCandidateUsers());
        task.setCandidateGroups(request.getCandidateGroups());
        
        if (request.getVariables() != null) {
            task.setVariables(request.getVariables());
        }
        
        return task;
    }
}