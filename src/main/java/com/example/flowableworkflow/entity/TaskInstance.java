package com.example.flowableworkflow.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "task_instance")
public class TaskInstance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "flowable_task_id", unique = true)
    private String flowableTaskId;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskInstanceStatus status = TaskInstanceStatus.CREATED;
    
    @Column(name = "assignee")
    private String assignee;
    
    @Column(name = "owner")
    private String owner;
    
    @Column(name = "candidate_users")
    private String candidateUsers;
    
    @Column(name = "candidate_groups")
    private String candidateGroups;
    
    @Column(name = "priority")
    private Integer priority = 50;
    
    @Column(name = "due_date")
    private LocalDateTime dueDate;
    
    @Column(name = "follow_up_date")
    private LocalDateTime followUpDate;
    
    @Column(name = "created_time")
    private LocalDateTime createdTime = LocalDateTime.now();
    
    @Column(name = "claim_time")
    private LocalDateTime claimTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "duration_in_millis")
    private Long durationInMillis;
    
    @Column(name = "form_key")
    private String formKey;
    
    @Column(name = "tenant_id")
    private String tenantId;
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "task_instance_variables", 
                     joinColumns = @JoinColumn(name = "task_instance_id"))
    @MapKeyColumn(name = "variable_name")
    @Column(name = "variable_value")
    private Map<String, String> localVariables = new HashMap<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_definition_id")
    private TaskDefinition taskDefinition;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_instance_id")
    @NotNull
    private WorkflowInstance workflowInstance;
    
    // Constructors
    public TaskInstance() {}
    
    public TaskInstance(WorkflowInstance workflowInstance, String name) {
        this.workflowInstance = workflowInstance;
        this.name = name;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getFlowableTaskId() {
        return flowableTaskId;
    }
    
    public void setFlowableTaskId(String flowableTaskId) {
        this.flowableTaskId = flowableTaskId;
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
    
    public TaskInstanceStatus getStatus() {
        return status;
    }
    
    public void setStatus(TaskInstanceStatus status) {
        this.status = status;
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
    
    public LocalDateTime getFollowUpDate() {
        return followUpDate;
    }
    
    public void setFollowUpDate(LocalDateTime followUpDate) {
        this.followUpDate = followUpDate;
    }
    
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }
    
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
    
    public LocalDateTime getClaimTime() {
        return claimTime;
    }
    
    public void setClaimTime(LocalDateTime claimTime) {
        this.claimTime = claimTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        if (this.createdTime != null && endTime != null) {
            this.durationInMillis = java.time.Duration.between(this.createdTime, endTime).toMillis();
        }
    }
    
    public Long getDurationInMillis() {
        return durationInMillis;
    }
    
    public void setDurationInMillis(Long durationInMillis) {
        this.durationInMillis = durationInMillis;
    }
    
    public String getFormKey() {
        return formKey;
    }
    
    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }
    
    public String getTenantId() {
        return tenantId;
    }
    
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    
    public Map<String, String> getLocalVariables() {
        return localVariables;
    }
    
    public void setLocalVariables(Map<String, String> localVariables) {
        this.localVariables = localVariables;
    }
    
    public void addLocalVariable(String name, String value) {
        this.localVariables.put(name, value);
    }
    
    public TaskDefinition getTaskDefinition() {
        return taskDefinition;
    }
    
    public void setTaskDefinition(TaskDefinition taskDefinition) {
        this.taskDefinition = taskDefinition;
    }
    
    public WorkflowInstance getWorkflowInstance() {
        return workflowInstance;
    }
    
    public void setWorkflowInstance(WorkflowInstance workflowInstance) {
        this.workflowInstance = workflowInstance;
    }
}