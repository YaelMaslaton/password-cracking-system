package com.example.minion.service;

import com.example.minion.dto.CrackRequest;

/**
 * Service interface for password cracking operations
 */
public interface CrackingService {
    
    /**
     * Processes a crack request by performing brute force attack on the given range
     * 
     * @param request the crack request containing task details and range
     */
    void crack(CrackRequest request);
}