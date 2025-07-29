package com.workflow.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "workflows")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Workflow {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "process_definition_id", nullable = false)
    private String processDefinitionId;
    
    @Column(name = "process_instance_id", nullable = false)
    private String processInstanceId;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "version")
    private String version;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "key", nullable = false)
    private String key;
    
    @Column(name = "deployment_id")
    private String deploymentId;
    
    @Column(name = "resource_name")
    private String resourceName;
    
    @Column(name = "diagram_resource_name")
    private String diagramResourceName;
    
    @Column(name = "tenant_id")
    private String tenantId;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private WorkflowStatus status;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "duration")
    private Long duration;
    
    @Column(name = "start_user_id")
    private String startUserId;
    
    @Column(name = "business_key")
    private String businessKey;
    
    @Column(name = "parent_process_instance_id")
    private String parentProcessInstanceId;
    
    @Column(name = "super_process_instance_id")
    private String superProcessInstanceId;
    
    @Column(name = "root_process_instance_id")
    private String rootProcessInstanceId;
    
    @Column(name = "delete_reason")
    private String deleteReason;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> tasks;
    
    public enum WorkflowStatus {
        ACTIVE, SUSPENDED, COMPLETED, TERMINATED, DELETED
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}