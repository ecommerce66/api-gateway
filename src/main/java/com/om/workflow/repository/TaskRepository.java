package com.om.workflow.repository;

import com.om.workflow.model.Task;
import com.om.workflow.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByWorkflowId(Long workflowId);
    
    List<Task> findByStatus(TaskStatus status);
    
    List<Task> findByAssignee(String assignee);
    
    List<Task> findByOwner(String owner);
    
    List<Task> findByTenantId(String tenantId);
    
    @Query("SELECT t FROM Task t WHERE t.dueDate < :date AND t.status != 'COMPLETED'")
    List<Task> findOverdueTasks(@Param("date") LocalDateTime date);
    
    @Query("SELECT t FROM Task t WHERE t.name LIKE %:name%")
    List<Task> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT t FROM Task t WHERE t.category = :category")
    List<Task> findByCategory(@Param("category") String category);
    
    @Query("SELECT t FROM Task t WHERE t.priority >= :priority")
    List<Task> findByPriorityGreaterThanEqual(@Param("priority") Integer priority);
    
    Optional<Task> findByTaskId(String taskId);
}