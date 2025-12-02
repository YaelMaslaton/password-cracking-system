package com.example.master.service;

import com.example.master.dto.BatchResponse;
import com.example.master.dto.ResultRequest;
import com.example.master.dto.TaskResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Service interface for orchestrating the password cracking workflow.
 * This service coordinates the distribution of password cracking tasks
 * among multiple Minion services and manages the collection of results.
 */
public interface MasterService {

    /**
     * Processes uploaded file containing MD5 hashes and creates password cracking tasks.
     * The file is validated, hashes are extracted, and work is distributed among available minions.
     *
     * @param file Multipart file containing MD5 hashes (one per line)
     * @return BatchResponse containing batch ID, task IDs, count, and status
     * @throws IOException if file processing fails
     * @throws IllegalArgumentException if file validation fails
     */
    BatchResponse submitHashes(MultipartFile file) throws IOException;

    /**
     * Processes results received from Minion services.
     * Updates subtask and task status, handles password found scenarios,
     * and manages batch completion tracking.
     *
     * @param resultRequest Result data from minion service containing task ID, status, and optional password
     * @throws IllegalArgumentException if subtask is not found
     */
    void processResult(ResultRequest resultRequest);

    /**
     * Retrieves the current status of a specific password cracking task.
     *
     * @param taskId Unique identifier of the task to query
     * @return TaskResponse containing task details and current status, or null if task not found
     */
    TaskResponse getTaskStatus(String taskId);
}