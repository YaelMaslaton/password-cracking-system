package com.example.master.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for batch submission response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchResponse {
    private String batchId;
    private List<String> taskIds;
    private int tasksCount;
    private String status;
}