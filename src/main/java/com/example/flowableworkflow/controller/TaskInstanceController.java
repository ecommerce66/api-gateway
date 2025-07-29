package com.example.flowableworkflow.controller;

import com.example.flowableworkflow.entity.TaskInstance;
import com.example.flowableworkflow.entity.TaskInstanceStatus;
import com.example.flowableworkflow.service.TaskInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/task-instances")
@CrossOrigin(origins = "*")
public class TaskInstanceController {
    
    @Autowired
    private TaskInstanceService taskInstanceService;
    
    @GetMapping("/{id}")
    public ResponseEntity<TaskInstance> getTaskInstanceById(@PathVariable Long id) {
        Optional<TaskInstance> taskInstance = taskInstanceService.getTaskInstanceById(id);
        return taskInstance.map(t -> new ResponseEntity<>(t, HttpStatus.OK))
                          .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/flowable-task/{flowableTaskId}")
    public ResponseEntity<TaskInstance> getTaskInstanceByFlowableTaskId(@PathVariable String flowableTaskId) {
        Optional<TaskInstance> taskInstance = taskInstanceService.getTaskInstanceByFlowableTaskId(flowableTaskId);
        return taskInstance.map(t -> new ResponseEntity<>(t, HttpStatus.OK))
                          .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @PostMapping("/{id}/claim")
    public ResponseEntity<TaskInstance> claimTask(@PathVariable Long id, @RequestParam String assignee) {
        try {
            TaskInstance claimed = taskInstanceService.claimTask(id, assignee);
            return new ResponseEntity<>(claimed, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    @PostMapping("/{id}/start")
    public ResponseEntity<TaskInstance> startTask(@PathVariable Long id) {
        try {
            TaskInstance started = taskInstanceService.startTask(id);
            return new ResponseEntity<>(started, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    @PostMapping("/{id}/complete")
    public ResponseEntity<TaskInstance> completeTask(@PathVariable Long id, 
                                                    @RequestBody(required = false) Map<String, Object> variables) {
        try {
            TaskInstance completed = taskInstanceService.completeTask(id, variables);
            return new ResponseEntity<>(completed, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    @PostMapping("/{id}/suspend")
    public ResponseEntity<TaskInstance> suspendTask(@PathVariable Long id) {
        try {
            TaskInstance suspended = taskInstanceService.suspendTask(id);
            return new ResponseEntity<>(suspended, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    @PostMapping("/{id}/resume")
    public ResponseEntity<TaskInstance> resumeTask(@PathVariable Long id) {
        try {
            TaskInstance resumed = taskInstanceService.resumeTask(id);
            return new ResponseEntity<>(resumed, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    @PostMapping("/{id}/delegate")
    public ResponseEntity<TaskInstance> delegateTask(@PathVariable Long id, @RequestParam String delegateAssignee) {
        try {
            TaskInstance delegated = taskInstanceService.delegateTask(id, delegateAssignee);
            return new ResponseEntity<>(delegated, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    @PostMapping("/{id}/assign")
    public ResponseEntity<TaskInstance> assignTask(@PathVariable Long id, @RequestParam String assignee) {
        try {
            TaskInstance assigned = taskInstanceService.assignTask(id, assignee);
            return new ResponseEntity<>(assigned, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskInstance>> getTaskInstancesByStatus(@PathVariable TaskInstanceStatus status) {
        List<TaskInstance> taskInstances = taskInstanceService.getTaskInstancesByStatus(status);
        return new ResponseEntity<>(taskInstances, HttpStatus.OK);
    }
    
    @GetMapping("/assignee/{assignee}")
    public ResponseEntity<List<TaskInstance>> getTaskInstancesByAssignee(@PathVariable String assignee) {
        List<TaskInstance> taskInstances = taskInstanceService.getTaskInstancesByAssignee(assignee);
        return new ResponseEntity<>(taskInstances, HttpStatus.OK);
    }
    
    @GetMapping("/workflow-instance/{workflowInstanceId}")
    public ResponseEntity<List<TaskInstance>> getTaskInstancesByWorkflowInstance(@PathVariable Long workflowInstanceId) {
        List<TaskInstance> taskInstances = taskInstanceService.getTaskInstancesByWorkflowInstance(workflowInstanceId);
        return new ResponseEntity<>(taskInstances, HttpStatus.OK);
    }
    
    @GetMapping("/candidate")
    public ResponseEntity<List<TaskInstance>> getTaskInstancesByCandidateUsersOrGroups(
            @RequestParam(required = false) String user, 
            @RequestParam(required = false) String group) {
        List<TaskInstance> taskInstances = taskInstanceService.getTaskInstancesByCandidateUsersOrGroups(user, group);
        return new ResponseEntity<>(taskInstances, HttpStatus.OK);
    }
    
    @GetMapping("/overdue")
    public ResponseEntity<List<TaskInstance>> getOverdueTasks() {
        List<TaskInstance> overdueTasks = taskInstanceService.getOverdueTasks();
        return new ResponseEntity<>(overdueTasks, HttpStatus.OK);
    }
    
    @GetMapping("/high-priority")
    public ResponseEntity<List<TaskInstance>> getHighPriorityTasks(@RequestParam(defaultValue = "70") Integer minimumPriority) {
        List<TaskInstance> highPriorityTasks = taskInstanceService.getHighPriorityTasks(minimumPriority);
        return new ResponseEntity<>(highPriorityTasks, HttpStatus.OK);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<TaskInstance>> searchTaskInstancesByName(@RequestParam String name) {
        List<TaskInstance> taskInstances = taskInstanceService.searchTaskInstancesByName(name);
        return new ResponseEntity<>(taskInstances, HttpStatus.OK);
    }
    
    @PostMapping("/{id}/variables/{variableName}")
    public ResponseEntity<Void> setTaskInstanceVariable(@PathVariable Long id, 
                                                       @PathVariable String variableName, 
                                                       @RequestBody Object value) {
        try {
            taskInstanceService.setTaskInstanceVariable(id, variableName, value);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @PostMapping("/{id}/variables")
    public ResponseEntity<Void> setTaskInstanceVariables(@PathVariable Long id, 
                                                        @RequestBody Map<String, Object> variables) {
        try {
            taskInstanceService.setTaskInstanceVariables(id, variables);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/{id}/variables")
    public ResponseEntity<Map<String, Object>> getTaskInstanceVariables(@PathVariable Long id) {
        try {
            Map<String, Object> variables = taskInstanceService.getTaskInstanceVariables(id);
            return new ResponseEntity<>(variables, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/count/status/{status}")
    public ResponseEntity<Long> countTaskInstancesByStatus(@PathVariable TaskInstanceStatus status) {
        Long count = taskInstanceService.countTaskInstancesByStatus(status);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }
}