package com.example.master.repository;

import com.example.master.entity.BatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository for BatchEntity operations.
 */
@Repository
public interface BatchRepository extends JpaRepository<BatchEntity, UUID> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE BatchEntity b SET b.completedTasks = b.completedTasks + 1 WHERE b.batchId = :batchId")
    void incrementCompletedTasks(@Param("batchId") UUID batchId);

}