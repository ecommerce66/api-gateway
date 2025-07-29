package com.example.flowableworkflow.repository;

import com.example.flowableworkflow.entity.WorkflowInstance;
import com.example.flowableworkflow.entity.WorkflowInstanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowInstanceRepository extends JpaRepository<WorkflowInstance, Long> {
    
    Optional<WorkflowInstance> findByProcessInstanceId(String processInstanceId);
    
    List<WorkflowInstance> findByStatus(WorkflowInstanceStatus status);
    
    List<WorkflowInstance> findByStartedBy(String startedBy);
    
    List<WorkflowInstance> findByWorkflowDefinitionId(Long workflowDefinitionId);
    
    List<WorkflowInstance> findByBusinessKey(String businessKey);
    
    List<WorkflowInstance> findByTenantId(String tenantId);
    
    @Query("SELECT w FROM WorkflowInstance w WHERE w.startTime BETWEEN :startDate AND :endDate")
    List<WorkflowInstance> findByStartTimeBetween(
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT w FROM WorkflowInstance w WHERE w.status = :status AND w.startedBy = :user")
    List<WorkflowInstance> findByStatusAndStartedBy(
            @Param("status") WorkflowInstanceStatus status, 
            @Param("user") String user);
    
    @Query("SELECT w FROM WorkflowInstance w WHERE w.workflowDefinition.processDefinitionKey = :processKey")
    List<WorkflowInstance> findByWorkflowProcessDefinitionKey(@Param("processKey") String processDefinitionKey);
    
    @Query("SELECT COUNT(w) FROM WorkflowInstance w WHERE w.status = :status")
    Long countByStatus(@Param("status") WorkflowInstanceStatus status);
    
    @Query("SELECT w FROM WorkflowInstance w WHERE w.name LIKE %:name%")
    List<WorkflowInstance> findByNameContaining(@Param("name") String name);
}