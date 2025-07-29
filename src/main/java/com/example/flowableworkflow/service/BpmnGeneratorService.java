package com.example.flowableworkflow.service;

import com.example.flowableworkflow.entity.TaskDefinition;
import com.example.flowableworkflow.entity.TaskType;
import com.example.flowableworkflow.entity.WorkflowDefinition;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BpmnGeneratorService {
    
    public String generateBpmnXml(WorkflowDefinition workflowDefinition) {
        StringBuilder bpmnXml = new StringBuilder();
        
        // BPMN XML Header
        bpmnXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        bpmnXml.append("<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"\n");
        bpmnXml.append("             xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        bpmnXml.append("             xmlns:flowable=\"http://flowable.org/bpmn\"\n");
        bpmnXml.append("             targetNamespace=\"http://www.flowable.org/processdef\">\n\n");
        
        // Process Definition
        bpmnXml.append("  <process id=\"").append(workflowDefinition.getProcessDefinitionKey()).append("\" ");
        bpmnXml.append("name=\"").append(workflowDefinition.getName()).append("\" ");
        bpmnXml.append("isExecutable=\"true\">\n\n");
        
        // Documentation
        if (workflowDefinition.getDescription() != null && !workflowDefinition.getDescription().isEmpty()) {
            bpmnXml.append("    <documentation>").append(workflowDefinition.getDescription()).append("</documentation>\n\n");
        }
        
        // Start Event
        bpmnXml.append("    <startEvent id=\"startEvent\" name=\"Start\">\n");
        bpmnXml.append("      <outgoing>flow1</outgoing>\n");
        bpmnXml.append("    </startEvent>\n\n");
        
        // Generate Tasks
        List<TaskDefinition> tasks = workflowDefinition.getTaskDefinitions()
                .stream()
                .sorted((t1, t2) -> Integer.compare(
                        t1.getSequenceNumber() != null ? t1.getSequenceNumber() : 0,
                        t2.getSequenceNumber() != null ? t2.getSequenceNumber() : 0))
                .toList();
        
        for (int i = 0; i < tasks.size(); i++) {
            TaskDefinition task = tasks.get(i);
            String taskId = "task" + (i + 1);
            String incomingFlow = i == 0 ? "flow1" : "flow" + (i + 1);
            String outgoingFlow = "flow" + (i + 2);
            
            generateTaskElement(bpmnXml, task, taskId, incomingFlow, outgoingFlow);
        }
        
        // End Event
        String lastFlow = tasks.isEmpty() ? "flow1" : "flow" + (tasks.size() + 1);
        bpmnXml.append("    <endEvent id=\"endEvent\" name=\"End\">\n");
        bpmnXml.append("      <incoming>").append(lastFlow).append("</incoming>\n");
        bpmnXml.append("    </endEvent>\n\n");
        
        // Sequence Flows
        generateSequenceFlows(bpmnXml, tasks.size());
        
        // Close Process and Definitions
        bpmnXml.append("  </process>\n");
        bpmnXml.append("</definitions>");
        
        return bpmnXml.toString();
    }
    
    private void generateTaskElement(StringBuilder bpmnXml, TaskDefinition taskDef, 
                                   String taskId, String incomingFlow, String outgoingFlow) {
        
        switch (taskDef.getTaskType()) {
            case USER_TASK:
                generateUserTask(bpmnXml, taskDef, taskId, incomingFlow, outgoingFlow);
                break;
            case SERVICE_TASK:
                generateServiceTask(bpmnXml, taskDef, taskId, incomingFlow, outgoingFlow);
                break;
            case SCRIPT_TASK:
                generateScriptTask(bpmnXml, taskDef, taskId, incomingFlow, outgoingFlow);
                break;
            case MANUAL_TASK:
                generateManualTask(bpmnXml, taskDef, taskId, incomingFlow, outgoingFlow);
                break;
            case PARALLEL_GATEWAY:
                generateParallelGateway(bpmnXml, taskDef, taskId, incomingFlow, outgoingFlow);
                break;
            case EXCLUSIVE_GATEWAY:
                generateExclusiveGateway(bpmnXml, taskDef, taskId, incomingFlow, outgoingFlow);
                break;
            default:
                generateUserTask(bpmnXml, taskDef, taskId, incomingFlow, outgoingFlow);
        }
    }
    
    private void generateUserTask(StringBuilder bpmnXml, TaskDefinition taskDef, 
                                String taskId, String incomingFlow, String outgoingFlow) {
        bpmnXml.append("    <userTask id=\"").append(taskId).append("\" ");
        bpmnXml.append("name=\"").append(taskDef.getName()).append("\"");
        
        if (taskDef.getAssignee() != null && !taskDef.getAssignee().isEmpty()) {
            bpmnXml.append(" flowable:assignee=\"").append(taskDef.getAssignee()).append("\"");
        }
        
        if (taskDef.getCandidateUsers() != null && !taskDef.getCandidateUsers().isEmpty()) {
            bpmnXml.append(" flowable:candidateUsers=\"").append(taskDef.getCandidateUsers()).append("\"");
        }
        
        if (taskDef.getCandidateGroups() != null && !taskDef.getCandidateGroups().isEmpty()) {
            bpmnXml.append(" flowable:candidateGroups=\"").append(taskDef.getCandidateGroups()).append("\"");
        }
        
        if (taskDef.getPriority() != null) {
            bpmnXml.append(" flowable:priority=\"").append(taskDef.getPriority()).append("\"");
        }
        
        if (taskDef.getDueDateExpression() != null && !taskDef.getDueDateExpression().isEmpty()) {
            bpmnXml.append(" flowable:dueDate=\"").append(taskDef.getDueDateExpression()).append("\"");
        }
        
        if (taskDef.getFormKey() != null && !taskDef.getFormKey().isEmpty()) {
            bpmnXml.append(" flowable:formKey=\"").append(taskDef.getFormKey()).append("\"");
        }
        
        bpmnXml.append(">\n");
        
        if (taskDef.getDescription() != null && !taskDef.getDescription().isEmpty()) {
            bpmnXml.append("      <documentation>").append(taskDef.getDescription()).append("</documentation>\n");
        }
        
        bpmnXml.append("      <incoming>").append(incomingFlow).append("</incoming>\n");
        bpmnXml.append("      <outgoing>").append(outgoingFlow).append("</outgoing>\n");
        bpmnXml.append("    </userTask>\n\n");
    }
    
    private void generateServiceTask(StringBuilder bpmnXml, TaskDefinition taskDef, 
                                   String taskId, String incomingFlow, String outgoingFlow) {
        bpmnXml.append("    <serviceTask id=\"").append(taskId).append("\" ");
        bpmnXml.append("name=\"").append(taskDef.getName()).append("\" ");
        bpmnXml.append("flowable:class=\"org.flowable.engine.impl.bpmn.behavior.ServiceTaskDelegateExpressionActivityBehavior\">\n");
        
        if (taskDef.getDescription() != null && !taskDef.getDescription().isEmpty()) {
            bpmnXml.append("      <documentation>").append(taskDef.getDescription()).append("</documentation>\n");
        }
        
        bpmnXml.append("      <incoming>").append(incomingFlow).append("</incoming>\n");
        bpmnXml.append("      <outgoing>").append(outgoingFlow).append("</outgoing>\n");
        bpmnXml.append("    </serviceTask>\n\n");
    }
    
    private void generateScriptTask(StringBuilder bpmnXml, TaskDefinition taskDef, 
                                  String taskId, String incomingFlow, String outgoingFlow) {
        bpmnXml.append("    <scriptTask id=\"").append(taskId).append("\" ");
        bpmnXml.append("name=\"").append(taskDef.getName()).append("\" ");
        bpmnXml.append("scriptFormat=\"javascript\">\n");
        
        bpmnXml.append("      <script><![CDATA[// Script content here]]></script>\n");
        bpmnXml.append("      <incoming>").append(incomingFlow).append("</incoming>\n");
        bpmnXml.append("      <outgoing>").append(outgoingFlow).append("</outgoing>\n");
        bpmnXml.append("    </scriptTask>\n\n");
    }
    
    private void generateManualTask(StringBuilder bpmnXml, TaskDefinition taskDef, 
                                  String taskId, String incomingFlow, String outgoingFlow) {
        bpmnXml.append("    <manualTask id=\"").append(taskId).append("\" ");
        bpmnXml.append("name=\"").append(taskDef.getName()).append("\">\n");
        
        if (taskDef.getDescription() != null && !taskDef.getDescription().isEmpty()) {
            bpmnXml.append("      <documentation>").append(taskDef.getDescription()).append("</documentation>\n");
        }
        
        bpmnXml.append("      <incoming>").append(incomingFlow).append("</incoming>\n");
        bpmnXml.append("      <outgoing>").append(outgoingFlow).append("</outgoing>\n");
        bpmnXml.append("    </manualTask>\n\n");
    }
    
    private void generateParallelGateway(StringBuilder bpmnXml, TaskDefinition taskDef, 
                                       String taskId, String incomingFlow, String outgoingFlow) {
        bpmnXml.append("    <parallelGateway id=\"").append(taskId).append("\" ");
        bpmnXml.append("name=\"").append(taskDef.getName()).append("\">\n");
        bpmnXml.append("      <incoming>").append(incomingFlow).append("</incoming>\n");
        bpmnXml.append("      <outgoing>").append(outgoingFlow).append("</outgoing>\n");
        bpmnXml.append("    </parallelGateway>\n\n");
    }
    
    private void generateExclusiveGateway(StringBuilder bpmnXml, TaskDefinition taskDef, 
                                        String taskId, String incomingFlow, String outgoingFlow) {
        bpmnXml.append("    <exclusiveGateway id=\"").append(taskId).append("\" ");
        bpmnXml.append("name=\"").append(taskDef.getName()).append("\">\n");
        bpmnXml.append("      <incoming>").append(incomingFlow).append("</incoming>\n");
        bpmnXml.append("      <outgoing>").append(outgoingFlow).append("</outgoing>\n");
        bpmnXml.append("    </exclusiveGateway>\n\n");
    }
    
    private void generateSequenceFlows(StringBuilder bpmnXml, int taskCount) {
        // Start to first task or end
        if (taskCount == 0) {
            bpmnXml.append("    <sequenceFlow id=\"flow1\" sourceRef=\"startEvent\" targetRef=\"endEvent\" />\n\n");
        } else {
            bpmnXml.append("    <sequenceFlow id=\"flow1\" sourceRef=\"startEvent\" targetRef=\"task1\" />\n\n");
            
            // Between tasks
            for (int i = 1; i < taskCount; i++) {
                bpmnXml.append("    <sequenceFlow id=\"flow").append(i + 1).append("\" ");
                bpmnXml.append("sourceRef=\"task").append(i).append("\" ");
                bpmnXml.append("targetRef=\"task").append(i + 1).append("\" />\n\n");
            }
            
            // Last task to end
            bpmnXml.append("    <sequenceFlow id=\"flow").append(taskCount + 1).append("\" ");
            bpmnXml.append("sourceRef=\"task").append(taskCount).append("\" ");
            bpmnXml.append("targetRef=\"endEvent\" />\n\n");
        }
    }
}