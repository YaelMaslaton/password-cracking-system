package com.example.master.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Entity representing a password cracking task for a single hash.
 */
@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskEntity {
    
    @Id
    @Column(name = "task_id")
    private UUID taskId;
    
    @Column(name = "batch_id", nullable = false)
    private UUID batchId;
    
    @Column(name = "hash_value", nullable = false)
    private String hashValue;
    
    @Column(name = "status", nullable = false)
    private String status;
    
    @Column(name = "found_password")
    private String foundPassword;
    
    @Column(name = "total_sub_tasks", nullable = false)
    private int totalSubTasks;
    
    @Column(name = "completed_sub_tasks", nullable = false)
    private int completedSubTasks;
}