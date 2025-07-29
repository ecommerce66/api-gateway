package com.workflow.repository;

import com.workflow.entity.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, Long> {
    
    Optional<Workflow> findByProcessInstanceId(String processInstanceId);
    
    Optional<Workflow> findByProcessDefinitionId(String processDefinitionId);
    
    List<Workflow> findByKey(String key);
    
    List<Workflow> findByStatus(Workflow.WorkflowStatus status);
    
    List<Workflow> findByTenantId(String tenantId);
    
    @Query("SELECT w FROM Workflow w WHERE w.startUserId = :userId")
    List<Workflow> findByStartUserId(@Param("userId") String userId);
    
    @Query("SELECT w FROM Workflow w WHERE w.businessKey = :businessKey")
    List<Workflow> findByBusinessKey(@Param("businessKey") String businessKey);
    
    @Query("SELECT w FROM Workflow w WHERE w.name LIKE %:name%")
    List<Workflow> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT w FROM Workflow w WHERE w.category = :category")
    List<Workflow> findByCategory(@Param("category") String category);
}