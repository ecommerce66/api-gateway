package com.om.workflow.repository;

import com.om.workflow.model.Workflow;
import com.om.workflow.model.WorkflowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, Long> {
    
    Optional<Workflow> findByProcessDefinitionKey(String processDefinitionKey);
    
    List<Workflow> findByStatus(WorkflowStatus status);
    
    List<Workflow> findByTenantId(String tenantId);
    
    @Query("SELECT w FROM Workflow w WHERE w.processDefinitionName LIKE %:name%")
    List<Workflow> findByProcessDefinitionNameContaining(@Param("name") String name);
    
    @Query("SELECT w FROM Workflow w WHERE w.category = :category")
    List<Workflow> findByCategory(@Param("category") String category);
    
    boolean existsByProcessDefinitionKey(String processDefinitionKey);
    
    Optional<Workflow> findByProcessDefinitionId(String processDefinitionId);
}