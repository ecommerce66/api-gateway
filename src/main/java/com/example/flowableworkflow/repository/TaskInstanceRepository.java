package com.example.flowableworkflow.repository;

import com.example.flowableworkflow.entity.TaskInstance;
import com.example.flowableworkflow.entity.TaskInstanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskInstanceRepository extends JpaRepository<TaskInstance, Long> {
    
    Optional<TaskInstance> findByFlowableTaskId(String flowableTaskId);
    
    List<TaskInstance> findByStatus(TaskInstanceStatus status);
    
    List<TaskInstance> findByAssignee(String assignee);
    
    List<TaskInstance> findByWorkflowInstanceId(Long workflowInstanceId);
    
    List<TaskInstance> findByTaskDefinitionId(Long taskDefinitionId);
    
    @Query("SELECT t FROM TaskInstance t WHERE t.candidateUsers LIKE %:user% OR t.candidateGroups LIKE %:group%")
    List<TaskInstance> findByCandidateUsersOrGroups(@Param("user") String user, @Param("group") String group);
    
    @Query("SELECT t FROM TaskInstance t WHERE t.status = :status AND t.assignee = :assignee")
    List<TaskInstance> findByStatusAndAssignee(
            @Param("status") TaskInstanceStatus status, 
            @Param("assignee") String assignee);
    
    @Query("SELECT t FROM TaskInstance t WHERE t.dueDate <= :date AND t.status NOT IN ('COMPLETED', 'CANCELLED')")
    List<TaskInstance> findOverdueTasks(@Param("date") LocalDateTime date);
    
    @Query("SELECT t FROM TaskInstance t WHERE t.workflowInstance.processInstanceId = :processInstanceId")
    List<TaskInstance> findByProcessInstanceId(@Param("processInstanceId") String processInstanceId);
    
    @Query("SELECT t FROM TaskInstance t WHERE t.createdTime BETWEEN :startDate AND :endDate")
    List<TaskInstance> findByCreatedTimeBetween(
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(t) FROM TaskInstance t WHERE t.status = :status")
    Long countByStatus(@Param("status") TaskInstanceStatus status);
    
    @Query("SELECT t FROM TaskInstance t WHERE t.name LIKE %:name%")
    List<TaskInstance> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT t FROM TaskInstance t WHERE t.priority >= :priority ORDER BY t.priority DESC")
    List<TaskInstance> findByPriorityGreaterThanEqualOrderByPriorityDesc(@Param("priority") Integer priority);
}