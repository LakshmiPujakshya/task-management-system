package com.autorabit.taskmanager.controller;

import com.autorabit.taskmanager.dto.ApiResponse;
import com.autorabit.taskmanager.dto.TaskDTO;
import com.autorabit.taskmanager.model.Task;
import com.autorabit.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * TaskController - REST API layer.
 *
 * Demonstrates:
 *  - RESTful API design (GET, POST, PUT, PATCH, DELETE)
 *  - Clean URL structure with versioning (/api/v1)
 *  - Input validation with @Valid
 *  - HTTP status codes
 *  - Async endpoint
 */
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final TaskService taskService;

    // ---- CRUD Endpoints ----

    /**
     * POST /api/v1/tasks
     * Create a new task.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TaskDTO.Response>> createTask(
            @Valid @RequestBody TaskDTO.Request request) {
        log.info("POST /api/v1/tasks - Creating task: {}", request.getTitle());
        TaskDTO.Response created = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Task created successfully"));
    }

    /**
     * GET /api/v1/tasks/{id}
     * Get a task by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskDTO.Response>> getTaskById(@PathVariable Long id) {
        TaskDTO.Response task = taskService.getTaskById(id);
        return ResponseEntity.ok(ApiResponse.success(task, "Task retrieved successfully"));
    }

    /**
     * GET /api/v1/tasks
     * Get all tasks, with optional filters.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskDTO.Summary>>> getAllTasks(
            @RequestParam(required = false) Task.Status status,
            @RequestParam(required = false) Task.Priority priority,
            @RequestParam(required = false) String assignedTo,
            @RequestParam(required = false) String search) {

        List<TaskDTO.Summary> tasks;

        if (search != null && !search.isBlank()) {
            tasks = taskService.searchTasks(search);
        } else if (status != null) {
            tasks = taskService.getTasksByStatus(status);
        } else if (priority != null) {
            tasks = taskService.getTasksByPriority(priority);
        } else if (assignedTo != null) {
            tasks = taskService.getTasksByAssignee(assignedTo);
        } else {
            tasks = taskService.getAllTasks();
        }

        return ResponseEntity.ok(ApiResponse.success(tasks,
                "Retrieved " + tasks.size() + " task(s)"));
    }

    /**
     * PUT /api/v1/tasks/{id}
     * Fully update a task.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskDTO.Response>> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskDTO.Request request) {
        TaskDTO.Response updated = taskService.updateTask(id, request);
        return ResponseEntity.ok(ApiResponse.success(updated, "Task updated successfully"));
    }

    /**
     * PATCH /api/v1/tasks/{id}/status
     * Update only the status (with transition validation).
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TaskDTO.Response>> updateStatus(
            @PathVariable Long id,
            @RequestParam Task.Status status) {
        TaskDTO.Response updated = taskService.updateStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(updated, "Status updated to " + status));
    }

    /**
     * DELETE /api/v1/tasks/{id}
     * Delete a task.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Task deleted successfully"));
    }

    // ---- Special Endpoints ----

    /**
     * GET /api/v1/tasks/overdue
     * Get all overdue tasks.
     */
    @GetMapping("/overdue")
    public ResponseEntity<ApiResponse<List<TaskDTO.Summary>>> getOverdueTasks() {
        List<TaskDTO.Summary> tasks = taskService.getOverdueTasks();
        return ResponseEntity.ok(ApiResponse.success(tasks,
                "Found " + tasks.size() + " overdue task(s)"));
    }

    /**
     * GET /api/v1/tasks/dashboard
     * Get task statistics for dashboard.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getDashboard() {
        Map<String, Long> stats = taskService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success(stats, "Dashboard stats retrieved"));
    }

    /**
     * GET /api/v1/tasks/async?assignedTo=john
     * Async endpoint — fetches tasks in a separate thread.
     */
    @GetMapping("/async")
    public CompletableFuture<ResponseEntity<ApiResponse<List<TaskDTO.Summary>>>> getTasksAsync(
            @RequestParam String assignedTo) {
        return taskService.getTasksAsync(assignedTo)
                .thenApply(tasks -> ResponseEntity.ok(
                        ApiResponse.success(tasks, "Async fetch: " + tasks.size() + " tasks")));
    }
}
