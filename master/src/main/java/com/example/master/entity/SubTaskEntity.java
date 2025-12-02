package com.example.master.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a subtask assigned to a specific minion.
 */
@Entity
@Table(name = "sub_tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubTaskEntity {
    
    @Id
    @Column(name = "sub_task_id")
    private UUID subTaskId;
    
    @Column(name = "task_id", nullable = false)
    private UUID taskId;
    
    @Column(name = "range_start", nullable = false)
    private long rangeStart;
    
    @Column(name = "range_end", nullable = false)
    private long rangeEnd;
    
    @Column(name = "status", nullable = false)
    private String status;
    
    @Column(name = "result_password")
    private String resultPassword;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private int retryCount = 0;
}