package com.example.flowableworkflow.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "workflow_definition")
public class WorkflowDefinition {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Workflow name is required")
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @NotBlank(message = "Process definition key is required")
    @Column(name = "process_definition_key", nullable = false, unique = true)
    private String processDefinitionKey;
    
    @Column(name = "version")
    private Integer version = 1;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private WorkflowStatus status = WorkflowStatus.DRAFT;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "tenant_id")
    private String tenantId;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();
    
    @Column(name = "last_modified_by")
    private String lastModifiedBy;
    
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;
    
    @Lob
    @Column(name = "bpmn_xml")
    private String bpmnXml;
    
    @OneToMany(mappedBy = "workflowDefinition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TaskDefinition> taskDefinitions = new HashSet<>();
    
    @OneToMany(mappedBy = "workflowDefinition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<WorkflowInstance> workflowInstances = new HashSet<>();
    
    // Constructors
    public WorkflowDefinition() {}
    
    public WorkflowDefinition(String name, String processDefinitionKey) {
        this.name = name;
        this.processDefinitionKey = processDefinitionKey;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }
    
    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }
    
    public Integer getVersion() {
        return version;
    }
    
    public void setVersion(Integer version) {
        this.version = version;
    }
    
    public WorkflowStatus getStatus() {
        return status;
    }
    
    public void setStatus(WorkflowStatus status) {
        this.status = status;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getTenantId() {
        return tenantId;
    }
    
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }
    
    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }
    
    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }
    
    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
    
    public String getBpmnXml() {
        return bpmnXml;
    }
    
    public void setBpmnXml(String bpmnXml) {
        this.bpmnXml = bpmnXml;
    }
    
    public Set<TaskDefinition> getTaskDefinitions() {
        return taskDefinitions;
    }
    
    public void setTaskDefinitions(Set<TaskDefinition> taskDefinitions) {
        this.taskDefinitions = taskDefinitions;
    }
    
    public Set<WorkflowInstance> getWorkflowInstances() {
        return workflowInstances;
    }
    
    public void setWorkflowInstances(Set<WorkflowInstance> workflowInstances) {
        this.workflowInstances = workflowInstances;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = LocalDateTime.now();
    }
}