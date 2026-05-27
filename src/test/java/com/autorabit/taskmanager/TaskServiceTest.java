package com.autorabit.taskmanager;

import com.autorabit.taskmanager.dto.TaskDTO;
import com.autorabit.taskmanager.exception.InvalidStatusTransitionException;
import com.autorabit.taskmanager.exception.TaskNotFoundException;
import com.autorabit.taskmanager.model.Task;
import com.autorabit.taskmanager.repository.TaskRepository;
import com.autorabit.taskmanager.service.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TaskServiceTest - Unit tests for TaskServiceImpl.
 * Demonstrates testing with JUnit 5 + Mockito.
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        sampleTask = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("Test description")
                .priority(Task.Priority.HIGH)
                .status(Task.Status.TODO)
                .assignedTo("alice")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusDays(5))
                .build();
    }

    @Test
    @DisplayName("Should create a task successfully")
    void createTask_shouldReturnCreatedTask() {
        TaskDTO.Request request = TaskDTO.Request.builder()
                .title("Test Task")
                .description("Test description")
                .priority(Task.Priority.HIGH)
                .assignedTo("alice")
                .dueDate(LocalDateTime.now().plusDays(5))
                .build();

        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        TaskDTO.Response response = taskService.createTask(request);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Test Task");
        assertThat(response.getPriority()).isEqualTo(Task.Priority.HIGH);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should throw TaskNotFoundException when task does not exist")
    void getTaskById_shouldThrowWhenNotFound() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(999L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("Should return task when found by ID")
    void getTaskById_shouldReturnTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        TaskDTO.Response response = taskService.getTaskById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Test Task");
    }

    @Test
    @DisplayName("Should update status from TODO to IN_PROGRESS")
    void updateStatus_shouldAllowValidTransition() {
        Task inProgress = Task.builder()
                .id(1L).title("Test Task").description("desc")
                .priority(Task.Priority.HIGH).status(Task.Status.IN_PROGRESS)
                .assignedTo("alice").createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now()).build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(inProgress);

        TaskDTO.Response response = taskService.updateStatus(1L, Task.Status.IN_PROGRESS);
        assertThat(response.getStatus()).isEqualTo(Task.Status.IN_PROGRESS);
    }

    @Test
    @DisplayName("Should throw InvalidStatusTransitionException for invalid transition")
    void updateStatus_shouldRejectInvalidTransition() {
        // DONE -> IN_PROGRESS is not allowed
        Task doneTask = Task.builder()
                .id(1L).title("Test Task").description("desc")
                .priority(Task.Priority.HIGH).status(Task.Status.DONE)
                .assignedTo("alice").createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now()).build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(doneTask));

        assertThatThrownBy(() -> taskService.updateStatus(1L, Task.Status.IN_PROGRESS))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }

    @Test
    @DisplayName("Should return all tasks as summaries")
    void getAllTasks_shouldReturnList() {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(sampleTask));

        List<TaskDTO.Summary> summaries = taskService.getAllTasks();

        assertThat(summaries).hasSize(1);
        assertThat(summaries.get(0).getTitle()).isEqualTo("Test Task");
    }

    @Test
    @DisplayName("Should delete task by ID")
    void deleteTask_shouldCallRepository() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        doNothing().when(taskRepository).delete(sampleTask);

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).delete(sampleTask);
    }
}
