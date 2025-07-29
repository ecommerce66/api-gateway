package com.example.flowableworkflow.service;

import com.example.flowableworkflow.entity.WorkflowDefinition;
import com.example.flowableworkflow.entity.WorkflowStatus;
import com.example.flowableworkflow.repository.WorkflowDefinitionRepository;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WorkflowDefinitionService {
    
    @Autowired
    private WorkflowDefinitionRepository workflowDefinitionRepository;
    
    @Autowired
    private RepositoryService repositoryService;
    
    @Autowired
    private BpmnGeneratorService bpmnGeneratorService;
    
    public WorkflowDefinition createWorkflowDefinition(WorkflowDefinition workflowDefinition) {
        // Validate unique process definition key
        if (workflowDefinitionRepository.existsByProcessDefinitionKey(workflowDefinition.getProcessDefinitionKey())) {
            throw new IllegalArgumentException("Process definition key already exists: " + workflowDefinition.getProcessDefinitionKey());
        }
        
        workflowDefinition.setCreatedDate(LocalDateTime.now());
        workflowDefinition.setStatus(WorkflowStatus.DRAFT);
        
        return workflowDefinitionRepository.save(workflowDefinition);
    }
    
    public WorkflowDefinition updateWorkflowDefinition(Long id, WorkflowDefinition workflowDefinition) {
        WorkflowDefinition existingWorkflow = workflowDefinitionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Workflow definition not found with id: " + id));
        
        existingWorkflow.setName(workflowDefinition.getName());
        existingWorkflow.setDescription(workflowDefinition.getDescription());
        existingWorkflow.setCategory(workflowDefinition.getCategory());
        existingWorkflow.setLastModifiedDate(LocalDateTime.now());
        existingWorkflow.setLastModifiedBy(workflowDefinition.getLastModifiedBy());
        
        return workflowDefinitionRepository.save(existingWorkflow);
    }
    
    public WorkflowDefinition activateWorkflowDefinition(Long id) {
        WorkflowDefinition workflowDefinition = workflowDefinitionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Workflow definition not found with id: " + id));
        
        // Generate BPMN XML if not exists
        if (workflowDefinition.getBpmnXml() == null || workflowDefinition.getBpmnXml().isEmpty()) {
            String bpmnXml = bpmnGeneratorService.generateBpmnXml(workflowDefinition);
            workflowDefinition.setBpmnXml(bpmnXml);
        }
        
        // Deploy to Flowable
        Deployment deployment = repositoryService.createDeployment()
                .name(workflowDefinition.getName())
                .addString(workflowDefinition.getProcessDefinitionKey() + ".bpmn20.xml", workflowDefinition.getBpmnXml())
                .tenantId(workflowDefinition.getTenantId())
                .deploy();
        
        workflowDefinition.setStatus(WorkflowStatus.ACTIVE);
        workflowDefinition.setLastModifiedDate(LocalDateTime.now());
        
        return workflowDefinitionRepository.save(workflowDefinition);
    }
    
    public WorkflowDefinition suspendWorkflowDefinition(Long id) {
        WorkflowDefinition workflowDefinition = workflowDefinitionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Workflow definition not found with id: " + id));
        
        // Suspend in Flowable
        repositoryService.suspendProcessDefinitionByKey(workflowDefinition.getProcessDefinitionKey());
        
        workflowDefinition.setStatus(WorkflowStatus.SUSPENDED);
        workflowDefinition.setLastModifiedDate(LocalDateTime.now());
        
        return workflowDefinitionRepository.save(workflowDefinition);
    }
    
    public void deleteWorkflowDefinition(Long id) {
        WorkflowDefinition workflowDefinition = workflowDefinitionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Workflow definition not found with id: " + id));
        
        // Delete from Flowable repository if deployed
        if (workflowDefinition.getStatus() == WorkflowStatus.ACTIVE || 
            workflowDefinition.getStatus() == WorkflowStatus.SUSPENDED) {
            
            repositoryService.createDeploymentQuery()
                    .processDefinitionKey(workflowDefinition.getProcessDefinitionKey())
                    .list()
                    .forEach(deployment -> 
                            repositoryService.deleteDeployment(deployment.getId(), true));
        }
        
        workflowDefinitionRepository.delete(workflowDefinition);
    }
    
    @Transactional(readOnly = true)
    public Optional<WorkflowDefinition> getWorkflowDefinitionById(Long id) {
        return workflowDefinitionRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<WorkflowDefinition> getWorkflowDefinitionByKey(String processDefinitionKey) {
        return workflowDefinitionRepository.findByProcessDefinitionKey(processDefinitionKey);
    }
    
    @Transactional(readOnly = true)
    public List<WorkflowDefinition> getAllWorkflowDefinitions() {
        return workflowDefinitionRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<WorkflowDefinition> getWorkflowDefinitionsByStatus(WorkflowStatus status) {
        return workflowDefinitionRepository.findByStatus(status);
    }
    
    @Transactional(readOnly = true)
    public List<WorkflowDefinition> getWorkflowDefinitionsByCreatedBy(String createdBy) {
        return workflowDefinitionRepository.findByCreatedBy(createdBy);
    }
    
    @Transactional(readOnly = true)
    public List<WorkflowDefinition> searchWorkflowDefinitionsByName(String name) {
        return workflowDefinitionRepository.findByNameContaining(name);
    }
    
    public WorkflowDefinition createNewVersion(Long originalId) {
        WorkflowDefinition original = workflowDefinitionRepository.findById(originalId)
                .orElseThrow(() -> new IllegalArgumentException("Workflow definition not found with id: " + originalId));
        
        // Find the latest version
        List<WorkflowDefinition> versions = workflowDefinitionRepository
                .findByProcessDefinitionKeyOrderByVersionDesc(original.getProcessDefinitionKey());
        
        Integer newVersion = versions.isEmpty() ? 1 : versions.get(0).getVersion() + 1;
        
        WorkflowDefinition newVersionWorkflow = new WorkflowDefinition();
        newVersionWorkflow.setName(original.getName());
        newVersionWorkflow.setDescription(original.getDescription());
        newVersionWorkflow.setProcessDefinitionKey(original.getProcessDefinitionKey());
        newVersionWorkflow.setVersion(newVersion);
        newVersionWorkflow.setCategory(original.getCategory());
        newVersionWorkflow.setTenantId(original.getTenantId());
        newVersionWorkflow.setStatus(WorkflowStatus.DRAFT);
        newVersionWorkflow.setCreatedDate(LocalDateTime.now());
        
        return workflowDefinitionRepository.save(newVersionWorkflow);
    }
}