package com.example.master.service;

import com.example.master.dto.BatchResponse;
import com.example.master.dto.ResultRequest;
import com.example.master.dto.TaskResponse;
import com.example.master.entity.BatchEntity;
import com.example.master.repository.BatchRepository;
import com.example.master.repository.TaskRepository;
import com.example.master.validator.HashFileValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static com.example.master.common.AppConstants.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MasterServiceImpl implements MasterService {

    private final BatchRepository batchRepository;
    private final TaskRepository taskRepository;
    private final HashFileValidator hashFileValidator;
    private final TaskOrchestrator taskOrchestrator;
    private final ResultProcessor resultProcessor;
    private final ExecutorService minionDispatchExecutor;

    @Override
    public BatchResponse submitHashes(MultipartFile file) throws IOException {
        List<String> validHashes = hashFileValidator.validateAndExtractHashes(file);

        UUID batchId = UUID.randomUUID();
        MDC.put("batchId", batchId.toString());
        
        BatchEntity batch = new BatchEntity(batchId, LocalDateTime.now(), STATUS_RUNNING, validHashes.size(), ZERO);
        batchRepository.save(batch);

        List<String> taskIds = new ArrayList<>();
        for (String hash : validHashes) {
            String taskId = taskOrchestrator.createAndDispatchTask(batchId, hash);
            taskIds.add(taskId);
        }

        return new BatchResponse(batchId.toString(), taskIds, taskIds.size(), STATUS_SUBMITTED);
    }

    @Override
    @Transactional
    public void processResult(ResultRequest resultRequest) {
        resultProcessor.processResult(resultRequest);
    }

    @Override
    public TaskResponse getTaskStatus(String taskId) {
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new IllegalArgumentException("TaskId cannot be null or empty");
        }
        
        UUID uuid;
        try {
            uuid = UUID.fromString(taskId);
        } catch (IllegalArgumentException ex) {
            log.warn("Invalid taskId format received: {}", taskId);
            throw new IllegalArgumentException("Invalid taskId format: " + taskId);
        }

        return taskRepository.findById(uuid)
                .map(task -> new TaskResponse(
                        task.getTaskId().toString(),
                        task.getHashValue(),
                        task.getStatus(),
                        task.getFoundPassword()
                ))
                .orElse(null);
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down minion dispatch executor");
        minionDispatchExecutor.shutdown();
        try {
            if (!minionDispatchExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                log.warn("Executor did not terminate gracefully, forcing shutdown");
                minionDispatchExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("Interrupted while waiting for executor shutdown");
            minionDispatchExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}