package com.workflow.service;

import com.workflow.dto.WorkflowRequest;
import com.workflow.dto.WorkflowResponse;
import com.workflow.entity.Task;
import com.workflow.entity.Workflow;
import com.workflow.repository.TaskRepository;
import com.workflow.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowService {
    
    private final WorkflowRepository workflowRepository;
    private final TaskRepository taskRepository;
    private final ProcessEngine processEngine;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    
    @Transactional
    public WorkflowResponse createWorkflow(WorkflowRequest request) {
        log.info("Creating workflow with name: {}", request.getName());
        
        // Generate BPMN XML dynamically based on request
        String bpmnXml = generateBpmnXml(request);
        
        // Deploy the process definition
        Deployment deployment = processEngine.getRepositoryService()
                .createDeployment()
                .addString(request.getKey() + ".bpmn20.xml", bpmnXml)
                .name(request.getName())
                .category(request.getCategory())
                .tenantId(request.getTenantId())
                .deploy();
        
        ProcessDefinition processDefinition = processEngine.getRepositoryService()
                .createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .singleResult();
        
        // Create workflow entity
        Workflow workflow = Workflow.builder()
                .processDefinitionId(processDefinition.getId())
                .processInstanceId(null) // Will be set when started
                .name(request.getName())
                .description(request.getDescription())
                .version(processDefinition.getVersion().toString())
                .category(request.getCategory())
                .key(request.getKey())
                .deploymentId(deployment.getId())
                .resourceName(processDefinition.getResourceName())
                .diagramResourceName(processDefinition.getDiagramResourceName())
                .tenantId(request.getTenantId())
                .status(Workflow.WorkflowStatus.ACTIVE)
                .startUserId(request.getStartUserId())
                .businessKey(request.getBusinessKey())
                .build();
        
        Workflow savedWorkflow = workflowRepository.save(workflow);
        
        // Create task entities
        List<Task> tasks = request.getTasks().stream()
                .map(taskRequest -> createTaskEntity(taskRequest, savedWorkflow))
                .collect(Collectors.toList());
        
        taskRepository.saveAll(tasks);
        savedWorkflow.setTasks(tasks);
        
        return WorkflowResponse.fromEntity(savedWorkflow);
    }
    
    @Transactional
    public WorkflowResponse startWorkflow(String processDefinitionId, String businessKey, Map<String, Object> variables) {
        log.info("Starting workflow with process definition ID: {}", processDefinitionId);
        
        // Start the process instance
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId, businessKey, variables);
        
        // Update workflow entity
        Optional<Workflow> workflowOpt = workflowRepository.findByProcessDefinitionId(processDefinitionId);
        if (workflowOpt.isPresent()) {
            Workflow workflow = workflowOpt.get();
            workflow.setProcessInstanceId(processInstance.getId());
            workflow.setStartTime(LocalDateTime.now());
            workflow.setStatus(Workflow.WorkflowStatus.ACTIVE);
            
            Workflow savedWorkflow = workflowRepository.save(workflow);
            
            // Update task entities with process instance information
            List<Task> tasks = taskRepository.findByProcessInstanceId(processInstance.getId());
            for (Task task : tasks) {
                task.setProcessInstanceId(processInstance.getId());
                task.setStatus(Task.TaskStatus.CREATED);
            }
            taskRepository.saveAll(tasks);
            
            return WorkflowResponse.fromEntity(savedWorkflow);
        }
        
        throw new RuntimeException("Workflow not found for process definition ID: " + processDefinitionId);
    }
    
    @Transactional
    public WorkflowResponse completeWorkflow(String processInstanceId) {
        log.info("Completing workflow with process instance ID: {}", processInstanceId);
        
        // Complete all remaining tasks
        List<Task> flowableTasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .list();
        
        for (Task flowableTask : flowableTasks) {
            taskService.complete(flowableTask.getId());
        }
        
        // Update workflow entity
        Optional<Workflow> workflowOpt = workflowRepository.findByProcessInstanceId(processInstanceId);
        if (workflowOpt.isPresent()) {
            Workflow workflow = workflowOpt.get();
            workflow.setEndTime(LocalDateTime.now());
            workflow.setStatus(Workflow.WorkflowStatus.COMPLETED);
            if (workflow.getStartTime() != null) {
                workflow.setDuration(java.time.Duration.between(workflow.getStartTime(), workflow.getEndTime()).toMillis());
            }
            
            Workflow savedWorkflow = workflowRepository.save(workflow);
            return WorkflowResponse.fromEntity(savedWorkflow);
        }
        
        throw new RuntimeException("Workflow not found for process instance ID: " + processInstanceId);
    }
    
    @Transactional
    public WorkflowResponse suspendWorkflow(String processInstanceId) {
        log.info("Suspending workflow with process instance ID: {}", processInstanceId);
        
        runtimeService.suspendProcessInstanceById(processInstanceId);
        
        Optional<Workflow> workflowOpt = workflowRepository.findByProcessInstanceId(processInstanceId);
        if (workflowOpt.isPresent()) {
            Workflow workflow = workflowOpt.get();
            workflow.setStatus(Workflow.WorkflowStatus.SUSPENDED);
            
            Workflow savedWorkflow = workflowRepository.save(workflow);
            return WorkflowResponse.fromEntity(savedWorkflow);
        }
        
        throw new RuntimeException("Workflow not found for process instance ID: " + processInstanceId);
    }
    
    @Transactional
    public WorkflowResponse activateWorkflow(String processInstanceId) {
        log.info("Activating workflow with process instance ID: {}", processInstanceId);
        
        runtimeService.activateProcessInstanceById(processInstanceId);
        
        Optional<Workflow> workflowOpt = workflowRepository.findByProcessInstanceId(processInstanceId);
        if (workflowOpt.isPresent()) {
            Workflow workflow = workflowOpt.get();
            workflow.setStatus(Workflow.WorkflowStatus.ACTIVE);
            
            Workflow savedWorkflow = workflowRepository.save(workflow);
            return WorkflowResponse.fromEntity(savedWorkflow);
        }
        
        throw new RuntimeException("Workflow not found for process instance ID: " + processInstanceId);
    }
    
    public List<WorkflowResponse> getAllWorkflows() {
        return workflowRepository.findAll().stream()
                .map(WorkflowResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    public Optional<WorkflowResponse> getWorkflowById(Long id) {
        return workflowRepository.findById(id)
                .map(WorkflowResponse::fromEntity);
    }
    
    public Optional<WorkflowResponse> getWorkflowByProcessInstanceId(String processInstanceId) {
        return workflowRepository.findByProcessInstanceId(processInstanceId)
                .map(WorkflowResponse::fromEntity);
    }
    
    private String generateBpmnXml(WorkflowRequest request) {
        StringBuilder bpmn = new StringBuilder();
        bpmn.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        bpmn.append("<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"\n");
        bpmn.append("             xmlns:flowable=\"http://flowable.org/bpmn\"\n");
        bpmn.append("             typeLanguage=\"http://www.w3.org/2001/XMLSchema\"\n");
        bpmn.append("             expressionLanguage=\"http://www.w3.org/1999/XPath\"\n");
        bpmn.append("             targetNamespace=\"http://flowable.org/processdef\"\n");
        bpmn.append("             xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        bpmn.append("             xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL http://www.omg.org/spec/BPMN/2.0/20100501/BPMN20.xsd\">\n");
        
        bpmn.append("  <process id=\"").append(request.getKey()).append("\" name=\"").append(request.getName()).append("\" isExecutable=\"true\">\n");
        
        // Add start event
        bpmn.append("    <startEvent id=\"startEvent\" name=\"Start\"/>\n");
        
        // Add tasks
        for (int i = 0; i < request.getTasks().size(); i++) {
            WorkflowRequest.TaskRequest task = request.getTasks().get(i);
            String taskId = "task" + (i + 1);
            String nextTaskId = i < request.getTasks().size() - 1 ? "task" + (i + 2) : "endEvent";
            
            bpmn.append("    <userTask id=\"").append(taskId).append("\" name=\"").append(task.getName()).append("\"");
            if (task.getAssignee() != null) {
                bpmn.append(" flowable:assignee=\"").append(task.getAssignee()).append("\"");
            }
            if (task.getPriority() != null) {
                bpmn.append(" flowable:priority=\"").append(task.getPriority()).append("\"");
            }
            bpmn.append("/>\n");
            
            // Add sequence flow
            bpmn.append("    <sequenceFlow id=\"flow").append(i + 1).append("\" sourceRef=\"");
            bpmn.append(i == 0 ? "startEvent" : "task" + i).append("\" targetRef=\"").append(taskId).append("\"/>\n");
        }
        
        // Add end event
        bpmn.append("    <endEvent id=\"endEvent\" name=\"End\"/>\n");
        
        // Add final sequence flow
        if (!request.getTasks().isEmpty()) {
            bpmn.append("    <sequenceFlow id=\"flow").append(request.getTasks().size() + 1)
                .append("\" sourceRef=\"task").append(request.getTasks().size())
                .append("\" targetRef=\"endEvent\"/>\n");
        } else {
            bpmn.append("    <sequenceFlow id=\"flow1\" sourceRef=\"startEvent\" targetRef=\"endEvent\"/>\n");
        }
        
        bpmn.append("  </process>\n");
        bpmn.append("</definitions>");
        
        return bpmn.toString();
    }
    
    private Task createTaskEntity(WorkflowRequest.TaskRequest taskRequest, Workflow workflow) {
        return Task.builder()
                .taskId(UUID.randomUUID().toString())
                .name(taskRequest.getName())
                .description(taskRequest.getDescription())
                .taskDefinitionKey(taskRequest.getTaskDefinitionKey())
                .assignee(taskRequest.getAssignee())
                .owner(taskRequest.getOwner())
                .priority(taskRequest.getPriority())
                .category(taskRequest.getCategory())
                .formKey(taskRequest.getFormKey())
                .parentTaskId(taskRequest.getParentTaskId())
                .tenantId(workflow.getTenantId())
                .status(Task.TaskStatus.CREATED)
                .workflow(workflow)
                .build();
    }
}