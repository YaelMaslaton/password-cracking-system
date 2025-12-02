package com.example.master.service;

import com.example.master.entity.SubTaskEntity;
import com.example.master.entity.TaskEntity;
import com.example.master.repository.SubTaskRepository;
import com.example.master.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.example.master.common.AppConstants.*;

/**
 * Service for handling subtask timeouts.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TimeoutService {

    private final SubTaskRepository subTaskRepository;
    private final TaskRepository taskRepository;
    private final TaskReassignmentService taskReassignmentService;

    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    public void handleTimeouts() {
        LocalDateTime timeoutThreshold = LocalDateTime.now().minusMinutes(SUBTASK_TIMEOUT_MINUTES);
        
        List<SubTaskEntity> timedOutSubTasks = subTaskRepository.findByStatusAndStartedAtBefore(STATUS_RUNNING, timeoutThreshold);
        
        for (SubTaskEntity subTask : timedOutSubTasks) {
            log.warn("SubTask {} timed out after {} minutes", subTask.getSubTaskId(), SUBTASK_TIMEOUT_MINUTES);
            
            subTask.setStatus(STATUS_TIMEOUT);
            subTaskRepository.save(subTask);
        }
        
        if (!timedOutSubTasks.isEmpty()) {
            taskReassignmentService.reassignTimeoutTasks();
        }
    }
}