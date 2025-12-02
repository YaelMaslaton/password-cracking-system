package com.example.master.service;

import com.example.master.client.MinionClient;
import com.example.master.dto.MinionRequest;
import com.example.master.entity.SubTaskEntity;
import com.example.master.entity.TaskEntity;
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
    private final MinionClient minionClient;
    private final ExecutorService minionDispatchExecutor;

    @Transactional
    public void reassignTimeoutTasks() {
        List<SubTaskEntity> timeoutTasks = subTaskRepository.findByStatus(STATUS_TIMEOUT);
        
        for (SubTaskEntity timeoutTask : timeoutTasks) {
            reassignTask(timeoutTask);
        }
    }

    private void reassignTask(SubTaskEntity timeoutTask) {
        int availableMinions = minionClient.getMinionCount();
        if (availableMinions == 0) {
            log.warn("No minions available for reassignment of task {}", timeoutTask.getSubTaskId());
            return;
        }

        int newMinionIndex = (int) (System.currentTimeMillis() % availableMinions);
        
        timeoutTask.setStatus(STATUS_RUNNING);
        timeoutTask.setStartedAt(LocalDateTime.now());
        subTaskRepository.save(timeoutTask);

        MinionRequest request = new MinionRequest(
            timeoutTask.getSubTaskId().toString(),
            getHashForTask(timeoutTask),
            timeoutTask.getRangeStart(),
            timeoutTask.getRangeEnd()
        );

        minionDispatchExecutor.submit(() -> {
            try {
                MDC.put("subTaskId", timeoutTask.getSubTaskId().toString());
                log.info("Reassigning timeout task {} to minion {}", timeoutTask.getSubTaskId(), newMinionIndex);
                minionClient.sendCrackRequest(newMinionIndex, request);
            } catch (Exception e) {
                log.error("Failed to reassign task {} to minion {}: {}", 
                    timeoutTask.getSubTaskId(), newMinionIndex, e.getMessage());
                timeoutTask.setStatus(STATUS_TIMEOUT);
                subTaskRepository.save(timeoutTask);
            } finally {
                MDC.remove("subTaskId");
            }
        });
    }

    private String getHashForTask(SubTaskEntity subTask) {
        TaskEntity task = taskRepository.findById(subTask.getTaskId()).orElseThrow();
        return task.getHashValue();
    }
}