package com.example.master.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for sending crack requests to Minion services.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MinionRequest {
    private String taskId;
    private String hash;
    private long rangeFrom;
    private long rangeTo;
}