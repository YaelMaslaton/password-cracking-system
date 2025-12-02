package com.example.minion.controller;

import com.example.minion.dto.CrackRequest;
import com.example.minion.service.CrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Minion Controller", description = "Password cracking operations")
public class MinionController {
    
    private final CrackingService crackingService;
    
    @Operation(
        summary = "Process password cracking task",
        description = "Receives a crack task with hash and number range, performs brute-force MD5 checks on phone numbers, and reports results back to master service"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Task accepted and processing started"
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid request parameters",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    @PostMapping("/crack")
    public ResponseEntity<Void> crack(
        @Parameter(
            description = "Crack task request containing taskId, hash, and number range",
            required = true,
            content = @Content(
                examples = @ExampleObject(
                    name = "Sample crack request",
                    value = "{\"taskId\": \"task-123\", \"hash\": \"5d41402abc4b2a76b9719d911017c592\", \"rangeFrom\": 500000000, \"rangeTo\": 533333333}"
                )
            )
        )
        @Valid @RequestBody CrackRequest request) {
        
        log.info("Received crack request: taskId={}", request.getTaskId());
        crackingService.crack(request);
        return ResponseEntity.ok().build();
    }
}