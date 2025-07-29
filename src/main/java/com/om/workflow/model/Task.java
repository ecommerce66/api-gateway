package com.om.workflow.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "tasks")
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Task name is required")
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description", length = 4000)
    private String description;
    
    @Column(name = "task_definition_key")
    private String taskDefinitionKey;
    
    @Column(name = "process_instance_id")
    private String processInstanceId;
    
    @Column(name = "execution_id")
    private String executionId;
    
    @Column(name = "process_definition_id")
    private String processDefinitionId;
    
    @Column(name = "task_id")
    private String taskId;
    
    @Column(name = "parent_task_id")
    private String parentTaskId;
    
    @Column(name = "form_key")
    private String formKey;
    
    @Column(name = "priority")
    private Integer priority;
    
    @Column(name = "due_date")
    private LocalDateTime dueDate;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "tenant_id")
    private String tenantId;
    
    @Column(name = "assignee")
    private String assignee;
    
    @Column(name = "owner")
    private String owner;
    
    @Column(name = "delegation_state")
    @Enumerated(EnumType.STRING)
    private DelegationState delegationState;
    
    @Column(name = "candidate_users")
    private String candidateUsers;
    
    @Column(name = "candidate_groups")
    private String candidateGroups;
    
    @Column(name = "claim_time")
    private LocalDateTime claimTime;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
    
    @Column(name = "suspension_state")
    private Integer suspensionState;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id")
    private Workflow workflow;
    
    @ElementCollection
    @CollectionTable(name = "task_variables", joinColumns = @JoinColumn(name = "task_id"))
    @MapKeyColumn(name = "variable_name")
    @Column(name = "variable_value")
    private Map<String, String> variables = new HashMap<>();
    
    // Constructors
    public Task() {
        this.createTime = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
        this.status = TaskStatus.CREATED;
        this.priority = 50; // Default priority
        this.delegationState = DelegationState.PENDING;
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
    
    public String getTaskDefinitionKey() {
        return taskDefinitionKey;
    }
    
    public void setTaskDefinitionKey(String taskDefinitionKey) {
        this.taskDefinitionKey = taskDefinitionKey;
    }
    
    public String getProcessInstanceId() {
        return processInstanceId;
    }
    
    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }
    
    public String getExecutionId() {
        return executionId;
    }
    
    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }
    
    public String getProcessDefinitionId() {
        return processDefinitionId;
    }
    
    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }
    
    public String getTaskId() {
        return taskId;
    }
    
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    
    public String getParentTaskId() {
        return parentTaskId;
    }
    
    public void setParentTaskId(String parentTaskId) {
        this.parentTaskId = parentTaskId;
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
    
    public DelegationState getDelegationState() {
        return delegationState;
    }
    
    public void setDelegationState(DelegationState delegationState) {
        this.delegationState = delegationState;
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
    
    public LocalDateTime getClaimTime() {
        return claimTime;
    }
    
    public void setClaimTime(LocalDateTime claimTime) {
        this.claimTime = claimTime;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public Integer getSuspensionState() {
        return suspensionState;
    }
    
    public void setSuspensionState(Integer suspensionState) {
        this.suspensionState = suspensionState;
    }
    
    public TaskStatus getStatus() {
        return status;
    }
    
    public void setStatus(TaskStatus status) {
        this.status = status;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public Workflow getWorkflow() {
        return workflow;
    }
    
    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }
    
    public Map<String, String> getVariables() {
        return variables;
    }
    
    public void setVariables(Map<String, String> variables) {
        this.variables = variables;
    }
    
    public void addVariable(String key, String value) {
        this.variables.put(key, value);
    }
}