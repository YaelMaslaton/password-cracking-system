package com.example.master.dto;

import com.example.master.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for receiving results from Minion services.
 */
@Data
public class ResultRequest {
    @NotBlank
    private String taskId;
    
    @NotNull
    private TaskStatus status;
    
    private String password;
}