package com.example.minion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Result of a password cracking task sent to master service")
public class ResultRequest {
    
    @Schema(
        description = "Task identifier",
        example = "task-123"
    )
    String taskId;
    
    @Schema(
        description = "Result status",
        example = "FOUND",
        allowableValues = {"FOUND", "NOT_FOUND"}
    )
    String status;
    
    @Schema(
        description = "Cracked password (only present when status is FOUND)",
        example = "050-1234567"
    )
    String password;
}