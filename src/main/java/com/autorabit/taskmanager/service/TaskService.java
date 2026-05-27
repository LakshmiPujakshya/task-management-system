package com.autorabit.taskmanager.service;

import com.autorabit.taskmanager.dto.TaskDTO;
import com.autorabit.taskmanager.model.Task;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * TaskService interface - defines the service contract.
 * Interface-based design is a core OOP principle (abstraction + polymorphism).
 */
public interface TaskService {

    TaskDTO.Response createTask(TaskDTO.Request request);

    TaskDTO.Response getTaskById(Long id);

    List<TaskDTO.Summary> getAllTasks();

    TaskDTO.Response updateTask(Long id, TaskDTO.Request request);

    TaskDTO.Response updateStatus(Long id, Task.Status newStatus);

    void deleteTask(Long id);

    List<TaskDTO.Summary> getTasksByStatus(Task.Status status);

    List<TaskDTO.Summary> getTasksByPriority(Task.Priority priority);

    List<TaskDTO.Summary> getTasksByAssignee(String assignedTo);

    List<TaskDTO.Summary> getOverdueTasks();

    List<TaskDTO.Summary> searchTasks(String keyword);

    Map<String, Long> getDashboardStats();

    // Async method demonstrating Multithreading
    CompletableFuture<List<TaskDTO.Summary>> getTasksAsync(String assignedTo);
}
