package com.example.master.service;

import com.example.master.client.MinionClient;
import com.example.master.dto.MinionRequest;
import com.example.master.entity.SubTaskEntity;
import com.example.master.entity.TaskEntity;
import com.example.master.enums.SubTaskStatus;
import com.example.master.enums.TaskStatus;
import com.example.master.repository.SubTaskRepository;
import com.example.master.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static com.example.master.common.AppConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskReassignmentService {

    private final SubTaskRepository subTaskRepository;
    private final TaskRepository taskRepository;
    private final BatchManager batchManager;
    private final MinionClient minionClient;
    private final ExecutorService minionDispatchExecutor;

    @Transactional
    public void reassignTimeoutTasks() {

        List<SubTaskEntity> timeoutTasks = subTaskRepository.findByStatus(SubTaskStatus.TIMEOUT);
        for (SubTaskEntity timeoutTask : timeoutTasks) {
            reassignOne(timeoutTask);
        }
    }

    private void reassignOne(SubTaskEntity subTask) {

        TaskEntity task = taskRepository.findById(subTask.getTaskId())
                .orElseThrow();

        if (subTask.getRetryCount() > MAX_RETRY_COUNT) {
            log.error("SubTask {} exceeded max retries → marking FAILED",
                    subTask.getSubTaskId());

            subTask.setStatus(SubTaskStatus.FAILED);
            subTaskRepository.save(subTask);
            task.setStatus(TaskStatus.FAILED);
            taskRepository.save(task);
            batchManager.finalizeTask(task);
            return;
        }

        int available = minionClient.getMinionCount();
        if (available == 0) {
            log.warn("No minions available → keeping SubTask {} in TIMEOUT",
                    subTask.getSubTaskId());
            return;
        }

        int chosen = (int) (System.currentTimeMillis() % available);

        MinionRequest request = new MinionRequest(
                subTask.getSubTaskId().toString(),
                task.getHashValue(),
                subTask.getRangeStart(),
                subTask.getRangeEnd()
        );

        minionDispatchExecutor.submit(() -> {
            try {
                MDC.put("subTaskId", subTask.getSubTaskId().toString());
                log.info("Reassigning SubTask {} to minion {}",
                        subTask.getSubTaskId(), chosen);

                minionClient.sendCrackRequest(chosen, request);

            } catch (Exception e) {
                log.error("Dispatch failed for SubTask {} → setting TIMEOUT again",
                        subTask.getSubTaskId());

                subTask.setStatus(SubTaskStatus.TIMEOUT);
                subTaskRepository.save(subTask);

            } finally {
                MDC.remove("subTaskId");
            }
        });
    }
}
