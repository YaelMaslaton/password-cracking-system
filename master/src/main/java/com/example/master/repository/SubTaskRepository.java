package com.example.master.repository;

import com.example.master.entity.SubTaskEntity;
import com.example.master.enums.SubTaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for SubTaskEntity operations.
 */
@Repository
public interface SubTaskRepository extends JpaRepository<SubTaskEntity, UUID> {
    List<SubTaskEntity> findByTaskIdAndStatus(UUID taskId, SubTaskStatus status);
    List<SubTaskEntity> findByStatusAndStartedAtBefore(SubTaskStatus status, LocalDateTime threshold);
    List<SubTaskEntity> findByStatus(SubTaskStatus status);

}