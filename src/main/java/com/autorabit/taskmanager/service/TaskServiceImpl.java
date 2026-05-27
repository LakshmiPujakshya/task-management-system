package com.autorabit.taskmanager.service;

import com.autorabit.taskmanager.dto.TaskDTO;
import com.autorabit.taskmanager.exception.InvalidStatusTransitionException;
import com.autorabit.taskmanager.exception.TaskNotFoundException;
import com.autorabit.taskmanager.model.Task;
import com.autorabit.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * TaskServiceImpl - Core business logic layer.
 *
 * Demonstrates:
 *  - OOP: Encapsulation, abstraction, interface implementation
 *  - Collections: List, Map, HashMap, streams
 *  - Exception Handling: Custom exceptions, try-catch
 *  - Multithreading: @Async + CompletableFuture
 *  - Spring: @Service, @Transactional, dependency injection
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    // Valid status transition map — uses Collections.unmodifiableMap for safety
    private static final Map<Task.Status, Set<Task.Status>> VALID_TRANSITIONS;

    static {
        Map<Task.Status, Set<Task.Status>> transitions = new HashMap<>();
        transitions.put(Task.Status.TODO,        EnumSet.of(Task.Status.IN_PROGRESS, Task.Status.CANCELLED));
        transitions.put(Task.Status.IN_PROGRESS, EnumSet.of(Task.Status.IN_REVIEW, Task.Status.TODO, Task.Status.CANCELLED));
        transitions.put(Task.Status.IN_REVIEW,   EnumSet.of(Task.Status.DONE, Task.Status.IN_PROGRESS, Task.Status.CANCELLED));
        transitions.put(Task.Status.DONE,        EnumSet.noneOf(Task.Status.class));
        transitions.put(Task.Status.CANCELLED,   EnumSet.noneOf(Task.Status.class));
        VALID_TRANSITIONS = Collections.unmodifiableMap(transitions);
    }

    @Override
    @Transactional
    public TaskDTO.Response createTask(TaskDTO.Request request) {
        log.info("Creating task: {}", request.getTitle());

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(request.getStatus() != null ? request.getStatus() : Task.Status.TODO)
                .assignedTo(request.getAssignedTo())
                .dueDate(request.getDueDate())
                .build();

        Task saved = taskRepository.save(task);
        log.info("Task created with id: {}", saved.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskDTO.Response getTaskById(Long id) {
        Task task = findTaskOrThrow(id);
        return toResponse(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO.Summary> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TaskDTO.Response updateTask(Long id, TaskDTO.Request request) {
        Task task = findTaskOrThrow(id);

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setAssignedTo(request.getAssignedTo());
        task.setDueDate(request.getDueDate());

        if (request.getStatus() != null && !request.getStatus().equals(task.getStatus())) {
            validateStatusTransition(task.getStatus(), request.getStatus());
            task.setStatus(request.getStatus());
        }

        Task updated = taskRepository.save(task);
        return toResponse(updated);
    }

    @Override
    @Transactional
    public TaskDTO.Response updateStatus(Long id, Task.Status newStatus) {
        Task task = findTaskOrThrow(id);
        validateStatusTransition(task.getStatus(), newStatus);
        task.setStatus(newStatus);
        Task updated = taskRepository.save(task);
        log.info("Task {} status updated: {} -> {}", id, task.getStatus(), newStatus);
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        Task task = findTaskOrThrow(id);
        taskRepository.delete(task);
        log.info("Task {} deleted", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO.Summary> getTasksByStatus(Task.Status status) {
        return taskRepository.findByStatus(status)
                .stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO.Summary> getTasksByPriority(Task.Priority priority) {
        return taskRepository.findByPriority(priority)
                .stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO.Summary> getTasksByAssignee(String assignedTo) {
        return taskRepository.findByAssignedTo(assignedTo)
                .stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO.Summary> getOverdueTasks() {
        return taskRepository.findOverdueTasks(LocalDateTime.now())
                .stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO.Summary> searchTasks(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllTasks();
        }
        return taskRepository.searchByKeyword(keyword.trim())
                .stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getDashboardStats() {
        // Uses Collections (HashMap) and streams for aggregation
        Map<String, Long> stats = new LinkedHashMap<>();

        List<Object[]> counts = taskRepository.countByStatus();
        for (Object[] row : counts) {
            stats.put(((Task.Status) row[0]).name(), (Long) row[1]);
        }

        long total = taskRepository.count();
        long overdue = taskRepository.findOverdueTasks(LocalDateTime.now()).size();

        stats.put("TOTAL", total);
        stats.put("OVERDUE", overdue);

        return stats;
    }

    /**
     * Async method using @Async and CompletableFuture — demonstrates Multithreading.
     * Runs in a separate thread from Spring's task executor pool.
     */
    @Override
    @Async
    @Transactional(readOnly = true)
    public CompletableFuture<List<TaskDTO.Summary>> getTasksAsync(String assignedTo) {
        log.info("Async task fetch started on thread: {}", Thread.currentThread().getName());

        List<TaskDTO.Summary> tasks = taskRepository.findByAssignedTo(assignedTo)
                .stream()
                .map(this::toSummary)
                .collect(Collectors.toList());

        log.info("Async task fetch completed. Found {} tasks for {}", tasks.size(), assignedTo);
        return CompletableFuture.completedFuture(tasks);
    }

    // ---- Private Helper Methods ----

    private Task findTaskOrThrow(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    private void validateStatusTransition(Task.Status current, Task.Status next) {
        Set<Task.Status> allowed = VALID_TRANSITIONS.getOrDefault(current, EnumSet.noneOf(Task.Status.class));
        if (!allowed.contains(next)) {
            throw new InvalidStatusTransitionException(current, next);
        }
    }

    private TaskDTO.Response toResponse(Task task) {
        return TaskDTO.Response.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .status(task.getStatus())
                .assignedTo(task.getAssignedTo())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .dueDate(task.getDueDate())
                .build();
    }

    private TaskDTO.Summary toSummary(Task task) {
        return TaskDTO.Summary.builder()
                .id(task.getId())
                .title(task.getTitle())
                .priority(task.getPriority())
                .status(task.getStatus())
                .assignedTo(task.getAssignedTo())
                .dueDate(task.getDueDate())
                .build();
    }
}
