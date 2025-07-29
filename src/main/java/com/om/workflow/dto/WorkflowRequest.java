package com.om.workflow.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

public class WorkflowRequest {
    
    @NotBlank(message = "Process definition key is required")
    private String processDefinitionKey;
    
    @NotBlank(message = "Process definition name is required")
    private String processDefinitionName;
    
    private String description;
    private String category;
    private String tenantId;
    private String versionTag;
    private Integer historyLevel;
    
    @NotEmpty(message = "At least one task is required")
    private List<TaskRequest> tasks;
    
    // Getters and Setters
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
    
    public List<TaskRequest> getTasks() {
        return tasks;
    }
    
    public void setTasks(List<TaskRequest> tasks) {
        this.tasks = tasks;
    }
}