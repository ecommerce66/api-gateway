package com.example.flowableworkflow.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "workflow_instance")
public class WorkflowInstance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "process_instance_id", unique = true)
    private String processInstanceId;
    
    @Column(name = "business_key")
    private String businessKey;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private WorkflowInstanceStatus status = WorkflowInstanceStatus.RUNNING;
    
    @Column(name = "started_by")
    private String startedBy;
    
    @Column(name = "start_time")
    private LocalDateTime startTime = LocalDateTime.now();
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "duration_in_millis")
    private Long durationInMillis;
    
    @Column(name = "tenant_id")
    private String tenantId;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "workflow_instance_variables", 
                     joinColumns = @JoinColumn(name = "workflow_instance_id"))
    @MapKeyColumn(name = "variable_name")
    @Column(name = "variable_value")
    private Map<String, String> variables = new HashMap<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_definition_id")
    @NotNull
    private WorkflowDefinition workflowDefinition;
    
    @OneToMany(mappedBy = "workflowInstance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TaskInstance> taskInstances = new HashSet<>();
    
    // Constructors
    public WorkflowInstance() {}
    
    public WorkflowInstance(WorkflowDefinition workflowDefinition, String startedBy) {
        this.workflowDefinition = workflowDefinition;
        this.startedBy = startedBy;
        this.name = workflowDefinition.getName() + " Instance";
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getProcessInstanceId() {
        return processInstanceId;
    }
    
    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }
    
    public String getBusinessKey() {
        return businessKey;
    }
    
    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }
    
    public WorkflowInstanceStatus getStatus() {
        return status;
    }
    
    public void setStatus(WorkflowInstanceStatus status) {
        this.status = status;
    }
    
    public String getStartedBy() {
        return startedBy;
    }
    
    public void setStartedBy(String startedBy) {
        this.startedBy = startedBy;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        if (this.startTime != null && endTime != null) {
            this.durationInMillis = java.time.Duration.between(this.startTime, endTime).toMillis();
        }
    }
    
    public Long getDurationInMillis() {
        return durationInMillis;
    }
    
    public void setDurationInMillis(Long durationInMillis) {
        this.durationInMillis = durationInMillis;
    }
    
    public String getTenantId() {
        return tenantId;
    }
    
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
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
    
    public Map<String, String> getVariables() {
        return variables;
    }
    
    public void setVariables(Map<String, String> variables) {
        this.variables = variables;
    }
    
    public void addVariable(String name, String value) {
        this.variables.put(name, value);
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