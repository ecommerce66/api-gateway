package com.workflow.repository;

import com.workflow.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    Optional<Task> findByTaskId(String taskId);
    
    List<Task> findByProcessInstanceId(String processInstanceId);
    
    List<Task> findByAssignee(String assignee);
    
    List<Task> findByOwner(String owner);
    
    List<Task> findByStatus(Task.TaskStatus status);
    
    List<Task> findByTaskDefinitionKey(String taskDefinitionKey);
    
    List<Task> findByTenantId(String tenantId);
    
    @Query("SELECT t FROM Task t WHERE t.assignee = :assignee AND t.status = :status")
    List<Task> findByAssigneeAndStatus(@Param("assignee") String assignee, @Param("status") Task.TaskStatus status);
    
    @Query("SELECT t FROM Task t WHERE t.processInstanceId = :processInstanceId AND t.status = :status")
    List<Task> findByProcessInstanceIdAndStatus(@Param("processInstanceId") String processInstanceId, 
                                               @Param("status") Task.TaskStatus status);
    
    @Query("SELECT t FROM Task t WHERE t.name LIKE %:name%")
    List<Task> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT t FROM Task t WHERE t.category = :category")
    List<Task> findByCategory(@Param("category") String category);
    
    @Query("SELECT t FROM Task t WHERE t.priority >= :priority")
    List<Task> findByPriorityGreaterThanEqual(@Param("priority") Integer priority);
}