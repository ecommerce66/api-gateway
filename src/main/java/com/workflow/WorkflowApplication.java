package com.workflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;

@SpringBootApplication
@ComponentScan(basePackages = "com.workflow")
public class WorkflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkflowApplication.class, args);
    }

    @Bean
    public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> engineConfigurationConfigurer() {
        return engineConfiguration -> {
            engineConfiguration.setActivityFontName("Arial");
            engineConfiguration.setLabelFontName("Arial");
            engineConfiguration.setAnnotationFontName("Arial");
            engineConfiguration.setHistoryLevel(org.flowable.engine.impl.history.HistoryLevel.FULL);
        };
    }
}