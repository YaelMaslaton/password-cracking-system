package com.example.minion.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Health Check", description = "Service health and status operations")
public class HealthController {

    private static final String STATUS_UP = "UP";
    private static final String SERVICE_NAME = "minion-service";
    private static final String VERSION = "1.0.0";

    @Value("${minion.id}")
    private String minionId;

    @Operation(
        summary = "Check service health",
        description = "Returns the current health status and basic information about the minion service"
    )
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealthStatus() {
        return ResponseEntity.ok(Map.of(
            "status", STATUS_UP,
            "minionId", minionId,
            "timestamp", LocalDateTime.now(),
            "service", SERVICE_NAME,
            "version", VERSION
        ));
    }
}