package com.example.master.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for receiving results from Minion services.
 */
@Data
public class ResultRequest {
    @NotBlank
    private String taskId;
    
    @NotBlank
    private String status; // FOUND, NOT_FOUND
    
    private String password; // optional
}