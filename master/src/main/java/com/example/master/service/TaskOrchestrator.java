package com.example.master.service;

import com.example.master.client.MinionClient;
import com.example.master.dto.MinionRequest;
import com.example.master.entity.SubTaskEntity;
import com.example.master.entity.TaskEntity;
import com.example.master.enums.SubTaskStatus;
import com.example.master.enums.TaskStatus;
import com.example.master.model.Range;
import com.example.master.repository.SubTaskRepository;
import com.example.master.repository.TaskRepository;
import com.example.master.util.RangeSplitter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import static com.example.master.common.AppConstants.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskOrchestrator {

    private final TaskRepository taskRepository;
    private final SubTaskRepository subTaskRepository;
    private final MinionClient minionClient;
    private final ExecutorService minionDispatchExecutor;

    public String createAndDispatchTask(UUID batchId, String hash) {
        int minionCount = minionClient.getMinionCount();
        if (minionCount == ZERO) {
            throw new IllegalStateException(ERROR_NO_MINIONS_CONFIGURED);
        }

        TaskEntity task = createTask(batchId, hash, minionCount);
        List<Range> ranges = RangeSplitter.splitRange(PHONE_RANGE_START, PHONE_RANGE_END, minionCount);
        List<SubTaskEntity> subTasks = createSubTasks(task.getTaskId(), ranges);
        dispatchToMinions(subTasks, hash);

        return task.getTaskId().toString();
    }

    private TaskEntity createTask(UUID batchId, String hash, int minionCount) {
        UUID taskId = UUID.randomUUID();
        TaskEntity task = new TaskEntity(taskId, batchId, hash, TaskStatus.RUNNING, null, minionCount, ZERO);
        taskRepository.save(task);
        return task;
    }

    private List<SubTaskEntity> createSubTasks(UUID taskId, List<Range> ranges) {
        List<SubTaskEntity> subTasks = new ArrayList<>();
        for (Range range : ranges) {
            UUID subTaskId = UUID.randomUUID();
            SubTaskEntity subTask = new SubTaskEntity(subTaskId, taskId, range.getRangeFrom(), 
                range.getRangeTo(), SubTaskStatus.RUNNING, null, LocalDateTime.now(), 0);
            subTaskRepository.save(subTask);
            subTasks.add(subTask);
        }
        return subTasks;
    }

    private void dispatchToMinions(List<SubTaskEntity> subTasks, String hash) {
        for (int i = 0; i < subTasks.size(); i++) {
            SubTaskEntity subTask = subTasks.get(i);
            MinionRequest request = new MinionRequest(subTask.getSubTaskId().toString(), 
                hash, subTask.getRangeStart(), subTask.getRangeEnd());
            int minionIndex = i;

            minionDispatchExecutor.submit(() -> {
                try {
                    MDC.put("subTaskId", subTask.getSubTaskId().toString());
                    retryDispatch(minionIndex, request, 3);
                } finally {
                    MDC.remove("subTaskId");
                }
            });
        }
    }

    private void retryDispatch(int minionIndex, MinionRequest request, int maxRetries) {
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                minionClient.sendCrackRequest(minionIndex, request);
                return;
            } catch (Exception e) {
                log.warn("Dispatch attempt {} failed for subtask {} to minion {}: {}", 
                    attempt, request.getTaskId(), minionIndex, e.getMessage());
                
                if (attempt == maxRetries) {
                    log.error("All {} dispatch attempts failed for subtask {} to minion {}", 
                        maxRetries, request.getTaskId(), minionIndex);
                    return;
                }
                
                try {
                    Thread.sleep(1000 * attempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }
}