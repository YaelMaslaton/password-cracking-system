package com.example.minion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Error response structure")
public class ErrorResponse {
    
    @Schema(description = "Error timestamp", example = "2023-12-02T10:30:00")
    LocalDateTime timestamp;
    
    @Schema(description = "HTTP status code", example = "400")
    int status;
    
    @Schema(description = "Error type", example = "Bad Request")
    String error;
    
    @Schema(description = "Error message", example = "Invalid request parameters")
    String message;
    
    @Schema(description = "Request path", example = "/crack")
    String path;
}