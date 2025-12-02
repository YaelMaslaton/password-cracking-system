package com.example.minion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request to crack a password hash within a specified phone number range")
public class CrackRequest {
    
    @NotBlank
    @Schema(
        description = "Unique identifier for the crack task",
        example = "task-123",
        required = true
    )
    String taskId;
    
    @NotBlank
    @Schema(
        description = "MD5 hash to crack",
        example = "5d41402abc4b2a76b9719d911017c592",
        required = true
    )
    String hash;
    
    @NotNull
    @Schema(
        description = "Starting number of the range to check (inclusive)",
        example = "500000000",
        required = true
    )
    Long rangeFrom;
    
    @NotNull
    @Schema(
        description = "Ending number of the range to check (inclusive)",
        example = "533333333",
        required = true
    )
    Long rangeTo;
    
    public boolean isValidRange() {
        return rangeFrom != null && rangeTo != null && rangeFrom <= rangeTo;
    }
}