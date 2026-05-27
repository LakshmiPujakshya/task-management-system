package com.autorabit.taskmanager.exception;

/**
 * Custom Exception classes - demonstrates Exception Handling best practices.
 */

// ---- TaskNotFoundException ----
public class TaskNotFoundException extends RuntimeException {

    private final Long taskId;

    public TaskNotFoundException(Long taskId) {
        super("Task not found with id: " + taskId);
        this.taskId = taskId;
    }

    public Long getTaskId() {
        return taskId;
    }
}
