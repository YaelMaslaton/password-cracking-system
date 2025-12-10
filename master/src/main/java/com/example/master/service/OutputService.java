package com.example.master.service;

import com.example.master.common.AppConstants;
import com.example.master.entity.TaskEntity;
import com.example.master.enums.TaskStatus;
import com.example.master.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutputService {

    private final TaskRepository taskRepository;

    public Resource generateBatchResultsFile(String batchId) {
        UUID uuid;
        try {
            uuid = UUID.fromString(batchId);
        } catch (IllegalArgumentException ex) {
            log.warn("Invalid batch ID format received: {}", batchId);
            throw new IllegalArgumentException("Invalid batch ID format. Expected UUID format like: 123e4567-e89b-12d3-a456-426614174000");
        }

        List<TaskEntity> tasks = taskRepository.findByBatchId(uuid);
        if (tasks.isEmpty()) {
            throw new IllegalArgumentException("Batch not found: " + batchId);
        }

        StringBuilder content = new StringBuilder();
        content.append(AppConstants.HEADER_RESULTS);
        content.append(AppConstants.HEADER_BATCH_ID).append(batchId).append("\n");
        content.append(AppConstants.HEADER_TOTAL_TASKS).append(tasks.size()).append("\n\n");

        for (TaskEntity task : tasks) {
            content.append(task.getHashValue()).append(":");
            
            if (task.getStatus() == TaskStatus.FOUND) {
                content.append(task.getFoundPassword());
            } else if (task.getStatus() == TaskStatus.FAILED) {
                content.append(AppConstants.RESULT_FAILED_SERVER_CRASH);
            } else if (task.getStatus() == TaskStatus.RUNNING) {
                content.append("RUNNING");
            } else {
                content.append("NOT_FOUND");
            }
            content.append("\n");
        }

        byte[] data = content.toString().getBytes();
        return new ByteArrayResource(data);
    }
}