package com.om.workflow.dto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Map;

public class TaskRequest {
    
    @NotBlank(message = "Task name is required")
    private String name;
    
    private String description;
    private String taskDefinitionKey;
    private String formKey;
    private Integer priority;
    private LocalDateTime dueDate;
    private String category;
    private String tenantId;
    private String assignee;
    private String owner;
    private String candidateUsers;
    private String candidateGroups;
    private Map<String, String> variables;
    
    // Getters and Setters
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
    
    public String getTaskDefinitionKey() {
        return taskDefinitionKey;
    }
    
    public void setTaskDefinitionKey(String taskDefinitionKey) {
        this.taskDefinitionKey = taskDefinitionKey;
    }
    
    public String getFormKey() {
        return formKey;
    }
    
    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }
    
    public Integer getPriority() {
        return priority;
    }
    
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    
    public LocalDateTime getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
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
    
    public String getAssignee() {
        return assignee;
    }
    
    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }
    
    public String getOwner() {
        return owner;
    }
    
    public void setOwner(String owner) {
        this.owner = owner;
    }
    
    public String getCandidateUsers() {
        return candidateUsers;
    }
    
    public void setCandidateUsers(String candidateUsers) {
        this.candidateUsers = candidateUsers;
    }
    
    public String getCandidateGroups() {
        return candidateGroups;
    }
    
    public void setCandidateGroups(String candidateGroups) {
        this.candidateGroups = candidateGroups;
    }
    
    public Map<String, String> getVariables() {
        return variables;
    }
    
    public void setVariables(Map<String, String> variables) {
        this.variables = variables;
    }
}