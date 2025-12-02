package com.example.master.repository;

import com.example.master.entity.BatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository for BatchEntity operations.
 */
@Repository
public interface BatchRepository extends JpaRepository<BatchEntity, UUID> {
}