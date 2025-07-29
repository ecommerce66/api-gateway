package com.example.flowableworkflow.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "task_definition")
public class TaskDefinition {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Task name is required")
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @NotBlank(message = "Task definition key is required")
    @Column(name = "task_definition_key", nullable = false)
    private String taskDefinitionKey;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "task_type")
    private TaskType taskType = TaskType.USER_TASK;
    
    @Column(name = "assignee")
    private String assignee;
    
    @Column(name = "candidate_users")
    private String candidateUsers;
    
    @Column(name = "candidate_groups")
    private String candidateGroups;
    
    @Column(name = "priority")
    private Integer priority = 50;
    
    @Column(name = "due_date_expression")
    private String dueDateExpression;
    
    @Column(name = "form_key")
    private String formKey;
    
    @Column(name = "documentation")
    private String documentation;
    
    @Column(name = "execution_listeners")
    private String executionListeners;
    
    @Column(name = "task_listeners")
    private String taskListeners;
    
    @Column(name = "input_parameters")
    private String inputParameters;
    
    @Column(name = "output_parameters")
    private String outputParameters;
    
    @Column(name = "sequence_number")
    private Integer sequenceNumber;
    
    @Column(name = "is_parallel")
    private Boolean isParallel = false;
    
    @Column(name = "is_mandatory")
    private Boolean isMandatory = true;
    
    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();
    
    @Column(name = "created_by")
    private String createdBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_definition_id")
    @NotNull
    private WorkflowDefinition workflowDefinition;
    
    @OneToMany(mappedBy = "taskDefinition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TaskInstance> taskInstances = new HashSet<>();
    
    // Constructors
    public TaskDefinition() {}
    
    public TaskDefinition(String name, String taskDefinitionKey, WorkflowDefinition workflowDefinition) {
        this.name = name;
        this.taskDefinitionKey = taskDefinitionKey;
        this.workflowDefinition = workflowDefinition;
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
    
    public TaskType getTaskType() {
        return taskType;
    }
    
    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }
    
    public String getAssignee() {
        return assignee;
    }
    
    public void setAssignee(String assignee) {
        this.assignee = assignee;
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
    
    public String getDueDateExpression() {
        return dueDateExpression;
    }
    
    public void setDueDateExpression(String dueDateExpression) {
        this.dueDateExpression = dueDateExpression;
    }
    
    public String getFormKey() {
        return formKey;
    }
    
    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }
    
    public String getDocumentation() {
        return documentation;
    }
    
    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }
    
    public String getExecutionListeners() {
        return executionListeners;
    }
    
    public void setExecutionListeners(String executionListeners) {
        this.executionListeners = executionListeners;
    }
    
    public String getTaskListeners() {
        return taskListeners;
    }
    
    public void setTaskListeners(String taskListeners) {
        this.taskListeners = taskListeners;
    }
    
    public String getInputParameters() {
        return inputParameters;
    }
    
    public void setInputParameters(String inputParameters) {
        this.inputParameters = inputParameters;
    }
    
    public String getOutputParameters() {
        return outputParameters;
    }
    
    public void setOutputParameters(String outputParameters) {
        this.outputParameters = outputParameters;
    }
    
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }
    
    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
    
    public Boolean getIsParallel() {
        return isParallel;
    }
    
    public void setIsParallel(Boolean isParallel) {
        this.isParallel = isParallel;
    }
    
    public Boolean getIsMandatory() {
        return isMandatory;
    }
    
    public void setIsMandatory(Boolean isMandatory) {
        this.isMandatory = isMandatory;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public WorkflowDefinition getWorkflowDefinition() {
        return workflowDefinition;
    }
    
    public void setWorkflowDefinition(WorkflowDefinition workflowDefinition) {
        this.workflowDefinition = workflowDefinition;
    }
    
    public Set<TaskInstance> getTaskInstances() {
        return taskInstances;
    }
    
    public void setTaskInstances(Set<TaskInstance> taskInstances) {
        this.taskInstances = taskInstances;
    }
}