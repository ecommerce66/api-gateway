package com.workflow.dto;

import com.workflow.entity.Workflow;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowResponse {
    
    private Long id;
    private String processDefinitionId;
    private String processInstanceId;
    private String name;
    private String description;
    private String version;
    private String category;
    private String key;
    private String deploymentId;
    private String resourceName;
    private String diagramResourceName;
    private String tenantId;
    private Workflow.WorkflowStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long duration;
    private String startUserId;
    private String businessKey;
    private String parentProcessInstanceId;
    private String superProcessInstanceId;
    private String rootProcessInstanceId;
    private String deleteReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<TaskResponse> tasks;
    
    public static WorkflowResponse fromEntity(Workflow workflow) {
        return WorkflowResponse.builder()
                .id(workflow.getId())
                .processDefinitionId(workflow.getProcessDefinitionId())
                .processInstanceId(workflow.getProcessInstanceId())
                .name(workflow.getName())
                .description(workflow.getDescription())
                .version(workflow.getVersion())
                .category(workflow.getCategory())
                .key(workflow.getKey())
                .deploymentId(workflow.getDeploymentId())
                .resourceName(workflow.getResourceName())
                .diagramResourceName(workflow.getDiagramResourceName())
                .tenantId(workflow.getTenantId())
                .status(workflow.getStatus())
                .startTime(workflow.getStartTime())
                .endTime(workflow.getEndTime())
                .duration(workflow.getDuration())
                .startUserId(workflow.getStartUserId())
                .businessKey(workflow.getBusinessKey())
                .parentProcessInstanceId(workflow.getParentProcessInstanceId())
                .superProcessInstanceId(workflow.getSuperProcessInstanceId())
                .rootProcessInstanceId(workflow.getRootProcessInstanceId())
                .deleteReason(workflow.getDeleteReason())
                .createdAt(workflow.getCreatedAt())
                .updatedAt(workflow.getUpdatedAt())
                .build();
    }
}