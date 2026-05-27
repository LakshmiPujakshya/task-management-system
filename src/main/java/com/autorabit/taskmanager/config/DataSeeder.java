package com.autorabit.taskmanager.config;

import com.autorabit.taskmanager.model.Task;
import com.autorabit.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DataSeeder - Seeds the H2 in-memory database with sample data on startup.
 * Implements CommandLineRunner — runs after Spring context is loaded.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final TaskRepository taskRepository;

    @Override
    public void run(String... args) {
        if (taskRepository.count() > 0) return; // Skip if data already exists

        List<Task> sampleTasks = List.of(
            Task.builder()
                .title("Setup CI/CD pipeline")
                .description("Configure GitHub Actions for automated build and deploy")
                .priority(Task.Priority.HIGH)
                .status(Task.Status.IN_PROGRESS)
                .assignedTo("alice")
                .dueDate(LocalDateTime.now().plusDays(3))
                .build(),

            Task.builder()
                .title("Write unit tests for UserService")
                .description("Achieve 80%+ test coverage for UserService class")
                .priority(Task.Priority.MEDIUM)
                .status(Task.Status.TODO)
                .assignedTo("bob")
                .dueDate(LocalDateTime.now().plusDays(5))
                .build(),

            Task.builder()
                .title("Fix memory leak in report generator")
                .description("Profile and fix memory leak causing OOM errors in production")
                .priority(Task.Priority.CRITICAL)
                .status(Task.Status.IN_REVIEW)
                .assignedTo("alice")
                .dueDate(LocalDateTime.now().minusDays(1)) // Overdue!
                .build(),

            Task.builder()
                .title("Update API documentation")
                .description("Update Swagger/OpenAPI docs for all v2 endpoints")
                .priority(Task.Priority.LOW)
                .status(Task.Status.DONE)
                .assignedTo("charlie")
                .dueDate(LocalDateTime.now().plusDays(10))
                .build(),

            Task.builder()
                .title("Database query optimisation")
                .description("Add indexes and optimise slow queries identified in APM")
                .priority(Task.Priority.HIGH)
                .status(Task.Status.TODO)
                .assignedTo("bob")
                .dueDate(LocalDateTime.now().plusDays(7))
                .build()
        );

        taskRepository.saveAll(sampleTasks);
        log.info("✅ Sample data seeded: {} tasks created", sampleTasks.size());
    }
}
