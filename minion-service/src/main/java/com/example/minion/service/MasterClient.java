package com.example.minion.service;

import com.example.minion.dto.ResultRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class MasterClient {
    
    private final RestTemplate restTemplate;
    
    @Value("${master.url}")
    private String masterUrl;
    
    /**
     * Sends result to master service with Spring Retry
     */
    @Retryable(
        value = {RestClientException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 1.5)
    )
    public void sendResult(ResultRequest request) {
        try {
            restTemplate.postForEntity(masterUrl, request, Void.class);
            log.info("Result sent to master: status={}, password={}", 
                    request.getStatus(), request.getPassword());
        } catch (RestClientException e) {
            log.warn("Failed to send result to master: status={}, error={}", 
                    request.getStatus(), e.getMessage());
            throw e;
        }
    }
    
    /**
     * Recovery method called after all retry attempts fail
     */
    @Recover
    public void recover(RestClientException ex, ResultRequest request) {
        log.error("Failed to send result after all retry attempts: status={}, error={}", 
                request.getStatus(), ex.getMessage());
    }
}