package com.example.master.controller;

import com.example.master.dto.BatchResponse;
import com.example.master.dto.ResultRequest;
import com.example.master.dto.TaskResponse;
import com.example.master.enums.TaskStatus;
import com.example.master.service.MasterService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Tag(name = "Master Service", description = "Distributed Password Cracking System - Master Service API")
public class MasterController {

    private final MasterService masterService;
    
    @Value("${internal.api.key}")
    private String internalApiKey;

    @Operation(
        summary = "Submit MD5 hashes for cracking",
        description = "Upload a file containing MD5 hashes (one per line) to be cracked by the distributed system"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Hashes submitted successfully",
            content = @Content(schema = @Schema(implementation = BatchResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid file format or content")
    })
    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BatchResponse> submitHashes(
        @Parameter(description = "File containing MD5 hashes (one per line)", required = true)
        @RequestParam("file") MultipartFile file) throws IOException {
        BatchResponse response = masterService.submitHashes(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Hidden
    @PostMapping("/result")
    public ResponseEntity<Void> receiveResult(
        @RequestHeader(value = "X-INTERNAL-KEY", required = false) String apiKey,
        @Valid @RequestBody ResultRequest resultRequest) {
        
        if (apiKey == null || !apiKey.equals(internalApiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        masterService.processResult(resultRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Get task status",
        description = "Retrieve the current status of a password cracking task"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task status retrieved successfully",
            content = @Content(schema = @Schema(implementation = TaskResponse.class))),
        @ApiResponse(responseCode = "404", description = "Task not found"),
        @ApiResponse(responseCode = "400", description = "Invalid task ID format")
    })
    @GetMapping("/task/{taskId}")
    public ResponseEntity<TaskResponse> getTaskStatus(
        @Parameter(description = "UUID of the task", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
        @PathVariable String taskId) {
        try {
            TaskResponse response = masterService.getTaskStatus(taskId);
            if (response == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new TaskResponse(taskId, null, TaskStatus.NOT_FOUND, null));
            }
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new TaskResponse(taskId, null, TaskStatus.FAILED, null));
        }
    }
}