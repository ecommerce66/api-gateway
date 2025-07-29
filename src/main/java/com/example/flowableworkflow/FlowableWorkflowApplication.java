package com.example.flowableworkflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class FlowableWorkflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlowableWorkflowApplication.class, args);
    }
}