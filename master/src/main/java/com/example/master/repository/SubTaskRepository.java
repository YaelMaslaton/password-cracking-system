package com.example.master.repository;

import com.example.master.entity.SubTaskEntity;
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
    List<SubTaskEntity> findByTaskIdAndStatus(UUID taskId, String status);
    List<SubTaskEntity> findByStatusAndStartedAtBefore(String status, LocalDateTime threshold);
    List<SubTaskEntity> findByStatus(String statusTimeout);

}