package com.workflow.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "task_id", nullable = false, unique = true)
    private String taskId;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "task_definition_key")
    private String taskDefinitionKey;
    
    @Column(name = "process_instance_id", nullable = false)
    private String processInstanceId;
    
    @Column(name = "execution_id")
    private String executionId;
    
    @Column(name = "process_definition_id")
    private String processDefinitionId;
    
    @Column(name = "scope_id")
    private String scopeId;
    
    @Column(name = "sub_scope_id")
    private String subScopeId;
    
    @Column(name = "scope_type")
    private String scopeType;
    
    @Column(name = "scope_definition_id")
    private String scopeDefinitionId;
    
    @Column(name = "propagated_stage_instance_id")
    private String propagatedStageInstanceId;
    
    @Column(name = "task_definition_id")
    private String taskDefinitionId;
    
    @Column(name = "event_name")
    private String eventName;
    
    @Column(name = "event_type")
    private String eventType;
    
    @Column(name = "created")
    private LocalDateTime created;
    
    @Column(name = "due_date")
    private LocalDateTime dueDate;
    
    @Column(name = "claim_time")
    private LocalDateTime claimTime;
    
    @Column(name = "work_time_in_millis")
    private Long workTimeInMillis;
    
    @Column(name = "assignee")
    private String assignee;
    
    @Column(name = "owner")
    private String owner;
    
    @Column(name = "delegation")
    @Enumerated(EnumType.STRING)
    private DelegationState delegation;
    
    @Column(name = "priority")
    private Integer priority;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "form_key")
    private String formKey;
    
    @Column(name = "parent_task_id")
    private String parentTaskId;
    
    @Column(name = "tenant_id")
    private String tenantId;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "duration")
    private Long duration;
    
    @Column(name = "delete_reason")
    private String deleteReason;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id")
    private Workflow workflow;
    
    @ElementCollection
    @CollectionTable(name = "task_variables", 
                    joinColumns = @JoinColumn(name = "task_id"))
    @MapKeyColumn(name = "variable_name")
    @Column(name = "variable_value")
    private Map<String, String> variables;
    
    public enum TaskStatus {
        CREATED, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED, SUSPENDED
    }
    
    public enum DelegationState {
        PENDING, RESOLVED
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (created == null) {
            created = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}