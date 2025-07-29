package com.workflow.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowRequest {
    
    @NotBlank(message = "Workflow name is required")
    private String name;
    
    private String description;
    
    @NotBlank(message = "Workflow key is required")
    private String key;
    
    private String version;
    
    private String category;
    
    private String tenantId;
    
    private String businessKey;
    
    private String startUserId;
    
    @NotNull(message = "Tasks list is required")
    private List<TaskRequest> tasks;
    
    private Map<String, Object> variables;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TaskRequest {
        
        @NotBlank(message = "Task name is required")
        private String name;
        
        private String description;
        
        @NotBlank(message = "Task definition key is required")
        private String taskDefinitionKey;
        
        private String assignee;
        
        private String owner;
        
        private Integer priority;
        
        private String category;
        
        private String formKey;
        
        private String parentTaskId;
        
        private Map<String, Object> variables;
    }
}