// infrastructure/repository/TaskRepository.java

package com.teamtacles.task.teamtacles_api_task.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.teamtacles.task.teamtacles_api_task.domain.model.Task;
import com.teamtacles.task.teamtacles_api_task.domain.model.enums.Status;

import java.time.LocalDateTime;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByStatus(Status status, Pageable pageable);

    // Ajustado para consultar a coleção de IDs e o campo projectId
    @Query("SELECT t FROM Task t JOIN t.responsibleUserIds r WHERE t.projectId.value = :projectId AND r = :userId")
    Page<Task> findByProjectIdAndUsersResponsabilityId(@Param("projectId") Long projectId, @Param("userId") Long userId, Pageable pageable);

    @Query("""
        SELECT DISTINCT t FROM Task t
        LEFT JOIN t.responsibleUserIds ur
        WHERE (:statusEnum IS NULL OR t.status = :statusEnum)
        AND (:dueDate IS NULL OR t.dueDate.value <= :dueDate)
        AND (:projectId IS NULL OR t.projectId.value = :projectId)
        AND (t.ownerUserId.value = :userId OR ur = :userId)
    """)
    Page<Task> findTasksFilteredByUser(@Param("statusEnum") Status statusEnum, @Param("dueDate") LocalDateTime dueDate, @Param("projectId") Long projectId, @Param("userId") Long userId, Pageable pageable);

    @Query("""
        SELECT DISTINCT t FROM Task t
        WHERE (:statusEnum IS NULL OR t.status = :statusEnum)
        AND (:dueDate IS NULL OR t.dueDate.value <= :dueDate)
        AND (:projectId IS NULL OR t.projectId.value = :projectId)
    """)
    Page<Task> findTasksFiltered(@Param("statusEnum") Status statusEnum, @Param("dueDate") LocalDateTime dueDate, @Param("projectId") Long projectId, Pageable pageable);
}