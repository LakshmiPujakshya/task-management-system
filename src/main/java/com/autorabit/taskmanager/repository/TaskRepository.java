package com.autorabit.taskmanager.repository;

import com.autorabit.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * TaskRepository - Spring Data JPA repository.
 * Demonstrates database interaction using Spring's repository pattern.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Derived query methods
    List<Task> findByStatus(Task.Status status);

    List<Task> findByPriority(Task.Priority priority);

    List<Task> findByAssignedTo(String assignedTo);

    List<Task> findByStatusAndPriority(Task.Status status, Task.Priority priority);

    // Custom JPQL query
    @Query("SELECT t FROM Task t WHERE t.dueDate < :now AND t.status NOT IN ('DONE', 'CANCELLED')")
    List<Task> findOverdueTasks(@Param("now") LocalDateTime now);

    // Count by status for dashboard
    @Query("SELECT t.status, COUNT(t) FROM Task t GROUP BY t.status")
    List<Object[]> countByStatus();

    // Search by title or description
    @Query("SELECT t FROM Task t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Task> searchByKeyword(@Param("keyword") String keyword);
}
