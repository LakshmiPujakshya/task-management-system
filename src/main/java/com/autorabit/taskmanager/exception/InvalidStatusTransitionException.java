package com.autorabit.taskmanager.exception;

import com.autorabit.taskmanager.model.Task;

/**
 * Thrown when an invalid task status transition is attempted.
 * e.g., moving a CANCELLED task back to IN_PROGRESS.
 */
public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(Task.Status from, Task.Status to) {
        super("Invalid status transition from " + from + " to " + to);
    }
}
