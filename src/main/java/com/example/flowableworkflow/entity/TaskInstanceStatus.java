package com.example.flowableworkflow.entity;

public enum TaskInstanceStatus {
    CREATED,
    READY,
    RESERVED,
    IN_PROGRESS,
    SUSPENDED,
    COMPLETED,
    FAILED,
    ERROR,
    EXITED,
    OBSOLETE
}