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

        int updatedCompletedTasks = batch.getCompletedTasks() + 1;
        batch.setCompletedTasks(updatedCompletedTasks);
        
        log.debug("Batch {} progress: {}/{}", batch.getBatchId(), updatedCompletedTasks, batch.getTotalTasks());

        if (updatedCompletedTasks >= batch.getTotalTasks()) {
            log.info("Batch {} is fully completed. Marking as COMPLETED", batch.getBatchId());
            batch.setStatus(STATUS_COMPLETED);
        }
        
        batchRepository.save(batch);
        log.info("Batch {} updated successfully.", batch.getBatchId());
    }
}