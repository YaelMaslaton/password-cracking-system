package com.example.master.service;

import com.example.master.entity.BatchEntity;
import com.example.master.entity.TaskEntity;
import com.example.master.enums.BatchStatus;
import com.example.master.enums.TaskStatus;
import com.example.master.repository.BatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.example.master.common.AppConstants.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchManager {

    private final BatchRepository batchRepository;

    @Transactional
    public void finalizeTask(TaskEntity task) {

        log.info("Finalizing task {} in batch {}", task.getTaskId(), task.getBatchId());

        BatchEntity batch = batchRepository.findById(task.getBatchId())
                .orElseThrow(() -> new IllegalStateException("Batch not found: " + task.getBatchId()));

        // If batch already completed/failed → do nothing
        if (batch.getStatus() == BatchStatus.COMPLETED ||
                batch.getStatus() == BatchStatus.FAILED) {
            log.info("Batch {} already finalized with status {}. Skipping.",
                    batch.getBatchId(), batch.getStatus());
            return;
        }

        // If task failed → batch should fail immediately
        if (task.getStatus() == TaskStatus.FAILED) {
            log.warn("Task {} FAILED. Marking batch {} as FAILED.",
                    task.getTaskId(), batch.getBatchId());

            batch.setStatus(BatchStatus.FAILED);
            batchRepository.save(batch);

            log.info("Batch {} updated successfully.", batch.getBatchId());
            return;
        }

        UUID batchId = batch.getBatchId();
        batchRepository.incrementCompletedTasks(batchId);
        
        batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new IllegalStateException("Batch not found after increment: " + batchId));

        log.debug("Batch {} progress: {}/{}",
                batchId, batch.getCompletedTasks(), batch.getTotalTasks());

        if (batch.getCompletedTasks() >= batch.getTotalTasks()) {
            log.info("Batch {} is fully completed. Marking as COMPLETED", batchId);
            batch.setStatus(BatchStatus.COMPLETED);
            batchRepository.save(batch);
        }
        log.info("Batch {} updated successfully.", batch.getBatchId());
    }
}
