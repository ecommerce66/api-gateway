package com.example.flowableworkflow.repository;

import com.example.flowableworkflow.entity.TaskDefinition;
import com.example.flowableworkflow.entity.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskDefinitionRepository extends JpaRepository<TaskDefinition, Long> {
    
    List<TaskDefinition> findByWorkflowDefinitionId(Long workflowDefinitionId);
    
    List<TaskDefinition> findByTaskType(TaskType taskType);
    
    List<TaskDefinition> findByAssignee(String assignee);
    
    Optional<TaskDefinition> findByTaskDefinitionKey(String taskDefinitionKey);
    
    @Query("SELECT t FROM TaskDefinition t WHERE t.workflowDefinition.id = :workflowId ORDER BY t.sequenceNumber")
    List<TaskDefinition> findByWorkflowDefinitionIdOrderBySequenceNumber(@Param("workflowId") Long workflowId);
    
    @Query("SELECT t FROM TaskDefinition t WHERE t.workflowDefinition.processDefinitionKey = :processKey")
    List<TaskDefinition> findByWorkflowProcessDefinitionKey(@Param("processKey") String processDefinitionKey);
    
    @Query("SELECT t FROM TaskDefinition t WHERE t.name LIKE %:name%")
    List<TaskDefinition> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT t FROM TaskDefinition t WHERE t.candidateUsers LIKE %:user% OR t.candidateGroups LIKE %:group%")
    List<TaskDefinition> findByCandidateUsersOrGroups(@Param("user") String user, @Param("group") String group);
    
    boolean existsByTaskDefinitionKey(String taskDefinitionKey);
}