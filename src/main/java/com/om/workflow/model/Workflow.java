package com.om.workflow.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workflows")
public class Workflow {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Process definition key is required")
    @Column(name = "process_definition_key", nullable = false, unique = true)
    private String processDefinitionKey;
    
    @NotBlank(message = "Process definition name is required")
    @Column(name = "process_definition_name", nullable = false)
    private String processDefinitionName;
    
    @Column(name = "process_definition_version")
    private Integer processDefinitionVersion;
    
    @Column(name = "process_definition_id")
    private String processDefinitionId;
    
    @Column(name = "deployment_id")
    private String deploymentId;
    
    @Column(name = "resource_name")
    private String resourceName;
    
    @Column(name = "diagram_resource_name")
    private String diagramResourceName;
    
    @Column(name = "description", length = 4000)
    private String description;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "tenant_id")
    private String tenantId;
    
    @Column(name = "version_tag")
    private String versionTag;
    
    @Column(name = "history_level")
    private Integer historyLevel;
    
    @Column(name = "suspension_state")
    private Integer suspensionState;
    
    @Column(name = "is_identity_links_initialized")
    private Boolean isIdentityLinksInitialized;
    
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private WorkflowStatus status;
    
    @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<>();
    
    // Constructors
    public Workflow() {
        this.createdDate = LocalDateTime.now();
        this.lastModifiedDate = LocalDateTime.now();
        this.status = WorkflowStatus.DRAFT;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }
    
    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }
    
    public String getProcessDefinitionName() {
        return processDefinitionName;
    }
    
    public void setProcessDefinitionName(String processDefinitionName) {
        this.processDefinitionName = processDefinitionName;
    }
    
    public Integer getProcessDefinitionVersion() {
        return processDefinitionVersion;
    }
    
    public void setProcessDefinitionVersion(Integer processDefinitionVersion) {
        this.processDefinitionVersion = processDefinitionVersion;
    }
    
    public String getProcessDefinitionId() {
        return processDefinitionId;
    }
    
    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }
    
    public String getDeploymentId() {
        return deploymentId;
    }
    
    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }
    
    public String getResourceName() {
        return resourceName;
    }
    
    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }
    
    public String getDiagramResourceName() {
        return diagramResourceName;
    }
    
    public void setDiagramResourceName(String diagramResourceName) {
        this.diagramResourceName = diagramResourceName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
    
    public String getVersionTag() {
        return versionTag;
    }
    
    public void setVersionTag(String versionTag) {
        this.versionTag = versionTag;
    }
    
    public Integer getHistoryLevel() {
        return historyLevel;
    }
    
    public void setHistoryLevel(Integer historyLevel) {
        this.historyLevel = historyLevel;
    }
    
    public Integer getSuspensionState() {
        return suspensionState;
    }
    
    public void setSuspensionState(Integer suspensionState) {
        this.suspensionState = suspensionState;
    }
    
    public Boolean getIsIdentityLinksInitialized() {
        return isIdentityLinksInitialized;
    }
    
    public void setIsIdentityLinksInitialized(Boolean isIdentityLinksInitialized) {
        this.isIdentityLinksInitialized = isIdentityLinksInitialized;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }
    
    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
    
    public WorkflowStatus getStatus() {
        return status;
    }
    
    public void setStatus(WorkflowStatus status) {
        this.status = status;
        this.lastModifiedDate = LocalDateTime.now();
    }
    
    public List<Task> getTasks() {
        return tasks;
    }
    
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
    
    public void addTask(Task task) {
        task.setWorkflow(this);
        this.tasks.add(task);
    }
}