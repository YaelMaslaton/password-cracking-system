package com.example.master.controller;

import com.example.master.service.OutputService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Output Service", description = "Generate output files with cracking results")
public class OutputController {

    private final OutputService outputService;

    @Operation(
        summary = "Download batch results as text file",
        description = "Generate and download a text file containing all cracked passwords for a batch"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "File generated successfully"),
        @ApiResponse(responseCode = "404", description = "Batch not found"),
        @ApiResponse(responseCode = "400", description = "Invalid batch ID format")
    })
    @GetMapping("/batch/{batchId}/results")
    public ResponseEntity<Resource> downloadBatchResults(
        @Parameter(description = "UUID of the batch", required = true)
        @PathVariable String batchId) {
        
        Resource file = outputService.generateBatchResultsFile(batchId);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"results_" + batchId + ".txt\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(file);
    }
}