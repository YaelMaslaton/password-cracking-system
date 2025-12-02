package com.example.master.service;

import com.example.master.entity.TaskEntity;
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
        content.append("# Password Cracking Results\n");
        content.append("# Batch ID: ").append(batchId).append("\n");
        content.append("# Total Tasks: ").append(tasks.size()).append("\n\n");

        for (TaskEntity task : tasks) {
            content.append(task.getHashValue()).append(":");
            
            if ("FOUND".equals(task.getStatus())) {
                content.append(task.getFoundPassword());
            } else if ("FAILED".equals(task.getStatus())) {
                content.append("FAILED_SERVER_CRASH");
            } else {
                content.append("NOT_FOUND");
            }
            content.append("\n");
        }

        byte[] data = content.toString().getBytes();
        return new ByteArrayResource(data);
    }
}