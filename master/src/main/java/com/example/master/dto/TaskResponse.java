package com.example.master.dto;

import com.example.master.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for returning task status information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private String taskId;
    private String hash;
    private TaskStatus status;
    private String password;
}