package com.example.master.service;

import com.example.master.entity.BatchEntity;
import com.example.master.entity.TaskEntity;
import com.example.master.repository.BatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
        if (STATUS_COMPLETED.equals(batch.getStatus()) ||
                STATUS_FAILED.equals(batch.getStatus())) {
            log.info("Batch {} already finalized with status {}. Skipping.",
                    batch.getBatchId(), batch.getStatus());
            return;
        }

        // If task failed → batch should fail immediately
        if (STATUS_FAILED.equals(task.getStatus())) {
            log.warn("Task {} FAILED. Marking batch {} as FAILED.",
                    task.getTaskId(), batch.getBatchId());

            batch.setStatus(STATUS_FAILED);
            batchRepository.save(batch);

            log.info("Batch {} updated successfully.", batch.getBatchId());
            return;
        }

        int updatedCompletedTasks = batch.getCompletedTasks() + 1;
        batch.setCompletedTasks(updatedCompletedTasks);

        log.debug("Batch {} progress: {}/{}",
                batch.getBatchId(), updatedCompletedTasks, batch.getTotalTasks());

        if (updatedCompletedTasks >= batch.getTotalTasks()) {
            log.info("Batch {} is fully completed. Marking as COMPLETED", batch.getBatchId());
            batch.setStatus(STATUS_COMPLETED);
        }

        batchRepository.save(batch);
        log.info("Batch {} updated successfully.", batch.getBatchId());
    }
}
