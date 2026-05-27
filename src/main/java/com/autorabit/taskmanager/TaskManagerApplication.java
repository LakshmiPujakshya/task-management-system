package com.autorabit.taskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * TaskManagerApplication - Main entry point for the Task Management REST API.
 * Demonstrates Spring Boot, REST APIs, and Async (Multithreading) capabilities.
 */
@SpringBootApplication
@EnableAsync
public class TaskManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagerApplication.class, args);
        System.out.println("\n✅ Task Manager API is running!");
        System.out.println("📖 H2 Console: http://localhost:8080/h2-console");
        System.out.println("🚀 API Base URL: http://localhost:8080/api/v1/tasks\n");
    }
}
