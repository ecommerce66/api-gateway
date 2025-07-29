package com.workflow.dto;

import com.workflow.entity.Task;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponse {
    
    private Long id;
    private String taskId;
    private String name;
    private String description;
    private String taskDefinitionKey;
    private String processInstanceId;
    private String executionId;
    private String processDefinitionId;
    private String scopeId;
    private String subScopeId;
    private String scopeType;
    private String scopeDefinitionId;
    private String propagatedStageInstanceId;
    private String taskDefinitionId;
    private String eventName;
    private String eventType;
    private LocalDateTime created;
    private LocalDateTime dueDate;
    private LocalDateTime claimTime;
    private Long workTimeInMillis;
    private String assignee;
    private String owner;
    private Task.DelegationState delegation;
    private Integer priority;
    private String category;
    private String formKey;
    private String parentTaskId;
    private String tenantId;
    private Task.TaskStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long duration;
    private String deleteReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Map<String, String> variables;
    
    public static TaskResponse fromEntity(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .taskId(task.getTaskId())
                .name(task.getName())
                .description(task.getDescription())
                .taskDefinitionKey(task.getTaskDefinitionKey())
                .processInstanceId(task.getProcessInstanceId())
                .executionId(task.getExecutionId())
                .processDefinitionId(task.getProcessDefinitionId())
                .scopeId(task.getScopeId())
                .subScopeId(task.getSubScopeId())
                .scopeType(task.getScopeType())
                .scopeDefinitionId(task.getScopeDefinitionId())
                .propagatedStageInstanceId(task.getPropagatedStageInstanceId())
                .taskDefinitionId(task.getTaskDefinitionId())
                .eventName(task.getEventName())
                .eventType(task.getEventType())
                .created(task.getCreated())
                .dueDate(task.getDueDate())
                .claimTime(task.getClaimTime())
                .workTimeInMillis(task.getWorkTimeInMillis())
                .assignee(task.getAssignee())
                .owner(task.getOwner())
                .delegation(task.getDelegation())
                .priority(task.getPriority())
                .category(task.getCategory())
                .formKey(task.getFormKey())
                .parentTaskId(task.getParentTaskId())
                .tenantId(task.getTenantId())
                .status(task.getStatus())
                .startTime(task.getStartTime())
                .endTime(task.getEndTime())
                .duration(task.getDuration())
                .deleteReason(task.getDeleteReason())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .variables(task.getVariables())
                .build();
    }
}