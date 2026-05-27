package com.autorabit.taskmanager.dto;

import com.autorabit.taskmanager.model.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTOs (Data Transfer Objects) for Task API.
 * Separates the API contract from the internal model — clean architecture.
 */
public class TaskDTO {

    /**
     * Request DTO - used when creating or updating a task.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {

        @NotBlank(message = "Title is required")
        private String title;

        private String description;

        @NotNull(message = "Priority is required")
        private Task.Priority priority;

        private Task.Status status;

        @NotBlank(message = "assignedTo is required")
        private String assignedTo;

        private LocalDateTime dueDate;
    }

    /**
     * Response DTO - returned to the client.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String title;
        private String description;
        private Task.Priority priority;
        private Task.Status status;
        private String assignedTo;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime dueDate;
    }

    /**
     * Summary DTO - lightweight version for list views.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary {
        private Long id;
        private String title;
        private Task.Priority priority;
        private Task.Status status;
        private String assignedTo;
        private LocalDateTime dueDate;
    }
}
