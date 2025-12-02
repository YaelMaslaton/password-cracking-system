package com.example.master.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a batch of password cracking tasks.
 */
@Entity
@Table(name = "batches")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchEntity {
    
    @Id
    @Column(name = "batch_id")
    private UUID batchId;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "status", nullable = false)
    private String status;
    
    @Column(name = "total_tasks", nullable = false)
    private int totalTasks;
    
    @Column(name = "completed_tasks", nullable = false)
    private int completedTasks;
}