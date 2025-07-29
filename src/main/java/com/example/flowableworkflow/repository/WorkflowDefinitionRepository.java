package com.example.flowableworkflow.repository;

import com.example.flowableworkflow.entity.WorkflowDefinition;
import com.example.flowableworkflow.entity.WorkflowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowDefinitionRepository extends JpaRepository<WorkflowDefinition, Long> {
    
    Optional<WorkflowDefinition> findByProcessDefinitionKey(String processDefinitionKey);
    
    List<WorkflowDefinition> findByStatus(WorkflowStatus status);
    
    List<WorkflowDefinition> findByCreatedBy(String createdBy);
    
    List<WorkflowDefinition> findByCategory(String category);
    
    List<WorkflowDefinition> findByTenantId(String tenantId);
    
    @Query("SELECT w FROM WorkflowDefinition w WHERE w.name LIKE %:name%")
    List<WorkflowDefinition> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT w FROM WorkflowDefinition w WHERE w.processDefinitionKey = :key AND w.version = :version")
    Optional<WorkflowDefinition> findByProcessDefinitionKeyAndVersion(
            @Param("key") String processDefinitionKey, 
            @Param("version") Integer version);
    
    @Query("SELECT w FROM WorkflowDefinition w WHERE w.processDefinitionKey = :key ORDER BY w.version DESC")
    List<WorkflowDefinition> findByProcessDefinitionKeyOrderByVersionDesc(@Param("key") String processDefinitionKey);
    
    boolean existsByProcessDefinitionKey(String processDefinitionKey);
}