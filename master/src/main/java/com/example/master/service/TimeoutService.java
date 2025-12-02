package com.example.master.service;

import com.example.master.entity.SubTaskEntity;

import com.example.master.repository.SubTaskRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


import static com.example.master.common.AppConstants.*;

/**
 * Service for handling subtask timeouts.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TimeoutService {

    private final SubTaskRepository subTaskRepository;
    private final TaskReassignmentService taskReassignmentService;

    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    public void handleTimeouts() {
        LocalDateTime timeoutThreshold = LocalDateTime.now().minusMinutes(SUBTASK_TIMEOUT_MINUTES);

        // Find RUNNING tasks that timed out
        List<SubTaskEntity> runningTimedOut = subTaskRepository.findByStatusAndStartedAtBefore(STATUS_RUNNING, timeoutThreshold);
        
        List<SubTaskEntity> timeoutRetryable = subTaskRepository.findByStatus(STATUS_TIMEOUT);
        runningTimedOut.addAll(timeoutRetryable);

        // Process running tasks that timed out
        for (SubTaskEntity subTask : runningTimedOut) {
            log.warn("SubTask {} timed out after {} minutes", subTask.getSubTaskId(), SUBTASK_TIMEOUT_MINUTES);
            
            subTask.setStatus(STATUS_TIMEOUT);
            subTask.setRetryCount(subTask.getRetryCount() + 1);
            subTaskRepository.save(subTask);
        }

        if (!runningTimedOut.isEmpty() || !timeoutRetryable.isEmpty()) {
            taskReassignmentService.reassignTimeoutTasks();
        }
    }
}